<template>
  <el-container class="customer-layout">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <el-icon :size="24"><HomeFilled /></el-icon>
        <span>东软颐养中心</span>
      </div>

      <el-menu
        :default-active="$route.path"
        class="menu"
        background-color="#2c3e50"
        text-color="#bfcbd9"
        active-text-color="#42b983"
        router
      >
        <el-menu-item index="/app/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="/app/care">
          <el-icon><FirstAidKit /></el-icon>
          <template #title>护理记录</template>
        </el-menu-item>
        <el-menu-item index="/app/meal">
          <el-icon><Food /></el-icon>
          <template #title>膳食日历</template>
        </el-menu-item>
        <el-menu-item index="/app/chat">
          <el-icon><ChatDotSquare /></el-icon>
          <template #title>AI健康助手</template>
        </el-menu-item>
        <el-menu-item index="/app/subscription">
          <el-icon><Ticket /></el-icon>
          <template #title>服务订阅</template>
        </el-menu-item>
        <el-menu-item index="/app/caregiver">
          <el-icon><UserFilled /></el-icon>
          <template #title>我的管家</template>
        </el-menu-item>
        <el-menu-item index="/app/payment/history">
          <el-icon><Wallet /></el-icon>
          <template #title>支付记录</template>
        </el-menu-item>
        <el-menu-item index="/app/profile">
          <el-icon><User /></el-icon>
          <template #title>个人信息</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-area">
      <el-header class="header">
        <span class="greeting">您好，{{ store.customerInfo?.realName || '客户' }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </el-header>

      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useUserStore()

function logout() {
  ElMessageBox.confirm('确定要退出吗？', '提示', {
    type: 'warning'
  }).then(() => {
    store.clearToken()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.customer-layout { height: 100vh; }
.sidebar { background: #2c3e50; overflow: hidden; }
.logo { height: 60px; color: #42b983; display: flex; align-items: center; gap: 10px; padding: 0 20px; font-size: 16px; font-weight: bold; border-bottom: 1px solid #3a4a5b; }
.menu { border-right: none; }
.main-area { flex-direction: column; }
.header { background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.08); padding: 0 20px; }
.greeting { font-size: 14px; color: #606266; }
.content { background: #f5f7fa; padding: 20px; overflow-y: auto; }
</style>
