<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="40" color="#409eff"><Monitor /></el-icon>
        <h2>东软颐养中心</h2>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="用户登录" name="user">
          <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="0" size="large">
            <el-form-item prop="phone">
              <el-input v-model="userForm.phone" placeholder="手机号" prefix-icon="Iphone" maxlength="11" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleUserLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-btn" :loading="userLoading" @click="handleUserLogin">登 录</el-button>
            </el-form-item>
            <el-form-item>
              <div class="login-footer">
                还没有账号？<router-link to="/register">立即注册</router-link>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="管理员登录" name="admin">
          <el-form ref="adminFormRef" :model="adminForm" :rules="adminRules" label-width="0" size="large">
            <el-form-item prop="username">
              <el-input v-model="adminForm.username" placeholder="管理账号 / 手机号" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleAdminLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-btn" :loading="adminLoading" @click="handleAdminLogin">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { login as customerLoginApi } from '../../api/auth'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('admin')
const adminLoading = ref(false)
const userLoading = ref(false)
const adminFormRef = ref(null)
const userFormRef = ref(null)

const adminForm = reactive({
  username: '',
  password: ''
})

const adminRules = {
  username: [{ required: true, message: '请输入账号/手机号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const userForm = reactive({
  phone: '',
  password: ''
})

const userRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleAdminLogin = async () => {
  const valid = await adminFormRef.value.validate().catch(() => false)
  if (!valid) return

  adminLoading.value = true
  try {
    const success = await userStore.login(adminForm.username, adminForm.password)
    if (success) {
      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  } catch (e) {
    // handled by interceptor
  } finally {
    adminLoading.value = false
  }
}

const handleUserLogin = async () => {
  const valid = await userFormRef.value.validate().catch(() => false)
  if (!valid) return

  userLoading.value = true
  try {
    const res = await customerLoginApi({ phone: userForm.phone, password: userForm.password })
    if (res.code === 200) {
      userStore.setCustomerSession(res.data)
      ElMessage.success('登录成功')
      router.push('/customer/profile')
    }
  } catch (e) {
    // handled by interceptor
  } finally {
    userLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3e4ea 0%, #d9d5dc 100%);
}

.login-card {
  width: 420px;
  padding: 32px 40px 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  margin-top: 12px;
  color: #303133;
  font-size: 22px;
}

.login-tabs {
  margin-top: 8px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-footer {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
}
</style>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3e4ea 0%, #d9d5dc 100%);
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin-top: 12px;
  color: #303133;
  font-size: 22px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}
</style>
