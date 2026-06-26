<template>
  <div class="archive-list">
    <el-card shadow="never">
      <div class="search-bar">
        <el-select v-model="filter.lawyerId" placeholder="选择律师" clearable filterable style="width: 180px">
          <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="filter.archiveStatus" placeholder="归档状态" clearable style="width: 160px">
          <el-option label="已归档" :value="1" />
          <el-option label="未归档" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="FolderChecked" @click="handleArchive">项目归档</el-button>
      </div>

      <el-table :data="archiveList" v-loading="loading" stripe border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="projectNo" label="项目编号" width="150" />
        <el-table-column prop="projectName" label="项目/案件名称" min-width="180" />
        <el-table-column prop="lawyerName" label="归属律师" width="120" />
        <el-table-column prop="feeAmount" label="档案费" width="120" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.feeAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="archiveStatus" label="归档状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.archiveStatus === 1 ? 'success' : 'info'">
              {{ row.archiveStatus === 1 ? '已归档' : '未归档' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="archiveDate" label="归档日期" width="110" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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

    <ArchiveDialog v-model:visible="dialogVisible" @success="loadData" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { Search, FolderChecked } from '@element-plus/icons-vue'
import { getArchiveFeeList, type ArchiveFee } from '@/api/archiveFee'
import { getLawyerList } from '@/api/project'
import ArchiveDialog from './ArchiveDialog.vue'

const loading = ref(false)
const archiveList = ref<ArchiveFee[]>([])
const lawyerList = ref<any[]>([])
const dialogVisible = ref(false)

const filter = reactive({
  lawyerId: undefined as number | undefined,
  archiveStatus: undefined as number | undefined,
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
    }
    if (filter.lawyerId) params.lawyerId = filter.lawyerId
    if (filter.archiveStatus !== undefined) params.archiveStatus = filter.archiveStatus
    const res: any = await getArchiveFeeList(params)
    archiveList.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleArchive = () => {
  dialogVisible.value = true
}

onMounted(async () => {
  lawyerList.value = await getLawyerList()
  loadData()
})
</script>

<style scoped>
.archive-list {
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
