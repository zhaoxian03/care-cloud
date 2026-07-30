import request from './request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/storage/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
