import request from './request'

export function getRolePage(params) {
  return request.get('/api/roles/page', { params })
}

export function getRoleList() {
  return request.get('/api/roles/list')
}

export function createRole(data) {
  return request.post('/api/roles', data)
}

export function updateRole(id, data) {
  return request.put(`/api/roles/${id}`, data)
}

export function updateRoleStatus(id, isDisabled) {
  return request.put(`/api/roles/${id}/status`, null, { params: { isDisabled } })
}

export function deleteRole(id) {
  return request.delete(`/api/roles/${id}`)
}

export function getRolePermissions(roleId) {
  return request.get(`/api/roles/${roleId}/permissions`)
}

export function saveRolePermissions(roleId, permissionIds) {
  return request.post(`/api/roles/${roleId}/permissions`, permissionIds)
}

export function getAdminRoles(adminId) {
  return request.get(`/api/admin/${adminId}/roles`)
}

export function saveAdminRoles(adminId, roleIds) {
  return request.post(`/api/admin/${adminId}/roles`, roleIds)
}
