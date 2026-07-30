import request from './request'

/**
 * 入住登记
 * @param {Object} data 入住信息
 * @param {number} data.userId 客户ID
 * @param {number} data.bedId 床位ID
 * @param {number} data.careLevelId 护理级别ID
 * @param {string} data.checkInDate 入住日期（格式：YYYY-MM-DD）
 * @returns {Promise} 返回 Promise 对象
 */
export function checkIn(data) {
  return request({ url: '/api/checkin', method: 'post', data })
}

/**
 * 退住登记
 * @param {number} checkInId 入住记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function checkOut(checkInId) {
  return request({ url: `/api/checkout/${checkInId}`, method: 'put' })
}

/**
 * 分页查询入住记录
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @param {number} [params.customerId] 客户ID筛选（可选）
 * @param {number} [params.status] 状态筛选（可选，0-入住中，1-已退住）
 * @returns {Promise} 返回 Promise 对象
 */
export function pageCheckIn(params) {
  return request({ url: '/api/checkin/page', method: 'get', params })
}
