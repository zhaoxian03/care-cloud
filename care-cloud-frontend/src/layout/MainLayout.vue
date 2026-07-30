<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <el-icon :size="28" color="#409eff"><Monitor /></el-icon>
        <span v-show="!isCollapse" class="logo-text">东软颐养中心</span>
      </div>
      
      <el-menu
        :default-active="currentRoute"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <el-menu-item v-if="userStore.hasPermission('dashboard:view')" index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>系统首页</template>
        </el-menu-item>

        <el-sub-menu v-if="userStore.hasPermission('customer:view')" index="customer">
          <template #title>
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item v-if="userStore.hasPermission('customer:list')" index="/customer">
            <el-icon><UserFilled /></el-icon>
            <template #title>客户列表</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('bed:view')" index="/customer/bed">
            <el-icon><Grid /></el-icon>
            <template #title>床位管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('checkin:view')" index="/customer/checkin">
            <el-icon><Document /></el-icon>
            <template #title>入住管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('outrecord:view')" index="/customer/outrecord">
            <el-icon><Promotion /></el-icon>
            <template #title>外出管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('subscription:view')" index="/customer/subscription">
            <el-icon><Ticket /></el-icon>
            <template #title>服务订阅</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('caregiver:view')" index="/caregiver">
            <el-icon><UserFilled /></el-icon>
            <template #title>管家管理</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="userStore.hasPermission('service:view')" index="service">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>服务产品管理</span>
          </template>
          <el-menu-item v-if="userStore.hasPermission('category:view')" index="/service/category">
            <el-icon><Collection /></el-icon>
            <template #title>服务分类</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('catalog:view')" index="/service/catalog">
            <el-icon><List /></el-icon>
            <template #title>服务目录</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="userStore.hasPermission('meal:view')" index="meal">
          <template #title>
            <el-icon><Food /></el-icon>
            <span>膳食管理</span>
          </template>
          <el-menu-item v-if="userStore.hasPermission('meal:calendar')" index="/meal">
            <el-icon><Calendar /></el-icon>
            <template #title>膳食日历</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('dish:view')" index="/meal/dish">
            <el-icon><Dish /></el-icon>
            <template #title>菜品管理</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="userStore.hasPermission('care:view')" index="care">
          <template #title>
            <el-icon><FirstAidKit /></el-icon>
            <span>护理管理</span>
          </template>
          <el-menu-item v-if="userStore.hasPermission('care:record')" index="/care/record">
            <el-icon><Document /></el-icon>
            <template #title>护理记录</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('care:level')" index="/care/level">
            <el-icon><Medal /></el-icon>
            <template #title>护理等级</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('care:item')" index="/care/item">
            <el-icon><List /></el-icon>
            <template #title>护理项目</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="userStore.hasPermission('system:view')" index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item v-if="userStore.hasPermission('admin:view')" index="/system/admin">
            <el-icon><User /></el-icon>
            <template #title>管理员管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('role:view')" index="/system/role">
            <el-icon><Collection /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('permission:view')" index="/system/permission">
            <el-icon><List /></el-icon>
            <template #title>权限管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('order:view')" index="/payment/manage">
            <el-icon><Money /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          <el-menu-item v-if="userStore.hasPermission('report:view')" index="/report">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>统计报表</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="user-info">
            <el-icon><UserFilled /></el-icon>
            <span class="user-name">{{ userStore.userInfo.realName || '未登录' }}</span>
            <el-tag size="small" :type="userStore.userInfo.roleLevel === 'super_admin' ? 'danger' : userStore.userInfo.roleLevel === 'caregiver' ? 'warning' : 'info'">
              {{ userStore.userInfo.roleLevel === 'super_admin' ? '超级管理员' : userStore.userInfo.roleLevel === 'caregiver' ? '健康管家' : '管理员' }}
            </el-tag>
          </span>
          <el-button type="danger" link @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

const currentRoute = computed(() => route.path)

onMounted(() => {
  userStore.getUserInfo()
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid #3a4a5b;
}

.logo-collapse .logo-text {
  display: none;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
}

.el-menu-vertical {
  border-right: none;
  max-height: calc(100vh - 60px);
  overflow-y: auto;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.main-container {
  flex-direction: column;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.user-name {
  font-size: 14px;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
