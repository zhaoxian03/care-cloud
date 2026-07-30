<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-icon today"><el-icon><Calendar /></el-icon></div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.todayCount || 0 }}</div>
              <div class="stats-label">今日护理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-icon pending"><el-icon><Clock /></el-icon></div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pendingCount || 0 }}</div>
              <div class="stats-label">待执行</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-icon progress"><el-icon><Loading /></el-icon></div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.inProgressCount || 0 }}</div>
              <div class="stats-label">执行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-icon completed"><el-icon><CircleCheck /></el-icon></div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.completedCount || 0 }}</div>
              <div class="stats-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 护理记录表格 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>护理记录</span>
          <div class="header-actions">
            <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>批量删除 ({{ selectedIds.length }})
            </el-button>
            <el-button type="primary" @click="openDialog(null)">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索客户姓名（留空显示全部）" clearable style="width: 300px" @clear="doSearch" @keyup.enter="doSearch" />
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px">
          <el-option label="待执行" :value="0" />
          <el-option label="执行中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-button type="primary" @click="doSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchKeyword = ''; filterStatus = ''; doSearch()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table
        :data="recordData"
        v-loading="recordLoading"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="userName" label="客户姓名" width="120" />
        <el-table-column prop="careItemName" label="护理项目" width="150" />
        <el-table-column prop="nurseName" label="护理人员" width="120" />
        <el-table-column label="执行时间" width="170">
          <template #default="{ row }">
            {{ row.recordDate }} {{ row.recordTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="statusTagType(row.status)"
              style="cursor: pointer"
              @click="toggleStatus(row)"
            >
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRecords"
        @current-change="loadRecords"
      />
    </el-card>

    <!-- 添加/编辑护理记录对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑护理记录' : '添加护理记录'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="选择客户" prop="customerId">
          <el-select
            v-model="formData.customerId"
            filterable
            remote
            :remote-method="searchCustomers"
            :loading="customerLoading"
            placeholder="请输入客户姓名搜索"
            style="width: 100%"
            :disabled="isEdit"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="`${item.realName} (${item.phone})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="护理项目" prop="careItemId">
          <el-select v-model="formData.careItemId" placeholder="请选择护理项目" style="width: 100%" :disabled="isEdit">
            <el-option
              v-for="item in careItems"
              :key="item.id"
              :label="item.itemName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="护理人员" v-if="!isEdit">
          <el-select v-model="formData.adminId" placeholder="请选择护理人员（留空为当前用户）" clearable style="width: 100%">
            <el-option
              v-for="item in nurseOptions"
              :key="item.id"
              :label="`${item.realName} (${item.username})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addCareRecord, pageAllCareRecords,
  deleteCareRecord, updateCareRecordStatus, updateCareRecord,
  batchDeleteCareRecords, getCareStats, listCareItems
} from '../../api/care'
import { pageCustomers } from '../../api/customer'
import { pageAdmins } from '../../api/admin'

const submitLoading = ref(false)

// 统计数据
const stats = ref({})

// 客户选择
const searchKeyword = ref('')
const customerOptions = ref([])
const customerLoading = ref(false)

// 状态筛选
const filterStatus = ref('')

// 护理项目列表
const careItems = ref([])

// 表格数据
const recordLoading = ref(false)
const recordData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

// 多选
const selectedIds = ref([])

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({
  customerId: null,
  careItemId: null,
  adminId: null,
  remark: ''
})
const nurseOptions = ref([])
const formRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  careItemId: [{ required: true, message: '请选择护理项目', trigger: 'change' }]
}

// 状态文本
const statusText = (status) => {
  const map = { 0: '待执行', 1: '执行中', 2: '已完成' }
  return map[status] || '未知'
}

// 状态标签类型
const statusTagType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success' }
  return map[status] || 'info'
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getCareStats()
    if (res.code === 200) {
      stats.value = res.data || {}
    }
  } catch (e) {
    // handled
  }
}

// 搜索客户
const searchCustomers = async (keyword) => {
  if (!keyword) return
  customerLoading.value = true
  try {
    const res = await pageCustomers({ page: 1, size: 20, keyword })
    if (res.code === 200) {
      customerOptions.value = res.data.records || []
    }
  } catch (e) {
    // handled
  } finally {
    customerLoading.value = false
  }
}

// 加载护理项目
const loadCareItems = async () => {
  try {
    const res = await listCareItems()
    if (res.code === 200) {
      careItems.value = res.data || []
    }
  } catch (e) {
    // handled
  }
}

// 搜索
const doSearch = () => {
  pagination.page = 1
  loadRecords()
}

// 加载护理记录
const loadRecords = async () => {
  recordLoading.value = true
  try {
    let res
    const params = {
      page: pagination.page,
      size: pagination.pageSize
    }
    if (filterStatus.value !== '' && filterStatus.value !== null) {
      params.status = filterStatus.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    res = await pageAllCareRecords(params)
    if (res.code === 200) {
      recordData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    // handled
  } finally {
    recordLoading.value = false
  }
}

// 多选变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 打开对话框
const openDialog = async (row) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    formData.customerId = row.customerId
    formData.careItemId = row.careItemId
    formData.remark = row.remark || ''
  } else {
    isEdit.value = false
    editId.value = null
    formData.customerId = null
    formData.careItemId = null
    formData.adminId = null
    formData.remark = ''
    // 加载护理人员列表
    if (nurseOptions.value.length === 0) {
      const res = await pageAdmins({ page: 1, size: 100 })
      if (res.code === 200) nurseOptions.value = res.data.records || []
    }
  }
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCareRecord({
        id: editId.value,
        customerId: formData.customerId,
        careItemId: formData.careItemId,
        remark: formData.remark
      })
      ElMessage.success('护理记录更新成功')
    } else {
      await addCareRecord({
        customerId: formData.customerId,
        careItemId: formData.careItemId,
        adminId: formData.adminId || undefined,
        remark: formData.remark
      })
      ElMessage.success('护理记录添加成功')
    }
    dialogVisible.value = false
    loadRecords()
    loadStats()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该护理记录吗？', '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await deleteCareRecord(row.id)
    ElMessage.success('删除成功')
    loadRecords()
    loadStats()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条记录吗？`, '批量删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await batchDeleteCareRecords(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadRecords()
    loadStats()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 切换状态
const toggleStatus = async (row) => {
  const statusMap = { 0: 1, 1: 2, 2: 0 }
  const newStatus = statusMap[row.status] ?? 0
  const statusTextMap = { 0: '待执行', 1: '执行中', 2: '已完成' }
  const statusText = statusTextMap[newStatus]

  try {
    await ElMessageBox.confirm(`确定要将状态改为"${statusText}"吗？`, '状态切换确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateCareRecordStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadRecords()
    loadStats()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

onMounted(() => {
  loadRecords()
  loadCareItems()
  loadStats()
})
</script>

<style scoped>
.stats-row {
  margin-bottom: 20px;
}
.stats-card {
  cursor: pointer;
}
.stats-content {
  display: flex;
  align-items: center;
  gap: 15px;
}
.stats-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}
.stats-icon.today {
  background: linear-gradient(135deg, #409eff, #337ecc);
}
.stats-icon.pending {
  background: linear-gradient(135deg, #e6a23c, #cf9236);
}
.stats-icon.progress {
  background: linear-gradient(135deg, #f56c6c, #c45656);
}
.stats-icon.completed {
  background: linear-gradient(135deg, #67c23a, #529b2e);
}
.stats-info {
  flex: 1;
}
.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
</style>
