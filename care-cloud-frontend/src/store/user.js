import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getCurrentAdmin, logout as logoutApi } from '../api/admin'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(sessionStorage.getItem('token') || '')

  const userInfo = ref({
    id: null,
    username: '',
    realName: '',
    roleLevel: ''
  })

  const customerInfo = ref(JSON.parse(sessionStorage.getItem('customerInfo') || 'null'))

  const menu = ref(JSON.parse(sessionStorage.getItem('menu') || '[]'))

  function setToken(newToken) {
    token.value = newToken
    sessionStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    sessionStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setCustomerSession(data) {
    setToken(data.token)
    const info = {
      id: data.id,
      phone: data.phone,
      realName: data.realName,
      age: data.age,
      gender: data.gender,
      avatarUrl: data.avatarUrl,
      emergencyContact: data.emergencyContact || '',
      emergencyRelation: data.emergencyRelation || '',
      role: 'customer'
    }
    customerInfo.value = info
    sessionStorage.setItem('customerInfo', JSON.stringify(info))
    sessionStorage.removeItem('userInfo')
  }

  function setMenu(menuData) {
    menu.value = menuData
    sessionStorage.setItem('menu', JSON.stringify(menuData))
  }

  function patchCustomerInfo(partial) {
    customerInfo.value = { ...customerInfo.value, ...partial }
    sessionStorage.setItem('customerInfo', JSON.stringify(customerInfo.value))
  }

  function getMenu() {
    return menu.value
  }

  function hasPermission(code) {
    if (!code) return true
    if (userInfo.value.roleLevel === 'super_admin') return true

    function searchTree(nodes) {
      if (!nodes || nodes.length === 0) return false
      for (const node of nodes) {
        if (node.code === code) return true
        if (node.children && node.children.length > 0) {
          if (searchTree(node.children)) return true
        }
      }
      return false
    }
    return searchTree(menu.value)
  }

  function hasAnyPermission(codes) {
    if (!codes || codes.length === 0) return true
    return codes.some(code => hasPermission(code))
  }

  function clearToken() {
    token.value = ''
    userInfo.value = { id: null, username: '', realName: '', roleLevel: '' }
    customerInfo.value = null
    menu.value = []
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    sessionStorage.removeItem('customerInfo')
    sessionStorage.removeItem('menu')
  }

  async function login(username, password) {
    const res = await loginApi({ username, password })
    if (res.code === 200) {
      setToken(res.data.token)
      const info = {
        id: res.data.id,
        username: res.data.username,
        realName: res.data.realName,
        roleLevel: res.data.roleLevel
      }
      setUserInfo(info)
      if (res.data.menu) {
        setMenu(res.data.menu)
      }
      sessionStorage.removeItem('customerInfo')
      return true
    }
    return false
  }

  async function getUserInfo() {
    try {
      const res = await getCurrentAdmin()
      if (res.code === 200) {
        setUserInfo(res.data)
      }
    } catch (e) {
      console.error('获取管理员信息失败', e)
      clearToken()
      router.push('/login')
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
    }
    clearToken()
    router.push('/login')
  }

  return {
    token,
    userInfo,
    customerInfo,
    menu,
    setToken,
    setCustomerSession,
    setMenu,
    getMenu,
    patchCustomerInfo,
    hasPermission,
    hasAnyPermission,
    clearToken,
    login,
    getUserInfo,
    logout
  }
})
