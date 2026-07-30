import request from './request'

/**
 * 创建客户
 * @param {Object} data 客户信息
 * @returns {Promise} 返回 Promise 对象
 */
export function createCustomer(data) {
  return request({ url: '/api/customer', method: 'post', data })
}

/**
 * 分页查询客户列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @param {string} [params.keyword] 搜索关键词（可选）
 * @param {number} [params.status] 状态筛选（可选）
 * @returns {Promise} 返回 Promise 对象
 */
export function pageCustomers(params) {
  return request({ url: '/api/customer/page', method: 'get', params })
}

/**
 * 修改客户信息
 * @param {number} id 客户ID
 * @param {Object} data 客户信息
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCustomer(id, data) {
  return request({ url: `/api/customer/${id}`, method: 'put', data })
}

/**
 * 更新客户状态（启用/禁用）
 * @param {number} id 客户ID
 * @param {number} status 状态（1-启用，0-禁用）
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCustomerStatus(id, status) {
  return request({ url: `/api/customer/${id}/status`, method: 'put', params: { status } })
}

/**
 * 删除客户（软删除）
 * @param {number} id 客户ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteCustomer(id) {
  return request({ url: `/api/customer/${id}`, method: 'delete' })
}
