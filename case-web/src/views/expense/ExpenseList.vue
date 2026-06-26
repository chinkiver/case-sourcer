<template>
  <div class="expense-list">
    <el-card shadow="never">
      <div class="search-bar">
        <el-select v-model="filter.lawyerId" placeholder="选择律师" clearable filterable style="width: 180px">
          <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="filter.expenseType" placeholder="支出类型" clearable style="width: 160px">
          <el-option v-for="item in expenseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="filter.expenseMonth" placeholder="支出月份 yyyy-MM" clearable style="width: 160px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增支出</el-button>
      </div>

      <el-table :data="expenseList" v-loading="loading" stripe border>
        <el-table-column prop="lawyerName" label="律师" width="120" />
        <el-table-column prop="expenseDate" label="支出日期" width="110" />
        <el-table-column prop="expenseMonth" label="支出月份" width="100" />
        <el-table-column prop="expenseType" label="支出类型" width="120" />
        <el-table-column prop="amount" label="金额" width="130" align="right">
          <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
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

    <el-dialog title="新增支出" v-model="dialogVisible" width="520px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="律师" prop="lawyerId">
          <el-select v-model="form.lawyerId" placeholder="选择律师" filterable style="width: 100%">
            <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="支出日期" prop="expenseDate">
          <el-date-picker v-model="form.expenseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支出类型" prop="expenseType">
          <el-select v-model="form.expenseType" placeholder="选择类型" style="width: 100%">
            <el-option v-for="item in expenseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { reactive, ref, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExpenseList, createExpense, deleteExpense, expenseTypeOptions, type Expense, type ExpenseForm } from '@/api/expense'
import { getLawyerList } from '@/api/project'

const loading = ref(false)
const expenseList = ref<Expense[]>([])
const lawyerList = ref<any[]>([])

const filter = reactive({
  lawyerId: undefined as number | undefined,
  expenseType: '',
  expenseMonth: '',
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive<ExpenseForm>({
  lawyerId: undefined,
  expenseDate: '',
  expenseType: '其他支出',
  amount: 0,
  remark: '',
})

const rules = {
  lawyerId: [{ required: true, message: '请选择律师', trigger: 'change' }],
  expenseDate: [{ required: true, message: '请选择支出日期', trigger: 'change' }],
  expenseType: [{ required: true, message: '请选择支出类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
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
    if (filter.expenseType) params.expenseType = filter.expenseType
    if (filter.expenseMonth) params.expenseMonth = filter.expenseMonth
    const res: any = await getExpenseList(params)
    expenseList.value = res.records
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
  filter.expenseType = ''
  filter.expenseMonth = ''
  handleSearch()
}

const handleAdd = () => {
  form.lawyerId = undefined
  form.expenseDate = new Date().toISOString().split('T')[0]
  form.expenseType = '其他支出'
  form.amount = 0
  form.remark = ''
  dialogVisible.value = true
}

const handleDelete = async (row: Expense) => {
  try {
    await ElMessageBox.confirm(`确定删除这条【${row.expenseType}】支出记录吗？`, '提示', { type: 'warning' })
    await deleteExpense(row.id)
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
    await createExpense(form)
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
  lawyerList.value = await getLawyerList()
  loadData()
})
</script>

<style scoped>
.expense-list {
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
