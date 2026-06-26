<template>
  <div class="social-record">
    <el-card shadow="never">
      <div class="search-bar">
        <el-select v-model="filter.lawyerId" placeholder="选择律师" clearable filterable style="width: 180px">
          <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-input-number v-model="filter.year" :min="2000" :max="2100" placeholder="年度" style="width: 120px" />
        <el-input-number v-model="filter.month" :min="1" :max="12" placeholder="月份" style="width: 120px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button type="success" :icon="Money" @click="handleGenerate">生成扣缴</el-button>
      </div>

      <el-table :data="recordList" v-loading="loading" stripe border>
        <el-table-column prop="lawyerName" label="律师" width="120" />
        <el-table-column prop="year" label="年度" width="80" align="center" />
        <el-table-column prop="month" label="月份" width="80" align="center" />
        <el-table-column prop="baseAmount" label="缴费基数" width="130" align="right">
          <template #default="{ row }">{{ formatMoney(row.baseAmount) }}</template>
        </el-table-column>
        <el-table-column label="个人缴纳" align="center">
          <el-table-column prop="pensionPersonal" label="养老" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.pensionPersonal) }}</template>
          </el-table-column>
          <el-table-column prop="medicalPersonal" label="医疗" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.medicalPersonal) }}</template>
          </el-table-column>
          <el-table-column prop="unemploymentPersonal" label="失业" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.unemploymentPersonal) }}</template>
          </el-table-column>
          <el-table-column prop="housingPersonal" label="公积金" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.housingPersonal) }}</template>
          </el-table-column>
          <el-table-column prop="totalPersonal" label="合计" width="120" align="right">
            <template #default="{ row }">{{ formatMoney(row.totalPersonal) }}</template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="单位缴纳" align="center">
          <el-table-column prop="pensionCompany" label="养老" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.pensionCompany) }}</template>
          </el-table-column>
          <el-table-column prop="medicalCompany" label="医疗" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.medicalCompany) }}</template>
          </el-table-column>
          <el-table-column prop="unemploymentCompany" label="失业" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.unemploymentCompany) }}</template>
          </el-table-column>
          <el-table-column prop="injuryCompany" label="工伤" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.injuryCompany) }}</template>
          </el-table-column>
          <el-table-column prop="maternityCompany" label="生育" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.maternityCompany) }}</template>
          </el-table-column>
          <el-table-column prop="housingCompany" label="公积金" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.housingCompany) }}</template>
          </el-table-column>
          <el-table-column prop="totalCompany" label="合计" width="120" align="right">
            <template #default="{ row }">{{ formatMoney(row.totalCompany) }}</template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
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

    <el-dialog title="生成社保公积金扣缴" v-model="generateVisible" width="420px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="generateForm" :rules="generateRules" ref="generateFormRef" label-width="80px">
        <el-form-item label="年度" prop="year">
          <el-input-number v-model="generateForm.year" :min="2000" :max="2100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-input-number v-model="generateForm.month" :min="1" :max="12" style="width: 100%" />
        </el-form-item>
        <el-form-item label="律师">
          <el-select v-model="generateForm.lawyerIds" multiple collapse-tags placeholder="全部律师" style="width: 100%">
            <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="submitGenerate">确认生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { Search, Money } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSocialInsuranceRecordList,
  generateSocialInsuranceRecords,
  deleteSocialInsuranceRecord,
  type SocialInsuranceRecord,
} from '@/api/socialInsurance'
import { getLawyerList } from '@/api/project'

const loading = ref(false)
const recordList = ref<SocialInsuranceRecord[]>([])
const lawyerList = ref<any[]>([])

const filter = reactive({
  lawyerId: undefined as number | undefined,
  year: undefined as number | undefined,
  month: undefined as number | undefined,
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const generateVisible = ref(false)
const generating = ref(false)
const generateFormRef = ref()
const generateForm = reactive({
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1,
  lawyerIds: [] as number[],
})

const generateRules = {
  year: [{ required: true, message: '请输入年度', trigger: 'blur' }],
  month: [{ required: true, message: '请输入月份', trigger: 'blur' }],
}

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = { page: pagination.page, size: pagination.size }
    if (filter.lawyerId) params.lawyerId = filter.lawyerId
    if (filter.year) params.year = filter.year
    if (filter.month) params.month = filter.month
    const res: any = await getSocialInsuranceRecordList(params)
    recordList.value = res.records
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
  filter.lawyerId = undefined
  filter.year = undefined
  filter.month = undefined
  handleSearch()
}

const handleGenerate = () => {
  generateForm.year = new Date().getFullYear()
  generateForm.month = new Date().getMonth() + 1
  generateForm.lawyerIds = []
  generateVisible.value = true
}

const submitGenerate = async () => {
  try {
    await generateFormRef.value.validate()
    generating.value = true
    const params: any = {
      year: generateForm.year,
      month: generateForm.month,
    }
    if (generateForm.lawyerIds && generateForm.lawyerIds.length > 0) {
      params.lawyerIds = generateForm.lawyerIds
    }
    const count = await generateSocialInsuranceRecords(params)
    ElMessage.success(`成功生成 ${count} 条扣缴记录`)
    generateVisible.value = false
    loadData()
  } catch (error: any) {
    console.error(error)
  } finally {
    generating.value = false
  }
}

const handleDelete = async (row: SocialInsuranceRecord) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.lawyerName} ${row.year}年${row.month}月 的扣缴记录吗？`, '提示', { type: 'warning' })
    await deleteSocialInsuranceRecord(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(async () => {
  lawyerList.value = await getLawyerList()
  loadData()
})
</script>

<style scoped>
.social-record {
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
