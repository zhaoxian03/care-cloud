<template>
  <div class="pay-page">
    <el-result :status="status" :title="title" :sub-title="subTitle">
      <template #extra>
        <el-button type="primary" @click="$router.push('/app/subscription')">返回服务订阅</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../api/request'

const route = useRoute()
const status = ref('success')
const title = ref('支付处理中...')
const subTitle = ref('正在验证支付结果')

onMounted(async () => {
  try {
    const params = { ...route.query }
    await request({ method: 'POST', url: '/api/payment/notify', params })
    title.value = '支付成功'
    subTitle.value = '您的订单已支付成功'
    status.value = 'success'
  } catch (e) {
    title.value = '支付验证中'
    subTitle.value = '如已支付，系统将自动更新订单状态'
    status.value = 'warning'
  }
})
</script>

<style scoped>
.pay-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f7fa; }
</style>
