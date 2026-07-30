import request from './request'

export function getPermissionTree() {
  return request.get('/api/permissions/tree')
}

export function createPermission(data) {
  return request.post('/api/permissions', data)
}

export function updatePermission(id, data) {
  return request.put(`/api/permissions/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/api/permissions/${id}`)
}
