<template>
  <div class="social-config">
    <el-card shadow="never">
      <div class="search-bar">
        <el-input-number v-model="filter.year" :min="2000" :max="2100" placeholder="年度" style="width: 120px" />
        <el-input-number v-model="filter.month" :min="1" :max="12" placeholder="月份" style="width: 120px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增配置</el-button>
      </div>

      <el-table :data="configList" v-loading="loading" stripe border>
        <el-table-column prop="year" label="年度" width="80" align="center" />
        <el-table-column prop="month" label="月份" width="80" align="center" />
        <el-table-column prop="baseAmount" label="缴费基数" width="140" align="right">
          <template #default="{ row }">{{ formatMoney(row.baseAmount) }}</template>
        </el-table-column>
        <el-table-column label="个人比例" align="center">
          <el-table-column prop="pensionRatePersonal" label="养老" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.pensionRatePersonal) }}</template>
          </el-table-column>
          <el-table-column prop="medicalRatePersonal" label="医疗" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.medicalRatePersonal) }}</template>
          </el-table-column>
          <el-table-column prop="unemploymentRatePersonal" label="失业" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.unemploymentRatePersonal) }}</template>
          </el-table-column>
          <el-table-column prop="housingRatePersonal" label="公积金" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.housingRatePersonal) }}</template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="单位比例" align="center">
          <el-table-column prop="pensionRateCompany" label="养老" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.pensionRateCompany) }}</template>
          </el-table-column>
          <el-table-column prop="medicalRateCompany" label="医疗" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.medicalRateCompany) }}</template>
          </el-table-column>
          <el-table-column prop="unemploymentRateCompany" label="失业" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.unemploymentRateCompany) }}</template>
          </el-table-column>
          <el-table-column prop="injuryRateCompany" label="工伤" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.injuryRateCompany) }}</template>
          </el-table-column>
          <el-table-column prop="maternityRateCompany" label="生育" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.maternityRateCompany) }}</template>
          </el-table-column>
          <el-table-column prop="housingRateCompany" label="公积金" width="90" align="right">
            <template #default="{ row }">{{ formatRate(row.housingRateCompany) }}</template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="680px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="130px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年度" prop="year">
              <el-input-number v-model="form.year" :min="2000" :max="2100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月份" prop="month">
              <el-input-number v-model="form.month" :min="1" :max="12" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="缴费基数" prop="baseAmount">
          <el-input-number v-model="form.baseAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-divider content-position="left">个人比例</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老">
              <el-input-number v-model="form.pensionRatePersonal" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗">
              <el-input-number v-model="form.medicalRatePersonal" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失业">
              <el-input-number v-model="form.unemploymentRatePersonal" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公积金">
              <el-input-number v-model="form.housingRatePersonal" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">单位比例</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老">
              <el-input-number v-model="form.pensionRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗">
              <el-input-number v-model="form.medicalRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失业">
              <el-input-number v-model="form.unemploymentRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工伤">
              <el-input-number v-model="form.injuryRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生育">
              <el-input-number v-model="form.maternityRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公积金">
              <el-input-number v-model="form.housingRateCompany" :min="0" :max="1" :precision="4" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSocialInsuranceConfigList,
  createSocialInsuranceConfig,
  updateSocialInsuranceConfig,
  deleteSocialInsuranceConfig,
  type SocialInsuranceConfig,
  type SocialInsuranceConfigForm,
} from '@/api/socialInsurance'

const loading = ref(false)
const configList = ref<SocialInsuranceConfig[]>([])

const filter = reactive({
  year: undefined as number | undefined,
  month: undefined as number | undefined,
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const dialogTitle = ref('新增配置')
const submitting = ref(false)
const formRef = ref()

const form = reactive<SocialInsuranceConfigForm>({
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1,
  baseAmount: 5000,
  pensionRatePersonal: 0.08,
  pensionRateCompany: 0.16,
  medicalRatePersonal: 0.02,
  medicalRateCompany: 0.09,
  unemploymentRatePersonal: 0.005,
  unemploymentRateCompany: 0.005,
  injuryRateCompany: 0.005,
  maternityRateCompany: 0.008,
  housingRatePersonal: 0.05,
  housingRateCompany: 0.05,
})

const rules = {
  year: [{ required: true, message: '请输入年度', trigger: 'blur' }],
  month: [{ required: true, message: '请输入月份', trigger: 'blur' }],
  baseAmount: [{ required: true, message: '请输入缴费基数', trigger: 'blur' }],
}

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatRate = (value?: number) => {
  if (value == null) return '0.00%'
  return (Number(value) * 100).toFixed(2) + '%'
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = { page: pagination.page, size: pagination.size }
    if (filter.year) params.year = filter.year
    if (filter.month) params.month = filter.month
    const res: any = await getSocialInsuranceConfigList(params)
    configList.value = res.records
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
  filter.year = undefined
  filter.month = undefined
  handleSearch()
}

const resetForm = () => {
  form.year = new Date().getFullYear()
  form.month = new Date().getMonth() + 1
  form.baseAmount = 5000
  form.pensionRatePersonal = 0.08
  form.pensionRateCompany = 0.16
  form.medicalRatePersonal = 0.02
  form.medicalRateCompany = 0.09
  form.unemploymentRatePersonal = 0.005
  form.unemploymentRateCompany = 0.005
  form.injuryRateCompany = 0.005
  form.maternityRateCompany = 0.008
  form.housingRatePersonal = 0.05
  form.housingRateCompany = 0.05
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  dialogTitle.value = '新增配置'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: SocialInsuranceConfig) => {
  isEdit.value = true
  currentId.value = row.id
  dialogTitle.value = '编辑配置'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: SocialInsuranceConfig) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.year}年${row.month}月 的社保公积金配置吗？`, '提示', { type: 'warning' })
    await deleteSocialInsuranceConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value && currentId.value) {
      await updateSocialInsuranceConfig(currentId.value, form)
    } else {
      await createSocialInsuranceConfig(form)
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.social-config {
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
