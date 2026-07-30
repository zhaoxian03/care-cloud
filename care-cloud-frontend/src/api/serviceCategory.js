import request from './request'

export function listServiceCategories() {
  return request({ url: '/api/service-category/list', method: 'get' })
}

export function createServiceCategory(data) {
  return request({ url: '/api/service-category', method: 'post', data })
}

export function updateServiceCategory(id, data) {
  return request({ url: `/api/service-category/${id}`, method: 'put', data })
}

export function deleteServiceCategory(id) {
  return request({ url: `/api/service-category/${id}`, method: 'delete' })
}
