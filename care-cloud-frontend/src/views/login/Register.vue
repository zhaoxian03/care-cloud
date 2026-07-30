<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <el-icon :size="40" color="#409eff"><User /></el-icon>
        <h2>用户注册</h2>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Iphone" maxlength="11" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="age">
          <el-input-number v-model="form.age" :min="1" :max="150" style="width: 100%" placeholder="年龄" />
        </el-form-item>
        <el-form-item prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" :loading="submitLoading" @click="handleRegister">注 册</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register as registerApi } from '../../api/auth'

const router = useRouter()
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  realName: '',
  age: null,
  gender: ''
})

const validatePhone = (rule, value, callback) => {
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的11位手机号'))
  } else {
    callback()
  }
}

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await registerApi({
      phone: form.phone,
      password: form.password,
      realName: form.realName,
      age: form.age,
      gender: form.gender
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3e4ea 0%, #d9d5dc 100%);
}

.register-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  margin-top: 12px;
  color: #303133;
  font-size: 22px;
}

.register-btn {
  width: 100%;
  margin-top: 10px;
}

.register-footer {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.register-footer a {
  color: #409eff;
  text-decoration: none;
}
</style>
