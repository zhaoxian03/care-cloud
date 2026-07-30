import request from './request'

/**
 * 自定义膳食（支持菜品关联）
 * @param {Object} data 膳食信息
 * @param {Array} dishIds 菜品ID列表（可选）
 * @returns {Promise} 返回 Promise 对象
 */
export function addMealCustom(data, dishIds) {
  const params = dishIds && dishIds.length > 0 ? { dishIds: dishIds.join(',') } : {}
  return request({ url: '/api/meal/custom', method: 'post', data, params })
}

/**
 * 批量创建膳食记录（一次性安排早/午/晚餐）
 * @param {Object} data { customerId, mealDate, mealTypes, dishIds }
 * @returns {Promise} 返回 Promise 对象
 */
export function addMealCustomBatch(data) {
  return request({ url: '/api/meal/custom/batch', method: 'post', data })
}

/**
 * 更新膳食记录
 * @param {Object} data 膳食信息
 * @param {Array} dishIds 菜品ID列表（可选）
 * @returns {Promise} 返回 Promise 对象
 */
export function updateMealCustom(data, dishIds) {
  const params = dishIds && dishIds.length > 0 ? { dishIds: dishIds.join(',') } : {}
  return request({ url: '/api/meal/custom', method: 'put', data, params })
}

/**
 * 查询用户膳食日历
 * @param {number} userId 客户ID
 * @param {Object} params 查询参数
 * @returns {Promise} 返回 Promise 对象
 */
export function getMealCalendar(userId, params) {
  return request({ url: `/api/meal/calendar/${userId}`, method: 'get', params })
}

/**
 * 分页查询膳食记录
 * @param {Object} params 查询参数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageMeals(params) {
  return request({ url: '/api/meal/page', method: 'get', params })
}

/**
 * 删除膳食记录
 * @param {number} id 膳食记录ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteMealCustom(id) {
  return request({ url: `/api/meal/custom/${id}`, method: 'delete' })
}

/**
 * 更新膳食记录状态
 * @param {number} id 膳食记录ID
 * @param {number} status 状态（0-启用，1-停用）
 * @returns {Promise} 返回 Promise 对象
 */
export function updateMealStatus(id, status) {
  return request({ url: `/api/meal/${id}/status`, method: 'put', params: { status } })
}

/**
 * 批量复制下周膳食
 * @returns {Promise} 返回 Promise 对象
 */
export function copyNextWeek() {
  return request({ url: '/api/meal/copy-next-week', method: 'post' })
}

// ==================== 菜品管理 API ====================

/**
 * 查询所有启用的菜品列表
 * @returns {Promise} 返回 Promise 对象
 */
export function listDishes() {
  return request({ url: '/api/dish/list', method: 'get' })
}

/**
 * 查询所有菜品列表（包含停用的）
 * @returns {Promise} 返回 Promise 对象
 */
export function listAllDishes() {
  return request({ url: '/api/dish/list-all', method: 'get' })
}

/**
 * 分页查询菜品
 * @param {Object} params 查询参数
 * @returns {Promise} 返回 Promise 对象
 */
export function pageDishes(params) {
  return request({ url: '/api/dish/page', method: 'get', params })
}

/**
 * 添加菜品
 * @param {Object} data 菜品信息
 * @returns {Promise} 返回 Promise 对象
 */
export function addDish(data) {
  return request({ url: '/api/dish', method: 'post', data })
}

/**
 * 更新菜品
 * @param {Object} data 菜品信息
 * @returns {Promise} 返回 Promise 对象
 */
export function updateDish(data) {
  return request({ url: '/api/dish', method: 'put', data })
}

/**
 * 删除菜品
 * @param {number} id 菜品ID
 * @returns {Promise} 返回 Promise 对象
 */
export function deleteDish(id) {
  return request({ url: `/api/dish/${id}`, method: 'delete' })
}
