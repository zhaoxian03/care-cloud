import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== undefined && res.code !== 200) {
      if (res.code === 401) {
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('menu')
        ElMessage.warning(res.msg || '登录已过期，请重新登录')
        router.push('/login')
        return Promise.reject(new Error(res.msg || '登录已过期'))
      }
      if (res.code === 403) {
        ElMessage.warning(res.msg || '无权限访问')
        return Promise.reject(new Error(res.msg || '无权限访问'))
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      // 兼容两种响应格式：自定义的 msg 字段和 Spring 默认的 message 字段
      const backendMsg = error.response?.data?.msg || error.response?.data?.message
      if (status === 401) {
        sessionStorage.removeItem('token')
        ElMessage.warning(backendMsg || '登录已过期，请重新登录')
        router.push('/login')
      } else if (status === 403) {
        ElMessage.warning(backendMsg || '无权限访问')
      } else if (status === 404) {
        ElMessage.error(backendMsg || '业务异常，请联系管理员')
      } else if (status === 429) {
        ElMessage.warning(backendMsg || '请求过于频繁，请稍后再试')
      } else if (status === 400) {
        ElMessage.warning(backendMsg || '请求参数错误')
      } else {
        // 500 及其他错误，显示中文提示
        ElMessage.error('业务异常，请联系管理员')
      }
    } else {
      ElMessage.error('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
