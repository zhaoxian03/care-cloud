<template>
  <div class="page-container">
    <el-row :gutter="20">
      <!-- 入住统计 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>入住统计</span>
          </template>
          <div v-loading="occupancyLoading">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="总床位数">{{ occupancyData.totalBeds || 0 }}</el-descriptions-item>
              <el-descriptions-item label="已占用">{{ occupancyData.occupiedBeds || 0 }}</el-descriptions-item>
              <el-descriptions-item label="空闲床位">{{ occupancyData.freeBeds || 0 }}</el-descriptions-item>
              <el-descriptions-item label="入住率">
                <el-progress :percentage="occupancyData.occupancyRate || 0" :stroke-width="20" />
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷统计 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>快捷统计</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="stat-card">
                <div class="stat-value" style="color: #409eff">{{ occupancyData.totalCustomers || 0 }}</div>
                <div class="stat-label">客户总数</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-card">
                <div class="stat-value" style="color: #67c23a">{{ occupancyData.checkedInCount || 0 }}</div>
                <div class="stat-label">在住人数</div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="12">
              <div class="stat-card">
                <div class="stat-value" style="color: #e6a23c">{{ occupancyData.outingCount || 0 }}</div>
                <div class="stat-label">外出人数</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-card">
                <div class="stat-value" style="color: #f56c6c">{{ occupancyData.overdueCount || 0 }}</div>
                <div class="stat-label">逾期未归</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 护理工作量统计 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>护理工作量统计</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadWorkload"
          />
        </div>
      </template>

      <el-table :data="workloadData" v-loading="workloadLoading" border stripe>
        <el-table-column prop="nurseName" label="护理人员" width="150" />
        <el-table-column prop="careCount" label="护理次数" width="120" align="center" />
        <el-table-column prop="customerCount" label="服务客户数" width="120" align="center" />
        <el-table-column label="工作量占比" min-width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.workloadPercent || 0" :stroke-width="16" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOccupancyReport, getNurseWorkload } from '../../api/report'

const occupancyLoading = ref(false)
const occupancyData = ref({})
const workloadLoading = ref(false)
const workloadData = ref([])
const dateRange = ref([])

// 加载入住统计
const loadOccupancy = async () => {
  occupancyLoading.value = true
  try {
    const res = await getOccupancyReport()
    if (res.code === 200) {
      occupancyData.value = res.data || {}
    }
  } catch (e) {
    // handled
  } finally {
    occupancyLoading.value = false
  }
}

// 加载护理工作量
const loadWorkload = async () => {
  workloadLoading.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getNurseWorkload(params)
    if (res.code === 200) {
      workloadData.value = res.data || []
    }
  } catch (e) {
    // handled
  } finally {
    workloadLoading.value = false
  }
}

onMounted(() => {
  loadOccupancy()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-card {
  text-align: center;
  padding: 20px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
