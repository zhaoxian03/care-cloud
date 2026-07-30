<template>
  <div>
    <el-card shadow="hover">
      <template #header><span>我的管家</span></template>
      <el-empty v-if="!caregivers || caregivers.length===0" description="暂无绑定管家" />
      <div v-for="cg in caregivers" :key="cg.relationId" style="padding:16px">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ cg.realName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ cg.phone }}</el-descriptions-item>
          <el-descriptions-item label="绑定时间">{{ cg.createDate }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCaregiver } from '../../api/portal'

const caregivers = ref([])

onMounted(async () => {
  const res = await getCaregiver()
  if (res.code === 200) caregivers.value = res.data || []
})
</script>
