<template>
  <div class="excel-import">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>Excel 历史数据导入</span>
        </div>
      </template>

      <el-alert type="info" :closable="false" class="import-tip">
        <template #title>
          <div>请上传包含以下工作表的 Excel 文件（xls 或 xlsx）：</div>
          <ul class="sheet-list">
            <li><strong>律师</strong>：姓名、工号、手机号、身份证号、入职日期、提成比例、期初余额</li>
            <li><strong>委托人</strong>：姓名、类型(1委托人/2当事人)、手机号、身份证号、地址、备注</li>
            <li><strong>项目</strong>：项目编号、项目名称、委托人、当事人、案由、当事人身份、业务类别、主办律师、协办律师、收案日期、合同金额、档案费、备注</li>
            <li><strong>项目收入</strong>：项目编号、收入日期、收入总额、主办律师收入、协办律师收入、备注</li>
          </ul>
          <div>每个工作表第一行为表头，数据从第二行开始。工作表名称必须完全匹配。</div>
        </template>
      </el-alert>

      <el-upload
        class="upload-area"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".xls,.xlsx"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 xls / xlsx 格式文件</div>
        </template>
      </el-upload>

      <div class="action-bar">
        <el-button type="primary" :loading="importing" :disabled="!selectedFile" @click="handleImport">开始导入</el-button>
      </div>

      <div v-if="result" class="result-area">
        <el-divider content-position="left">导入结果</el-divider>
        <el-row :gutter="20">
          <el-col :span="6" v-for="(item, key) in result" :key="key">
            <el-card shadow="hover" :body-style="{ padding: '16px' }">
              <div class="result-title">{{ sheetName(key) }}</div>
              <div class="result-stat">
                <span class="label">成功：</span>
                <span class="value success">{{ item.successRows }}</span>
              </div>
              <div class="result-stat">
                <span class="label">失败：</span>
                <span class="value fail">{{ item.failRows }}</span>
              </div>
              <div class="result-stat">
                <span class="label">总计：</span>
                <span class="value">{{ item.totalRows }}</span>
              </div>
              <el-collapse v-if="item.errors.length > 0">
                <el-collapse-item title="查看错误">
                  <ul class="error-list">
                    <li v-for="(err, idx) in item.errors" :key="idx">{{ err }}</li>
                  </ul>
                </el-collapse-item>
              </el-collapse>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importExcel, type ImportSummary } from '@/api/import'

const selectedFile = ref<File | null>(null)
const importing = ref(false)
const result = ref<ImportSummary | null>(null)

const sheetName = (key: string) => {
  const map: Record<string, string> = {
    lawyer: '律师',
    client: '委托人',
    project: '项目',
    income: '项目收入',
  }
  return map[key] || key
}

const handleFileChange = (file: any) => {
  selectedFile.value = file.raw
  result.value = null
}

const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  importing.value = true
  try {
    result.value = await importExcel(selectedFile.value)
    ElMessage.success('导入完成')
  } catch (error: any) {
    console.error(error)
  } finally {
    importing.value = false
  }
}
</script>

<style scoped>
.excel-import {
  min-height: 100%;
}
.card-header {
  font-weight: bold;
}
.import-tip {
  margin-bottom: 20px;
}
.sheet-list {
  margin: 8px 0;
  padding-left: 20px;
}
.sheet-list li {
  margin: 4px 0;
}
.upload-area {
  margin-bottom: 20px;
}
.action-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.result-area {
  margin-top: 20px;
}
.result-title {
  font-weight: bold;
  margin-bottom: 12px;
}
.result-stat {
  margin: 4px 0;
}
.result-stat .value {
  font-weight: bold;
}
.result-stat .success {
  color: #67c23a;
}
.result-stat .fail {
  color: #f56c6c;
}
.error-list {
  margin: 0;
  padding-left: 16px;
  color: #f56c6c;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
}
.error-list li {
  margin: 4px 0;
}
</style>
