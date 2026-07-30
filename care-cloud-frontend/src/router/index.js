import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue'),
    meta: { title: '注册' }
  },

  {
    path: '/app',
    component: () => import('../layout/CustomerLayout.vue'),
    redirect: '/app/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'CustomerDashboard',
        component: () => import('../views/portal/Dashboard.vue'),
        meta: { title: '客户首页' }
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('../views/portal/Chat.vue'),
        meta: { title: 'AI健康助手' }
      },
      {
        path: 'care',
        name: 'CustomerCare',
        component: () => import('../views/portal/CareRecords.vue'),
        meta: { title: '护理记录' }
      },
      {
        path: 'meal',
        name: 'CustomerMeal',
        component: () => import('../views/portal/MealCalendar.vue'),
        meta: { title: '膳食日历' }
      },
      {
        path: 'subscription',
        name: 'CustomerSubscription',
        component: () => import('../views/portal/Subscriptions.vue'),
        meta: { title: '服务订阅' }
      },
      {
        path: 'caregiver',
        name: 'CustomerCaregiver',
        component: () => import('../views/portal/Caregiver.vue'),
        meta: { title: '我的管家' }
      },
      {
        path: 'profile',
        name: 'CustomerProfile',
        component: () => import('../views/portal/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'pay/success',
        name: 'PaySuccess',
        component: () => import('../views/portal/PaySuccess.vue'),
        meta: { title: '支付结果' }
      },
      {
        path: 'payment/history',
        name: 'PaymentHistory',
        component: () => import('../views/portal/PaymentHistory.vue'),
        meta: { title: '支付记录' }
      }
    ]
  },

  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'customer',
        name: 'CustomerManage',
        component: () => import('../views/customer/CustomerManage.vue'),
        meta: { title: '客户管理' }
      },
      {
        path: 'customer/bed',
        name: 'BedManage',
        component: () => import('../views/bed/BedManage.vue'),
        meta: { title: '床位管理' }
      },
      {
        path: 'customer/checkin',
        name: 'CheckInManage',
        component: () => import('../views/checkin/CheckInManage.vue'),
        meta: { title: '入住管理' }
      },
      {
        path: 'customer/outrecord',
        name: 'OutRecordManage',
        component: () => import('../views/outrecord/OutRecordManage.vue'),
        meta: { title: '外出管理' }
      },
      {
        path: 'meal',
        name: 'MealManage',
        component: () => import('../views/meal/MealManage.vue'),
        meta: { title: '膳食管理' }
      },
      {
        path: 'meal/dish',
        name: 'DishManage',
        component: () => import('../views/meal/DishManage.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'care/record',
        name: 'CareRecordManage',
        component: () => import('../views/care/CareRecordManage.vue'),
        meta: { title: '护理记录' }
      },
      {
        path: 'care/level',
        name: 'CareLevelManage',
        component: () => import('../views/care/CareLevelManage.vue'),
        meta: { title: '护理等级' }
      },
      {
        path: 'care/item',
        name: 'CareItemManage',
        component: () => import('../views/care/CareItemManage.vue'),
        meta: { title: '护理项目' }
      },
      {
        path: 'report',
        name: 'ReportManage',
        component: () => import('../views/report/ReportManage.vue'),
        meta: { title: '统计报表' }
      },
      {
        path: 'system/admin',
        name: 'AdminManage',
        component: () => import('../views/admin/AdminManage.vue'),
        meta: { title: '管理员管理' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('../views/system/RoleManage.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/permission',
        name: 'PermissionManage',
        component: () => import('../views/system/PermissionManage.vue'),
        meta: { title: '权限管理' }
      },
      {
        path: 'caregiver',
        name: 'CaregiverManage',
        component: () => import('../views/caregiver/CaregiverManage.vue'),
        meta: { title: '管家管理' }
      },
      {
        path: 'customer/subscription',
        name: 'CustomerSubscriptionManage',
        component: () => import('../views/service/CustomerSubscriptionManage.vue'),
        meta: { title: '服务订阅' }
      },
      {
        path: 'service/category',
        name: 'ServiceCategoryManage',
        component: () => import('../views/service/ServiceCategoryManage.vue'),
        meta: { title: '服务分类' }
      },
      {
        path: 'service/catalog',
        name: 'ServiceCatalogManage',
        component: () => import('../views/service/ServiceCatalogManage.vue'),
        meta: { title: '服务产品目录' }
      },
      {
        path: 'payment/manage',
        name: 'PaymentManage',
        component: () => import('../views/admin/PaymentManage.vue'),
        meta: { title: '订单管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const title = to.meta.title
  if (title) {
    document.title = `${title} - 东软颐养中心`
  }

  const token = sessionStorage.getItem('token')
  const customerInfo = JSON.parse(sessionStorage.getItem('customerInfo') || 'null')
  const publicPages = ['/login', '/register']

  if (publicPages.includes(to.path)) {
    if (token) {
      if (customerInfo) {
        next('/app/dashboard')
      } else {
        next('/')
      }
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  // 客户只能访问客户端的页面
  if (customerInfo && !to.path.startsWith('/app')) {
    next('/app/dashboard')
    return
  }

  // 管理员不能访问客户端页面
  if (!customerInfo && to.path.startsWith('/app')) {
    next('/dashboard')
    return
  }

  // 检查是否需要超级管理员权限
  if (to.meta.requiresSuperAdmin) {
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
    if (userInfo.roleLevel !== 'super_admin') {
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
