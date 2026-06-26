<template>
  <div class="project-list">
    <el-card shadow="never">
      <div class="search-bar">
        <el-input v-model="searchForm.keyword" placeholder="项目编号/项目名称" clearable style="width: 260px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd" v-if="userStore.hasPermission('project:add')">新增项目</el-button>
      </div>

      <el-table :data="projectList" v-loading="loading" stripe border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="projectNo" label="项目编号" width="150" />
        <el-table-column prop="projectName" label="项目/案件名称" min-width="180" />
        <el-table-column prop="businessType" label="业务类别" width="100">
          <template #default="{ row }">
            {{ businessTypeMap[row.businessType] || row.businessType }}
          </template>
        </el-table-column>
        <el-table-column prop="clientName" label="委托人" width="120" />
        <el-table-column prop="hostLawyerName" label="主办律师" width="120" />
        <el-table-column prop="assistLawyerName" label="协办律师" width="120" />
        <el-table-column prop="caseDate" label="收案日期" width="110" />
        <el-table-column prop="contractAmount" label="合同金额" width="120" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.contractAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="receivedAmount" label="回款金额" width="120" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.receivedAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="receiveRatio" label="回款比例" width="100" align="right">
          <template #default="{ row }">
            {{ row.receiveRatio }}%
          </template>
        </el-table-column>
        <el-table-column prop="archiveStatus" label="归档" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.archiveStatus === 1 ? 'success' : 'info'">
              {{ row.archiveStatus === 1 ? '已归档' : '未归档' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleIncome(row)">登记收入</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="userStore.hasPermission('project:edit')">删除</el-button>
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

    <ProjectFormDialog
      v-model:visible="dialogVisible"
      :data="currentRow"
      @success="loadData"
    />

    <IncomeDialog
      v-model:visible="incomeVisible"
      :project="currentRow"
      @success="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getProjectList, deleteProject, type Project } from '@/api/project'
import { useUserStore } from '@/stores/user'
import ProjectFormDialog from './ProjectFormDialog.vue'
import IncomeDialog from './IncomeDialog.vue'

const userStore = useUserStore()
const loading = ref(false)
const projectList = ref<Project[]>([])
const dialogVisible = ref(false)
const incomeVisible = ref(false)
const currentRow = ref<Project | undefined>(undefined)

const searchForm = reactive({
  keyword: '',
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const businessTypeMap: Record<string, string> = {
  MS: '民事诉讼',
  XS: '刑事诉讼',
  XZ: '行政诉讼',
  LD: '劳动仲裁',
  SS: '商事仲裁',
  GW: '法律顾问',
  ZX: '专项服务',
  FY: '行政复议',
  QT: '其他非诉',
  MZ: '民事执行',
}

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getProjectList({
      keyword: searchForm.keyword,
      page: pagination.page,
      size: pagination.size,
    })
    projectList.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  currentRow.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: Project) => {
  currentRow.value = row
  dialogVisible.value = true
}

const handleIncome = (row: Project) => {
  currentRow.value = row
  incomeVisible.value = true
}

const handleDelete = (row: Project) => {
  ElMessageBox.confirm('确认删除该项目？删除后不可恢复', '提示', { type: 'warning' })
    .then(async () => {
      await deleteProject(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.project-list {
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
</style>
