import request from './request'

/**
 * 入住统计报表
 * @returns {Promise} 返回 Promise 对象
 */
export function getOccupancyReport() {
  return request({ url: '/api/report/occupancy', method: 'get' })
}

/**
 * 护理工作量统计
 * @param {Object} params 查询参数
 * @param {string} [params.startDate] 开始日期（可选）
 * @param {string} [params.endDate] 结束日期（可选）
 * @returns {Promise} 返回 Promise 对象
 */
export function getNurseWorkload(params) {
  return request({ url: '/api/report/nurse/workload', method: 'get', params })
}
