<template>
  <view class="index-container">
    <view class="header">
      <view class="welcome">您好，{{ userInfo?.realName || userInfo?.username || '律师' }}</view>
      <view class="role">{{ userInfo?.roles?.join(' ') }}</view>
    </view>

    <view class="card balance-card">
      <view class="card-title">账户余额</view>
      <view class="balance">{{ formatMoney(summary.currentBalance) }}</view>
      <view class="balance-sub">暂扣档案费 {{ formatMoney(summary.pendingArchiveFee) }}</view>
    </view>

    <view class="stats-grid">
      <view class="stat-item">
        <view class="stat-label">累计收入</view>
        <view class="stat-value green">{{ formatMoney(summary.totalIncome) }}</view>
      </view>
      <view class="stat-item">
        <view class="stat-label">累计支出</view>
        <view class="stat-value red">{{ formatMoney(summary.totalExpense) }}</view>
      </view>
    </view>

    <view class="card">
      <view class="card-title">最近流水</view>
      <view v-if="transactions.length === 0" class="empty">暂无流水记录</view>
      <view v-for="item in transactions" :key="item.id" class="txn-item">
        <view class="txn-left">
          <view class="txn-type">{{ item.txnType }}</view>
          <view class="txn-date">{{ item.txnDate }} {{ item.projectName }}</view>
        </view>
        <view class="txn-right">
          <view class="txn-amount" :class="item.amount >= 0 ? 'green' : 'red'">
            {{ item.amount >= 0 ? '+' : '' }}{{ formatMoney(item.amount) }}
          </view>
          <view class="txn-balance">余额 {{ formatMoney(item.balance) }}</view>
        </view>
      </view>
    </view>

    <button class="logout-btn" type="warn" plain @click="logout">退出登录</button>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { getAccountSummary, getAccountTransactions, type AccountSummary, type AccountTransaction } from '@/api/ledger'
import type { UserInfo } from '@/api/auth'

const userInfo = ref<UserInfo | null>(uni.getStorageSync('userInfo'))
const summary = reactive<AccountSummary>({
  lawyerId: 0,
  lawyerName: '',
  openingBalance: 0,
  totalIncome: 0,
  totalExpense: 0,
  currentBalance: 0,
  pendingArchiveFee: 0,
})
const transactions = ref<AccountTransaction[]>([])

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadData = async () => {
  try {
    const sum = await getAccountSummary()
    Object.assign(summary, sum)
    const txn = await getAccountTransactions({ page: 1, size: 10 })
    transactions.value = txn.records
  } catch (error) {
    console.error(error)
  }
}

const logout = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
  uni.reLaunch({ url: '/pages/login/login' })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.index-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20rpx;
}
.header {
  padding: 20rpx;
}
.welcome {
  font-size: 36rpx;
  font-weight: bold;
  color: #303133;
}
.role {
  font-size: 24rpx;
  color: #909399;
  margin-top: 8rpx;
}
.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.balance-card {
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  color: #fff;
}
.card-title {
  font-size: 28rpx;
  margin-bottom: 16rpx;
}
.balance-card .card-title {
  color: rgba(255, 255, 255, 0.8);
}
.balance {
  font-size: 60rpx;
  font-weight: bold;
}
.balance-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 10rpx;
}
.stats-grid {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}
.stat-item {
  flex: 1;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}
.stat-label {
  font-size: 26rpx;
  color: #909399;
  margin-bottom: 10rpx;
}
.stat-value {
  font-size: 34rpx;
  font-weight: bold;
}
.green { color: #67c23a; }
.red { color: #f56c6c; }
.txn-item {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.txn-item:last-child {
  border-bottom: none;
}
.txn-type {
  font-size: 30rpx;
  color: #303133;
}
.txn-date {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
}
.txn-amount {
  font-size: 32rpx;
  font-weight: bold;
  text-align: right;
}
.txn-balance {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
  text-align: right;
}
.empty {
  text-align: center;
  color: #909399;
  padding: 40rpx 0;
}
.logout-btn {
  margin-top: 40rpx;
}
</style>
