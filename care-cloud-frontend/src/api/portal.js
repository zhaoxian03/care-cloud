import request from './request'

export const getDashboard = () => request({ url: '/api/app/dashboard', method: 'GET' })

export const getCareList = (params) => request({ url: '/api/app/care/list', method: 'GET', params })

export const getMealCalendar = () => request({ url: '/api/app/meal/calendar', method: 'GET' })

export const getSubscriptionList = (params) => request({ url: '/api/app/subscription/list', method: 'GET', params })

export const renewSubscription = (id, data) => request({ url: `/api/app/subscription/${id}/renew`, method: 'PUT', data })

export const cancelSubscription = (id) => request({ url: `/api/app/subscription/${id}/cancel`, method: 'PUT' })

export const createSubscription = (data) => request({ url: '/api/app/subscription/create', method: 'POST', data })

export const getCareItems = () => request({ url: '/api/app/care/item/list', method: 'GET' })

export const getServiceCategories = () => request({ url: '/api/app/service/category/list', method: 'GET' })

export const getServiceCatalogs = () => request({ url: '/api/app/service/catalog/list', method: 'GET' })

export const createPayment = (data) => request({ url: '/api/app/payment/create', method: 'POST', data })

export const getCaregiver = () => request({ url: '/api/app/caregiver', method: 'GET' })
