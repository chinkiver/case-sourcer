<template>
  <div class="ledger-view">
    <el-row :gutter="20" class="summary-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">当前余额</div>
          <div class="stat-value primary">{{ formatMoney(summary.currentBalance) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">可提款金额</div>
          <div class="stat-value success">{{ formatMoney(summary.availableWithdrawal) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">暂扣档案费</div>
          <div class="stat-value warning">{{ formatMoney(summary.pendingArchiveFee) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">累计支出</div>
          <div class="stat-value danger">{{ formatMoney(summary.totalExpense) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="summary-row">
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">项目收款</div>
          <div class="stat-value small success">{{ formatMoney(summary.totalProjectIncome) }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">提成入账</div>
          <div class="stat-value small success">{{ formatMoney(summary.totalCommission) }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">社保公积金</div>
          <div class="stat-value small danger">{{ formatMoney(summary.totalSocialInsurance) }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">个税扣缴</div>
          <div class="stat-value small danger">{{ formatMoney(summary.totalTax) }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">其他支出</div>
          <div class="stat-value small danger">{{ formatMoney(summary.totalPayout) }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-title small">期初余额</div>
          <div class="stat-value small">{{ formatMoney(summary.openingBalance) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="table-card">
      <div class="filter-bar">
        <el-select v-model="filter.lawyerId" placeholder="选择律师" clearable filterable style="width: 180px" v-if="isAdmin">
          <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-input v-model="filter.txnMonth" placeholder="月份 yyyy-MM" clearable style="width: 160px" />
        <el-select v-model="filter.txnType" placeholder="交易类型" clearable style="width: 160px">
          <el-option v-for="item in txnTypes" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      </div>

      <el-table :data="transactionList" v-loading="loading" stripe border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="txnDate" label="日期" width="110" />
        <el-table-column prop="txnMonth" label="月份" width="90" />
        <el-table-column prop="projectNo" label="项目编号" width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="160" />
        <el-table-column prop="txnType" label="交易类型" width="120" />
        <el-table-column prop="amount" label="发生金额" width="130" align="right">
          <template #default="{ row }">
            <span :class="row.amount >= 0 ? 'amount-plus' : 'amount-minus'">
              {{ formatSignedMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="130" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.balance) }}
          </template>
        </el-table-column>
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
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getAccountSummary, getAccountTransactions, type AccountSummary, type AccountTransaction } from '@/api/ledger'
import { getLawyerList } from '@/api/project'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo?.roles?.includes('admin') || userStore.userInfo?.roles?.includes('finance'))

const loading = ref(false)
const summary = reactive<AccountSummary>({
  lawyerId: 0,
  lawyerName: '',
  openingBalance: 0,
  totalIncome: 0,
  totalExpense: 0,
  currentBalance: 0,
  pendingArchiveFee: 0,
  totalProjectIncome: 0,
  totalCommission: 0,
  totalSocialInsurance: 0,
  totalTax: 0,
  totalPayout: 0,
  availableWithdrawal: 0,
})
const transactionList = ref<AccountTransaction[]>([])
const lawyerList = ref<any[]>([])

const filter = reactive({
  lawyerId: undefined as number | undefined,
  txnMonth: '',
  txnType: '',
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const txnTypes = ['项目收款', '账户增加', '暂扣档案费', '社保公积金扣款', '个税扣缴', '个人报销', '工资发放', '其他支出']

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatSignedMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  const sign = value >= 0 ? '+' : '-'
  return sign + '¥' + Math.abs(Number(value)).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadSummary = async () => {
  try {
    const res = await getAccountSummary(filter.lawyerId)
    Object.assign(summary, res)
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
    }
    if (filter.lawyerId) params.lawyerId = filter.lawyerId
    if (filter.txnMonth) params.txnMonth = filter.txnMonth
    if (filter.txnType) params.txnType = filter.txnType

    const res: any = await getAccountTransactions(params)
    transactionList.value = res.records
    pagination.total = res.total
    await loadSummary()
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

onMounted(async () => {
  if (isAdmin.value) {
    lawyerList.value = await getLawyerList()
  }
  loadData()
})
</script>

<style scoped>
.ledger-view {
  min-height: 100%;
}
.summary-row {
  margin-bottom: 20px;
}
.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 26px;
  font-weight: bold;
}
.stat-value.primary { color: #409eff; }
.stat-value.success { color: #67c23a; }
.stat-value.danger { color: #f56c6c; }
.stat-value.warning { color: #e6a23c; }
.stat-title.small {
  font-size: 12px;
  margin-bottom: 6px;
}
.stat-value.small {
  font-size: 18px;
}
.table-card {
  min-height: 500px;
}
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.amount-plus {
  color: #67c23a;
}
.amount-minus {
  color: #f56c6c;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
