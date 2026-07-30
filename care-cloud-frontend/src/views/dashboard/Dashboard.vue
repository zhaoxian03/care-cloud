<template>
  <div class="page-container">
    <h2 style="margin-bottom: 20px; color: #303133;">欢迎使用东软颐养中心管理系统</h2>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalBeds }}</div>
          <div class="stat-label">总床位数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67c23a">{{ stats.freeBeds }}</div>
          <div class="stat-label">空闲床位</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #e6a23c">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/customer/bed')">
              <el-icon><Grid /></el-icon>床位管理
            </el-button>
            <el-button type="success" @click="$router.push('/customer')">
              <el-icon><User /></el-icon>客户管理
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <div class="system-info">
            <p>系统名称：东软颐养中心管理系统</p>
            <p>当前用户：{{ userStore.userInfo.realName || '未登录' }}</p>
            <p>用户角色：{{ userStore.userInfo.roleNames ? userStore.userInfo.roleNames[0] : '-' }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { getFreeBeds, pageBeds } from '../../api/bed'
import { pageCustomers } from '../../api/customer'

const userStore = useUserStore()

const stats = reactive({
  totalBeds: 0,
  freeBeds: 0,
  totalUsers: 0
})

onMounted(async () => {
  try {
    const [freeRes, bedPageRes, custRes] = await Promise.all([
      getFreeBeds(),
      pageBeds({ page: 1, size: 1 }),
      pageCustomers({ page: 1, size: 1 })
    ])
    stats.freeBeds = freeRes.data.length || 0
    stats.totalBeds = bedPageRes.data?.total ?? 0
    stats.totalUsers = custRes.data?.total ?? 0
  } catch (e) {
    // ignore
  }
})
</script>

<style scoped>
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
}

.system-info p {
  margin-bottom: 10px;
  color: #606266;
}
</style>
