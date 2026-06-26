<template>
  <el-dialog title="项目归档" v-model="visibleValue" width="520px" destroy-on-close :close-on-click-modal="false">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="选择项目" prop="projectId">
        <el-select v-model="form.projectId" placeholder="选择要归档的项目" filterable clearable style="width: 100%" @change="handleProjectChange">
          <el-option v-for="item in projectList" :key="item.id" :label="`${item.projectNo} - ${item.projectName}`" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="归属律师" prop="lawyerId">
        <el-select v-model="form.lawyerId" placeholder="选择律师" filterable clearable style="width: 100%">
          <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="档案费">
        <el-input :model-value="formatMoney(selectedProject?.archiveFee)" disabled />
      </el-form-item>
      <el-form-item label="归档日期" prop="archiveDate">
        <el-date-picker v-model="form.archiveDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visibleValue = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确认归档</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { archiveProject, type ArchiveFeeForm } from '@/api/archiveFee'
import { getProjectList, getLawyerList, type Project } from '@/api/project'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits(['update:visible', 'success'])

const visibleValue = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const formRef = ref()
const submitting = ref(false)
const projectList = ref<Project[]>([])
const lawyerList = ref<any[]>([])

const form = reactive<ArchiveFeeForm>({
  projectId: undefined,
  lawyerId: undefined,
  archiveDate: '',
  remark: '',
})

const selectedProject = computed(() => {
  return projectList.value.find(p => p.id === form.projectId)
})

const rules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  lawyerId: [{ required: true, message: '请选择律师', trigger: 'change' }],
  archiveDate: [{ required: true, message: '请选择归档日期', trigger: 'change' }],
}

const formatMoney = (value?: number) => {
  if (value == null) return '¥0.00'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const handleProjectChange = (projectId: number) => {
  const project = projectList.value.find(p => p.id === projectId)
  if (project) {
    form.lawyerId = project.hostLawyerId
  }
}

const loadOptions = async () => {
  const res: any = await getProjectList({ page: 1, size: 1000 })
  projectList.value = res.records.filter((p: Project) => p.archiveStatus !== 1)
  lawyerList.value = await getLawyerList()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    await archiveProject(form)
    ElMessage.success('归档成功')
    visibleValue.value = false
    emit('success')
  } catch (error: any) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    form.projectId = undefined
    form.lawyerId = undefined
    form.archiveDate = new Date().toISOString().split('T')[0]
    form.remark = ''
    loadOptions()
  }
})

onMounted(() => {
  if (props.visible) loadOptions()
})
</script>
