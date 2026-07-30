<template>
  <div class="profile-page">
    <el-card shadow="hover" class="profile-card">
      <template #header><span>个人信息</span></template>
      <el-form :model="form" label-width="100px" size="large">
        <el-form-item label="头像">
          <div style="display:flex;align-items:center;gap:12px">
            <el-avatar :size="72" :src="avatarSrc">
              <el-icon :size="36"><UserFilled /></el-icon>
            </el-avatar>
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="onUploadOk"
              :on-error="onUploadFail"
              :before-upload="beforeUpload"
            >
              <el-button size="small" type="primary">上传头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-divider content-position="left">紧急联系人</el-divider>
        <el-form-item label="联系人">
          <el-input v-model="form.emergencyContact" placeholder="联系人手机号" />
        </el-form-item>
        <el-form-item label="关系">
          <el-input v-model="form.emergencyRelation" placeholder="如：子女、配偶" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </el-form-item>
        <el-divider />
        <el-form-item label="修改密码">
          <el-button type="primary" size="small" @click="pwdDialog=true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="pwdDialog" title="修改密码" width="400px">
      <el-form :model="pwdForm" label-width="100px">
        <el-form-item label="旧密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog=false">取消</el-button>
        <el-button type="primary" :loading="pwdSaving" @click="changePwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'
import request from '../../api/request'

const store = useUserStore()
const minioBase = 'http://localhost:9000'
const uploadUrl = (import.meta.env.VITE_API_BASE_URL || '') + '/api/storage/upload'
const uploadHeaders = computed(() => {
  return store.token ? { Authorization: 'Bearer ' + store.token } : {}
})

const avatarSrc = computed(() => {
  const u = form.value.avatarUrl
  return u ? minioBase + '/' + u : undefined
})

const form = ref({
  phone: '', realName: '', age: null, gender: '',
  avatarUrl: '', emergencyContact: '', emergencyRelation: ''
})
const saving = ref(false)

onMounted(() => {
  const info = store.customerInfo || {}
  form.value.phone = info.phone || ''
  form.value.realName = info.realName || ''
  form.value.age = info.age ?? null
  form.value.gender = info.gender || ''
  form.value.avatarUrl = info.avatarUrl || ''
  form.value.emergencyContact = info.emergencyContact || ''
  form.value.emergencyRelation = info.emergencyRelation || ''
})

function beforeUpload(file) {
  const ok = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持 JPG/PNG/GIF/WebP'); return false }
  if (file.size > 2 * 1024 * 1024) { ElMessage.error('图片不能超过2MB'); return false }
  return true
}

function onUploadOk(res) {
  if (res && res.data) {
    form.value.avatarUrl = res.data
    ElMessage.success('上传成功，请点击保存')
  }
}

function onUploadFail() {
  ElMessage.error('上传失败')
}

async function handleSave() {
  const oldInfo = store.customerInfo || {}
  const phoneChanged = form.value.phone && form.value.phone !== oldInfo.phone
  const ecChanged = form.value.emergencyContact && form.value.emergencyContact !== (oldInfo.emergencyContact || '')
  const needConfirm = phoneChanged || ecChanged

  if (needConfirm) {
    const parts = []
    if (phoneChanged) parts.push('您的手机号')
    if (ecChanged) parts.push('紧急联系人联系方式')
    try {
      await ElMessageBox.confirm(`您确定要修改${parts.join('和')}吗？`, '确认修改', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch { return }
  }

  saving.value = true
  try {
    await request({ method: 'PUT', url: '/api/auth/profile', data: form.value })
    store.patchCustomerInfo(form.value)
    ElMessage.success('保存成功')
  } catch (e) { /* handled */ }
  saving.value = false
}

const pwdDialog = ref(false)
const pwdSaving = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

async function changePwd() {
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  pwdSaving.value = true
  try {
    await request({
      method: 'PUT', url: '/api/auth/password',
      data: { oldPassword: pwdForm.value.oldPassword, newPassword: pwdForm.value.newPassword }
    })
    ElMessage.success('密码修改成功')
    pwdDialog.value = false
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) { /* handled */ }
  pwdSaving.value = false
}
</script>

<style scoped>
.profile-page { padding: 20px; display: flex; justify-content: center; }
.profile-card { width: 640px; }
</style>
