<template>
  <el-dialog
    :title="isEdit ? '编辑项目' : '新增项目'"
    v-model="visible"
    width="680px"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="项目名称" prop="projectName">
            <el-input v-model="form.projectName" placeholder="填写项目名称，尽量不要重复" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="委托人" prop="clientId">
            <el-select v-model="form.clientId" placeholder="选择委托人" filterable clearable style="width: calc(100% - 36px)">
              <el-option v-for="item in clientList" :key="item.id" :label="formatClientLabel(item)" :value="item.id" />
            </el-select>
            <el-button :icon="Plus" circle plain size="small" type="primary" style="margin-left: 4px" @click="openQuickCreate(1)" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="当事人" prop="partyId">
            <el-select v-model="form.partyId" placeholder="选择当事人" filterable clearable style="width: calc(100% - 36px)">
              <el-option v-for="item in partyList" :key="item.id" :label="formatClientLabel(item)" :value="item.id" />
            </el-select>
            <el-button :icon="Plus" circle plain size="small" type="primary" style="margin-left: 4px" @click="openQuickCreate(2)" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="案由" prop="caseCause">
            <el-input v-model="form.caseCause" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="当事人身份" prop="partyIdentity">
            <el-input v-model="form.partyIdentity" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类别" prop="businessType">
            <el-select v-model="form.businessType" placeholder="选择业务类别" style="width: 100%">
              <el-option v-for="item in businessTypes" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收案日期" prop="caseDate">
            <el-date-picker v-model="form.caseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="主办律师" prop="hostLawyerId">
            <el-select v-model="form.hostLawyerId" placeholder="选择主办律师" filterable clearable style="width: 100%">
              <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="协办律师" prop="assistLawyerId">
            <el-select v-model="form.assistLawyerId" placeholder="选择协办律师" filterable clearable style="width: 100%">
              <el-option v-for="item in lawyerList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同金额" prop="contractAmount">
            <el-input-number v-model="form.contractAmount" :min="0" :precision="2" :controls="false" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="档案费" prop="archiveFee">
            <el-input-number v-model="form.archiveFee" :min="0" :precision="2" :controls="false" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="quickCreate.visible"
    :title="quickCreate.clientType === 1 ? '新建委托人' : '新建当事人'"
    width="420px"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form :model="quickCreate.form" :rules="quickCreateRules" ref="quickFormRef" label-width="80px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="quickCreate.form.name" placeholder="请输入姓名/名称" maxlength="128" />
      </el-form-item>
      <el-form-item label="身份证" prop="idCard">
        <el-input
          v-model="quickCreate.form.idCard"
          placeholder="选填；填入后自动校验并解析性别/年龄"
          maxlength="18"
        />
        <div v-if="quickCreate.idCardHint" class="id-card-hint" :class="quickCreate.idCardHintClass">
          {{ quickCreate.idCardHint }}
        </div>
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="quickCreate.form.phone" placeholder="选填" maxlength="20" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="quickCreate.form.address" placeholder="选填" maxlength="255" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="quickCreate.form.remark" type="textarea" :rows="2" placeholder="选填" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="quickCreate.visible = false">取消</el-button>
      <el-button type="primary" :loading="quickCreate.submitting" @click="handleQuickCreate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createProject, updateProject, getLawyerList, getClientList, type ProjectForm, type Project } from '@/api/project'
import { createClient, getClientList as fetchClientList } from '@/api/client'
import { isValidIdCard, parseIdCard } from '@/utils/idCard'

const props = defineProps<{
  visible: boolean
  data?: Project
}>()

const emit = defineEmits(['update:visible', 'success'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const isEdit = computed(() => !!props.data)
const formRef = ref()
const submitting = ref(false)
const lawyerList = ref<any[]>([])
const clientList = ref<any[]>([])
const partyList = ref<any[]>([])

const form = reactive<ProjectForm>({
  projectName: '',
  clientId: undefined,
  partyId: undefined,
  caseCause: '',
  partyIdentity: '',
  businessType: 'MS',
  hostLawyerId: undefined,
  assistLawyerId: undefined,
  caseDate: '',
  contractAmount: 0,
  archiveFee: 1000,
  remark: '',
})

const businessTypes = [
  { value: 'MS', label: '民事诉讼' },
  { value: 'XS', label: '刑事诉讼' },
  { value: 'XZ', label: '行政诉讼' },
  { value: 'LD', label: '劳动仲裁' },
  { value: 'SS', label: '商事仲裁' },
  { value: 'GW', label: '法律顾问' },
  { value: 'ZX', label: '专项服务' },
  { value: 'FY', label: '行政复议' },
  { value: 'QT', label: '其他非诉' },
  { value: 'MZ', label: '民事执行' },
]

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  clientId: [{ required: true, message: '请选择委托人', trigger: 'change' }],
  businessType: [{ required: true, message: '请选择业务类别', trigger: 'change' }],
  hostLawyerId: [{ required: true, message: '请选择主办律师', trigger: 'change' }],
  caseDate: [{ required: true, message: '请选择收案日期', trigger: 'change' }],
  contractAmount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }],
}

const resetForm = () => {
  form.projectName = ''
  form.clientId = undefined
  form.partyId = undefined
  form.caseCause = ''
  form.partyIdentity = ''
  form.businessType = 'MS'
  form.hostLawyerId = undefined
  form.assistLawyerId = undefined
  form.caseDate = ''
  form.contractAmount = 0
  form.archiveFee = 1000
  form.remark = ''
}

const fillForm = (data: Project) => {
  form.projectName = data.projectName
  form.clientId = data.clientId
  form.partyId = data.partyId
  form.caseCause = data.caseCause
  form.partyIdentity = data.partyIdentity
  form.businessType = data.businessType
  form.hostLawyerId = data.hostLawyerId
  form.assistLawyerId = data.assistLawyerId
  form.caseDate = data.caseDate
  form.contractAmount = data.contractAmount
  form.archiveFee = data.archiveFee
  form.remark = data.remark
}

watch(() => props.visible, (val) => {
  if (val) {
    if (props.data) {
      fillForm(props.data)
    } else {
      resetForm()
    }
  }
})

const loadOptions = async () => {
  lawyerList.value = await getLawyerList()
  clientList.value = await getClientList(1)
  partyList.value = await getClientList(2)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value && props.data) {
      await updateProject(props.data.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createProject(form)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('success')
  } catch (error: any) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadOptions()
})

// 现场新建客户
const quickFormRef = ref()
const quickCreate = reactive<{
  visible: boolean
  submitting: boolean
  clientType: 1 | 2
  form: { name: string; idCard: string; phone: string; address: string; remark: string }
  idCardHint: string
  idCardHintClass: string
}>({
  visible: false,
  submitting: false,
  clientType: 1,
  form: { name: '', idCard: '', phone: '', address: '', remark: '' },
  idCardHint: '',
  idCardHintClass: '',
})

const quickCreateRules = {
  name: [{ required: true, message: '请输入姓名/名称', trigger: 'blur' }],
}

const openQuickCreate = (type: 1 | 2) => {
  quickCreate.clientType = type
  quickCreate.form = { name: '', idCard: '', phone: '', address: '', remark: '' }
  quickCreate.idCardHint = ''
  quickCreate.idCardHintClass = ''
  quickCreate.visible = true
}

watch(() => quickCreate.form.idCard, (val) => {
  const v = (val || '').trim()
  if (!v) {
    quickCreate.idCardHint = ''
    quickCreate.idCardHintClass = ''
    return
  }
  if (!/^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(v)) {
    quickCreate.idCardHint = '格式不正确（需 18 位）'
    quickCreate.idCardHintClass = 'hint-error'
    return
  }
  if (!isValidIdCard(v)) {
    quickCreate.idCardHint = '校验位不通过'
    quickCreate.idCardHintClass = 'hint-error'
    return
  }
  const parsed = parseIdCard(v)
  if (parsed) {
    quickCreate.idCardHint = `${parsed.gender} · ${parsed.age} 岁 · 出生 ${parsed.birthDate}`
    quickCreate.idCardHintClass = 'hint-ok'
  }
})

const formatClientLabel = (item: any) => {
  if (!item) return ''
  const parts: string[] = [item.name]
  const meta: string[] = []
  if (item.gender) meta.push(item.gender)
  if (typeof item.age === 'number') meta.push(`${item.age}岁`)
  return meta.length ? `${parts[0]} (${meta.join(' · ')})` : parts[0]
}

const handleQuickCreate = async () => {
  try {
    await quickFormRef.value.validate()
    quickCreate.submitting = true
    const idCard = quickCreate.form.idCard.trim()
    if (idCard && !isValidIdCard(idCard)) {
      ElMessage.error('身份证号校验失败')
      return
    }
    const newClient = await createClient({
      name: quickCreate.form.name.trim(),
      clientType: quickCreate.clientType,
      phone: quickCreate.form.phone.trim() || undefined,
      idCard: idCard || undefined,
      address: quickCreate.form.address.trim() || undefined,
      remark: quickCreate.form.remark.trim() || undefined,
    })
    // 刷新对应下拉并自动选中新条目
    if (quickCreate.clientType === 1) {
      clientList.value = await fetchClientList(1)
      form.clientId = newClient.id
    } else {
      partyList.value = await fetchClientList(2)
      form.partyId = newClient.id
    }
    ElMessage.success('新增成功')
    quickCreate.visible = false
  } catch (error: any) {
    // 校验失败已被 ElMessage 弹出
  } finally {
    quickCreate.submitting = false
  }
}
</script>

<style scoped>
.id-card-hint {
  font-size: 12px;
  line-height: 1.4;
  margin-top: 2px;
}
.hint-ok {
  color: #67c23a;
}
.hint-error {
  color: #f56c6c;
}
</style>
