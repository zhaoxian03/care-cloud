import request from './request'

/**
 * 管理员登录
 * @param {Object} data 登录信息
 * @param {string} data.username 账号
 * @param {string} data.password 密码
 * @returns {Promise} 返回 Promise 对象，包含管理员信息和 Token
 */
export function login(data) {
  return request({ url: '/api/admin/login', method: 'post', data })
}

/**
 * 管理员退出登录
 * @returns {Promise} 返回 Promise 对象
 */
export function logout() {
  return request({ url: '/api/admin/logout', method: 'post' })
}

/**
 * 获取当前管理员信息
 * @returns {Promise} 返回 Promise 对象，包含管理员详细信息
 */
export function getCurrentAdmin() {
  return request({ url: '/api/admin/current', method: 'get' })
}

/**
 * 刷新 Token
 * @returns {Promise} 返回 Promise 对象，包含新的 Token
 */
export function refreshToken() {
  return request({ url: '/api/admin/refresh', method: 'post' })
}

/**
 * 创建管理员（仅超级管理员）
 * @param {Object} data 管理员信息
 * @returns {Promise} 返回 Promise 对象
 */
export function createAdmin(data) {
  return request({ url: '/api/admin', method: 'post', data })
}

/**
 * 分页查询管理员列表（仅超级管理员）
 * @param {Object} params 查询参数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageAdmins(params) {
  return request({ url: '/api/admin/page', method: 'get', params })
}

/**
 * 更新管理员状态（仅超级管理员）
 * @param {number} id 管理员ID
 * @param {number} status 状态（1-启用，0-禁用）
 * @returns {Promise} 返回 Promise 对象
 */
export function updateAdminStatus(id, status) {
  return request({ url: `/api/admin/${id}/status`, method: 'put', params: { status } })
}

/**
 * 删除管理员（仅超级管理员）
 * @param {number} id 管理员ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteAdmin(id) {
  return request({ url: `/api/admin/${id}`, method: 'delete' })
}
