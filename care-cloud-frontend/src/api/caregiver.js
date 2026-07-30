import request from './request'

export function bindCaregiver(data) {
  return request({ url: '/api/caregiver/bind', method: 'post', data })
}

export function unbindCaregiver(id) {
  return request({ url: `/api/caregiver/${id}`, method: 'delete' })
}

export function getCaregiversByCustomer(customerId) {
  return request({ url: `/api/caregiver/customer/${customerId}`, method: 'get' })
}

export function getCaregiversByAdmin(adminId) {
  return request({ url: `/api/caregiver/admin/${adminId}`, method: 'get' })
}

export function pageCaregivers(params) {
  return request({ url: '/api/caregiver/page', method: 'get', params })
}

export function updateCaregiverStatus(id, status) {
  return request({ url: `/api/caregiver/${id}/status`, method: 'put', params: { status } })
}

export function deleteCaregiver(id) {
  return request({ url: `/api/caregiver/account/${id}`, method: 'delete' })
}

export function createCaregiver(data) {
  return request({ url: '/api/admin/caregiver', method: 'post', data })
}

export function getCaregiver(id) {
  return request({ url: `/api/caregiver/${id}`, method: 'get' })
}

export function updateCaregiver(id, data) {
  return request({ url: `/api/caregiver/${id}`, method: 'put', data })
}

export function getAvailableCaregivers() {
  return request({ url: '/api/caregiver/available', method: 'get' })
}
