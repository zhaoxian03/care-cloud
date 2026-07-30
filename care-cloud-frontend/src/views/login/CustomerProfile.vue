<template>
  <div class="profile-page">
    <el-card shadow="hover" class="profile-card">
      <template #header>
        <div class="card-header">
          <span>我的信息</span>
          <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </template>

      <el-form :model="form" label-width="100px" size="large">
        <el-form-item label="手机号">
          <el-input :model-value="storeInfo.phone" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-area">
            <el-avatar
              :size="72"
              :src="avatarDisplay"
              icon="UserFilled"
            />
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
            >
              <el-button size="small" type="primary">上传头像</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
        </el-form-item>

        <el-divider />

        <el-form-item label="修改密码">
          <el-button type="primary" size="small" @click="showPwdDialog = true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showPwdDialog" title="修改密码" width="400px" destroy-on-close>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import request from '../../api/request'

const router = useRouter()
const userStore = useUserStore()

const minioBaseUrl = 'http://localhost:9000'
const storeInfo = computed(() => userStore.customerInfo || {})
const token = computed(() => userStore.token)

const uploadUrl = `${import.meta.env.VITE_API_BASE_URL || ''}/api/storage/upload`
const uploadHeaders = computed(() => token.value ? { Authorization: 'Bearer ' + token.value } : {})

const avatarDisplay = computed(() => {
  return storeInfo.value.avatarUrl ? minioBaseUrl + '/' + storeInfo.value.avatarUrl : undefined
})

const form = ref({
  realName: '',
  age: null,
  gender: '',
  avatarUrl: ''
})

onMounted(() => {
  if (storeInfo.value.realName) form.value.realName = storeInfo.value.realName
  if (storeInfo.value.age != null) form.value.age = storeInfo.value.age
  if (storeInfo.value.gender) form.value.gender = storeInfo.value.gender
  if (storeInfo.value.avatarUrl) form.value.avatarUrl = storeInfo.value.avatarUrl
})

const saveLoading = ref(false)
const handleSave = async () => {
  saveLoading.value = true
  try {
    await request({
      method: 'PUT',
      url: '/api/auth/profile',
      data: {
        realName: form.value.realName,
        age: form.value.age,
        gender: form.value.gender,
        avatarUrl: form.value.avatarUrl
      }
    })
    userStore.setCustomerSession({
      ...userStore.customerInfo,
      realName: form.value.realName,
      age: form.value.age,
      gender: form.value.gender,
      avatarUrl: form.value.avatarUrl
    })
    ElMessage.success('保存成功')
  } catch (e) {
    // handled
  } finally {
    saveLoading.value = false
  }
}

const beforeUpload = (file) => {
  const isImg = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isImg) {
    ElMessage.error('只支持 JPG/PNG/GIF/WebP 格式')
    return false
  }
  const is2M = file.size / 1024 / 1024 < 2
  if (!is2M) {
    ElMessage.error('图片不能超过 2MB')
    return false
  }
  return true
}

const handleUploadSuccess = (res) => {
  if (res && res.data) {
    const relativePath = res.data
    form.value.avatarUrl = relativePath
    ElMessage.success('头像上传成功，请点击保存')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const showPwdDialog = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      if (value !== pwdForm.value.newPassword) {
        callback(new Error('两次密码输入不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }]
}

const handleChangePwd = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await request({
      method: 'PUT',
      url: '/api/auth/password',
      data: {
        oldPassword: pwdForm.value.oldPassword,
        newPassword: pwdForm.value.newPassword
      }
    })
    ElMessage.success('密码修改成功')
    showPwdDialog.value = false
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {
    // handled
  } finally {
    pwdLoading.value = false
  }
}

const handleLogout = () => {
  userStore.clearToken()
  router.push('/login')
}
</script>

<style scoped>
.profile-page {
  padding: 40px;
  display: flex;
  justify-content: center;
}

.profile-card {
  width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-area {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
