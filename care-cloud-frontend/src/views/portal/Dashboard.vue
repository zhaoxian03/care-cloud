<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span>老人信息</span></template>
          <el-descriptions v-if="data.customer" :column="2" border>
            <el-descriptions-item label="姓名">{{ data.customer.realName }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ data.customer.age }}岁</el-descriptions-item>
            <el-descriptions-item label="性别">{{ data.customer.gender }}</el-descriptions-item>
            <el-descriptions-item label="自理能力">
              <el-tag>{{ data.customer.selfCareAbility || '未设置' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="紧急联系人">{{ data.customer.emergencyContact || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="与联系人关系">{{ data.customer.emergencyRelation || '未设置' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>我的管家</span></template>
          <div v-if="data.caregivers && data.caregivers.length > 0">
            <div v-for="cg in data.caregivers" :key="cg.relationId" style="padding:8px 0;border-bottom:1px solid #ebeef5">
              <p><strong>{{ cg.realName }}</strong></p>
              <p style="color:#909399;font-size:12px">{{ cg.phone }}</p>
              <p style="color:#c0c4cc;font-size:12px">绑定：{{ cg.createDate }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无绑定管家" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>最近护理记录</span>
            <el-button type="text" size="small" style="float:right" @click="$router.push('/app/care')">查看全部</el-button>
          </template>
          <el-table :data="data.recentRecords || []" size="small">
            <el-table-column prop="recordDate" label="日期" width="110" />
            <el-table-column prop="careItemName" label="项目" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status===2?'success':row.status===1?'warning':'info'" size="small">
                  {{ row.status===2?'已完成':row.status===1?'执行中':'待执行' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!data.recentRecords || data.recentRecords.length===0" description="暂无护理记录" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>服务订阅</span>
            <el-button type="text" size="small" style="float:right" @click="$router.push('/app/subscription')">查看全部</el-button>
          </template>
          <el-table :data="data.subscriptions || []" size="small">
            <el-table-column prop="catalogName" label="服务" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status==='ACTIVE'?'success':'warning'" size="small">
                  {{ row.status==='ACTIVE'?'生效中':row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="endDate" label="到期" width="110" />
          </el-table>
          <div v-if="data.expiringSubscriptionCount>0" style="margin-top:10px">
            <el-alert type="warning" :closable="false" show-icon>
              {{ data.expiringSubscriptionCount }} 个服务即将到期
            </el-alert>
          </div>
          <el-empty v-if="!data.subscriptions || data.subscriptions.length===0" description="暂无服务订阅" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '../../api/portal'

const data = ref({})

onMounted(async () => {
  const res = await getDashboard()
  if (res.code === 200) data.value = res.data
})
</script>
