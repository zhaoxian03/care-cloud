import request from './request'

export function pageSubscriptions(params) {
  return request({ url: '/api/customer-subscription/page', method: 'get', params })
}

export function createSubscription(data) {
  return request({ url: '/api/customer-subscription', method: 'post', data })
}

export function deleteSubscription(id) {
  return request({ url: `/api/customer-subscription/${id}`, method: 'delete' })
}

export function renewSubscription(id, data) {
  return request({ url: `/api/customer-subscription/${id}/renew`, method: 'put', data })
}
