<template>
  <el-dialog title="登记收入" v-model="visibleValue" width="560px" destroy-on-close :close-on-click-modal="false">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="项目名称">
        <el-input v-model="projectName" disabled />
      </el-form-item>
      <el-form-item label="收入日期" prop="incomeDate">
        <el-date-picker v-model="form.incomeDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
      <el-form-item label="收入总额" prop="incomeAmount">
        <el-input-number v-model="form.incomeAmount" :min="0" :precision="2" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="主办律师收入" prop="hostAmount">
        <el-input-number v-model="form.hostAmount" :min="0" :precision="2" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="协办律师收入" prop="assistAmount">
        <el-input-number v-model="form.assistAmount" :min="0" :precision="2" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visibleValue = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createIncome, type IncomeForm } from '@/api/income'

const props = defineProps<{
  visible: boolean
  project?: any
}>()

const emit = defineEmits(['update:visible', 'success'])

const visibleValue = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const projectName = computed(() => props.project?.projectName || '')
const formRef = ref()
const submitting = ref(false)

const form = reactive<IncomeForm>({
  projectId: undefined,
  incomeDate: '',
  incomeAmount: 0,
  hostAmount: 0,
  assistAmount: 0,
  remark: '',
})

const rules = {
  incomeDate: [{ required: true, message: '请选择收入日期', trigger: 'change' }],
  incomeAmount: [{ required: true, message: '请输入收入总额', trigger: 'blur' }],
}

watch(() => props.visible, (val) => {
  if (val && props.project) {
    form.projectId = props.project.id
    form.incomeDate = new Date().toISOString().split('T')[0]
    form.incomeAmount = 0
    form.hostAmount = 0
    form.assistAmount = 0
    form.remark = ''
  }
})

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.hostAmount + form.assistAmount > form.incomeAmount) {
      ElMessage.warning('主办律师收入 + 协办律师收入不能超过收入总额')
      return
    }
    submitting.value = true
    await createIncome(form)
    ElMessage.success('登记成功')
    visibleValue.value = false
    emit('success')
  } catch (error: any) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}
</script>
