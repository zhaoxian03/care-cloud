<template>
  <div>
    <el-card shadow="hover">
      <template #header><span>支付记录</span></template>

      <el-form :inline="true" :model="searchForm" style="margin-bottom:16px">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="输入订单号" clearable style="width:200px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:120px" @change="load">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已完成" value="SUCCESS" />
            <el-option label="已过期" value="EXPIRED" />
            <el-option label="已失败" value="FAILED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="250" show-overflow-tooltip />
        <el-table-column prop="subject" label="项目" show-overflow-tooltip />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createDate" label="创建日期" width="110" />
        <el-table-column prop="payTime" label="支付时间" width="100" />
      </el-table>

      <div style="margin-top:16px;text-align:right">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          @current-change="load"
          @size-change="load"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../api/request'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchForm = reactive({ orderNo: '', status: '' })

function statusLabel(s) {
  return { PENDING: '待支付', SUCCESS: '已完成', EXPIRED: '已过期', FAILED: '已失败' }[s] || s
}

function statusType(s) {
  return { PENDING: 'warning', SUCCESS: 'success', EXPIRED: 'danger', FAILED: 'danger' }[s] || 'info'
}

async function load() {
  loading.value = true
  const params = { page: page.value, size: size.value }
  if (searchForm.orderNo) params.orderNo = searchForm.orderNo
  if (searchForm.status) params.status = searchForm.status
  const res = await request({ url: '/api/app/payment/list', method: 'GET', params })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
  loading.value = false
}

function reset() {
  searchForm.orderNo = ''
  searchForm.status = ''
  page.value = 1
  load()
}

onMounted(load)
</script>
