import request from './request'

/**
 * 查询护理等级列表
 * @returns {Promise} 返回 Promise 对象
 */
export function listCareLevels() {
  return request({ url: '/api/carelevel/list', method: 'get' })
}

/**
 * 添加护理等级
 * @param {Object} data 护理等级信息
 * @param {string} data.levelName 等级名称
 * @param {number} data.price 每日费用
 * @param {string} data.description 描述
 * @returns {Promise} 返回 Promise 对象
 */
export function addCareLevel(data) {
  return request({ url: '/api/carelevel', method: 'post', data })
}

/**
 * 删除护理等级
 * @param {number} id 护理等级ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteCareLevel(id) {
  return request({ url: `/api/carelevel/${id}`, method: 'delete' })
}

/**
 * 更新护理等级
 * @param {Object} data 护理等级信息
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCareLevel(data) {
  return request({ url: '/api/carelevel', method: 'put', data })
}

/**
 * 查询护理项目列表（只返回启用的）
 * @returns {Promise} 返回 Promise 对象
 */
export function listCareItems() {
  return request({ url: '/api/care/item/list', method: 'get' })
}

/**
 * 查询所有护理项目列表（包含停用的）
 * @returns {Promise} 返回 Promise 对象
 */
export function listAllCareItems() {
  return request({ url: '/api/care/item/list-all', method: 'get' })
}

/**
 * 分页查询护理项目
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageCareItems(params) {
  return request({ url: '/api/care/item/page', method: 'get', params })
}

/**
 * 添加护理项目
 * @param {Object} data 护理项目信息
 * @param {string} data.itemName 项目名称
 * @param {number} data.defaultDurationMinutes 预计耗时（分钟）
 * @returns {Promise} 返回 Promise 对象
 */
export function addCareItem(data) {
  return request({ url: '/api/care/item', method: 'post', data })
}

/**
 * 删除护理项目
 * @param {number} id 护理项目ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteCareItem(id) {
  return request({ url: `/api/care/item/${id}`, method: 'delete' })
}

/**
 * 更新护理项目
 * @param {Object} data 护理项目信息
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCareItem(data) {
  return request({ url: '/api/care/item', method: 'put', data })
}

/**
 * 添加护理记录
 * @param {Object} data 护理记录信息
 * @param {number} data.customerId 客户ID
 * @param {number} data.careItemId 护理项目ID
 * @param {string} data.remark 备注
 * @returns {Promise} 返回 Promise 对象
 */
export function addCareRecord(data) {
  return request({ url: '/api/care/record', method: 'post', data })
}

/**
 * 分页查询全部护理记录
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageAllCareRecords(params) {
  return request({ url: '/api/care/record/page', method: 'get', params })
}

/**
 * 分页查询指定客户的护理记录
 * @param {number} customerId 客户ID
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页条数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageCareRecords(customerId, params) {
  return request({ url: `/api/care/record/page/${customerId}`, method: 'get', params })
}

/**
 * 删除护理记录
 * @param {number} id 护理记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteCareRecord(id) {
  return request({ url: `/api/care/record/${id}`, method: 'delete' })
}

/**
 * 更新护理记录状态
 * @param {number} id 护理记录ID
 * @param {number} status 状态（0-待执行，1-执行中，2-已完成）
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCareRecordStatus(id, status) {
  return request({ url: `/api/care/record/${id}/status`, method: 'put', params: { status } })
}

/**
 * 更新护理记录
 * @param {Object} data 护理记录信息
 * @returns {Promise} 返回 Promise 对象
 */
export function updateCareRecord(data) {
  return request({ url: '/api/care/record', method: 'put', data })
}

/**
 * 批量删除护理记录
 * @param {Array} ids 记录ID列表
 * @returns {Promise} 返回 Promise 对象
 */
export function batchDeleteCareRecords(ids) {
  return request({ url: '/api/care/record/batch', method: 'delete', data: ids })
}

/**
 * 获取护理统计数据
 * @returns {Promise} 返回 Promise 对象
 */
export function getCareStats() {
  return request({ url: '/api/care/record/stats', method: 'get' })
}

/**
 * 查询护理级别关联的护理项目列表
 * @param {number} careLevelId 护理级别ID
 * @returns {Promise} 返回 Promise 对象
 */
export function getCareLevelItems(careLevelId) {
  return request({ url: `/api/care/level-item/${careLevelId}`, method: 'get' })
}

/**
 * 批量保存护理级别与项目的关联
 * @param {number} careLevelId 护理级别ID
 * @param {Array} careItemIds 护理项目ID列表
 * @returns {Promise} 返回 Promise 对象
 */
export function saveCareLevelItems(careLevelId, careItemIds) {
  return request({ url: `/api/care/level-item/${careLevelId}`, method: 'post', data: careItemIds })
}
