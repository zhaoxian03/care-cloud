import request from './request'

export function listServiceCatalogs(params) {
  return request({ url: '/api/service-catalog/list', method: 'get', params })
}

export function pageServiceCatalogs(params) {
  return request({ url: '/api/service-catalog/page', method: 'get', params })
}

export function createServiceCatalog(data) {
  return request({ url: '/api/service-catalog', method: 'post', data })
}

export function updateServiceCatalog(id, data) {
  return request({ url: `/api/service-catalog/${id}`, method: 'put', data })
}

export function deleteServiceCatalog(id) {
  return request({ url: `/api/service-catalog/${id}`, method: 'delete' })
}
