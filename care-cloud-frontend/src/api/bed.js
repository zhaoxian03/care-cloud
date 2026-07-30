import request from './request'

export function getFreeBeds() {
  return request({ url: '/api/bed/free', method: 'get' })
}

export function pageBeds(params) {
  return request({ url: '/api/bed/page', method: 'get', params })
}

export function addBed(data) {
  return request({ url: '/api/bed', method: 'post', data })
}

export function updateBed(id, data) {
  return request({ url: `/api/bed/${id}`, method: 'put', data })
}

export function deleteBed(id) {
  return request({ url: `/api/bed/${id}`, method: 'delete' })
}
