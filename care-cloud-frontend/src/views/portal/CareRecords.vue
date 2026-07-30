<template>
  <div>
    <el-card shadow="hover">
      <template #header><span>我的护理记录</span></template>
      <el-table :data="list" border stripe v-loading="loading" size="medium">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="recordTime" label="时间" width="100" />
        <el-table-column prop="careItemName" label="护理项目" />
        <el-table-column prop="nurseName" label="护理人员" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status===2?'success':row.status===1?'warning':'info'" size="small">
              {{ row.status===2?'已完成':row.status===1?'执行中':'待执行' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev,pager,next,total"
          @current-change="load"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCareList } from '../../api/portal'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function load() {
  loading.value = true
  const res = await getCareList({ page: page.value, size: size.value })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
  loading.value = false
}

onMounted(load)
</script>
