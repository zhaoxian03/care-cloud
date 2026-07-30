import request from './request'

/**
 * 外出登记
 * @param {Object} data 外出信息
 * @param {number} data.customerId 客户ID
 * @param {string} data.reason 外出原因
 * @param {string} data.expectedBackTime 预计返回时间（格式：YYYY-MM-DD HH:mm:ss）
 * @returns {Promise} 返回 Promise 对象
 */
export function addOutRecord(data) {
  return request({ url: '/api/outrecord', method: 'post', data })
}

/**
 * 外出返回登记
 * @param {number} outId 外出记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function backOutRecord(outId) {
  return request({ url: `/api/outrecord/back/${outId}`, method: 'put' })
}

/**
 * 强制返回
 * @param {number} outId 外出记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function forceBackOutRecord(outId) {
  return request({ url: `/api/outrecord/force-back/${outId}`, method: 'put' })
}

/**
 * 删除外出记录
 * @param {number} outId 外出记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteOutRecord(outId) {
  return request({ url: `/api/outrecord/${outId}`, method: 'delete' })
}

/**
 * 分页查询外出记录
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @param {number} [params.customerId] 客户ID筛选（可选）
 * @param {number} [params.status] 状态筛选（可选，0-外出中，1-已返回，2-逾期）
 * @returns {Promise} 返回 Promise 对象
 */
export function pageOutRecords(params) {
  return request({ url: '/api/outrecord/page', method: 'get', params })
}
