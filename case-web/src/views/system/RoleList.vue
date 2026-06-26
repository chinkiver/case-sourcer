<template>
  <div class="role-list">
    <el-card shadow="never">
      <div class="search-bar">
        <el-input v-model="filter.keyword" placeholder="角色编码/名称" clearable style="width: 220px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>

      <el-table :data="roleList" v-loading="loading" stripe border>
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="permissionNames" label="权限" min-width="240">
          <template #default="{ row }">
            <el-tag v-for="name in row.permissionNames.slice(0, 5)" :key="name" size="small" class="perm-tag">{{ name }}</el-tag>
            <el-tag v-if="row.permissionNames.length > 5" size="small" type="info">+{{ row.permissionNames.length - 5 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          :page-sizes="[10, 20, 50]"
          @change="loadData"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <div class="permission-tree-wrapper">
            <el-tree
              ref="treeRef"
              :data="permissionTree"
              show-checkbox
              node-key="id"
              :props="{ label: 'permissionName', children: 'children' }"
              default-expand-all
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getPermissionTree,
  type Role,
  type RoleForm,
  type Permission,
} from '@/api/system'

const loading = ref(false)
const roleList = ref<Role[]>([])
const permissionTree = ref<Permission[]>([])

const filter = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const dialogTitle = ref('新增角色')
const submitting = ref(false)
const formRef = ref()
const treeRef = ref<any>(null)

const form = reactive<RoleForm>({
  roleCode: '',
  roleName: '',
  description: '',
  permissionIds: [],
})

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = { page: pagination.page, size: pagination.size }
    if (filter.keyword) params.keyword = filter.keyword
    const res: any = await getRoleList(params)
    roleList.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const resetFilter = () => {
  filter.keyword = ''
  handleSearch()
}

const resetForm = () => {
  form.roleCode = ''
  form.roleName = ''
  form.description = ''
  form.permissionIds = []
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
  nextTick(() => treeRef.value?.setCheckedKeys([]))
}

const handleEdit = (row: Role) => {
  isEdit.value = true
  currentId.value = row.id
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
  nextTick(() => treeRef.value?.setCheckedKeys(row.permissionIds || []))
}

const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(`确定删除角色【${row.roleName}】吗？`, '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    const data: RoleForm = { ...form }
    data.permissionIds = treeRef.value?.getCheckedKeys(false) || []
    if (isEdit.value && currentId.value) {
      await updateRole(currentId.value, data)
    } else {
      await createRole(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  permissionTree.value = await getPermissionTree()
  loadData()
})
</script>

<style scoped>
.role-list {
  min-height: 100%;
}
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.perm-tag {
  margin-right: 6px;
}
.permission-tree-wrapper {
  max-height: 320px;
  overflow: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}
</style>
