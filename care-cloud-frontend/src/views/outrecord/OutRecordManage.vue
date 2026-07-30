<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>外出管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>外出登记
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索客户姓名" clearable style="width: 220px" @clear="doSearch" @keyup.enter="doSearch" />
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px">
          <el-option label="外出中" :value="0" />
          <el-option label="已返回" :value="1" />
          <el-option label="逾期" :value="2" />
        </el-select>
        <el-button type="primary" @click="doSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchKeyword = ''; filterStatus = ''; doSearch()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="realName" label="客户姓名" width="120" />
        <el-table-column prop="reason" label="外出原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="外出时间" width="170">
          <template #default="{ row }">
            {{ row.outDate }} {{ row.outTime }}
          </template>
        </el-table-column>
        <el-table-column label="预计返回" width="170">
          <template #default="{ row }">
            {{ row.expectedBackDate && row.expectedBackTime ? `${row.expectedBackDate} ${row.expectedBackTime}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="实际返回" width="170">
          <template #default="{ row }">
            {{ row.actualBackDate && row.actualBackTime ? `${row.actualBackDate} ${row.actualBackTime}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link size="small" @click="handleBack(row)">返回</el-button>
              <el-button type="warning" link size="small" @click="handleForceBack(row)">强制返回</el-button>
            </template>

             <!-- 删除按钮始终显示 -->
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
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 外出登记对话框 -->
    <el-dialog v-model="addDialogVisible" title="外出登记" width="500px" destroy-on-close>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="选择客户" prop="customerId">
          <el-select
            v-model="addForm.customerId"
            filterable
            remote
            :remote-method="searchCustomers"
            :loading="customerLoading"
            placeholder="请输入客户姓名搜索"
            style="width: 100%"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="`${item.realName} (${item.phone})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="外出原因" prop="reason">
          <el-input v-model="addForm.reason" type="textarea" :rows="3" placeholder="请输入外出原因" />
        </el-form-item>
        <el-form-item label="预计返回日期" prop="expectedBackDate">
          <el-date-picker
            v-model="addForm.expectedBackDate"
            type="date"
            placeholder="选择预计返回日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预计返回时间" prop="expectedBackTime">
          <el-time-picker
            v-model="addForm.expectedBackTime"
            placeholder="选择预计返回时间"
            value-format="HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addOutRecord, backOutRecord, forceBackOutRecord, pageOutRecords ,deleteOutRecord} from '../../api/outrecord'
import { pageCustomers } from '../../api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

// 外出登记相关
const addDialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = reactive({
  customerId: null,
  reason: '',
  expectedBackDate: '',
  expectedBackTime: ''
})

const addRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  reason: [{ required: true, message: '请输入外出原因', trigger: 'blur' }],
  expectedBackDate: [{ required: true, message: '请选择预计返回日期', trigger: 'change' }],
  expectedBackTime: [{ required: true, message: '请选择预计返回时间', trigger: 'change' }]
}

// 下拉选项
const customerOptions = ref([])
const customerLoading = ref(false)

// 禁用过去日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

// 状态文本
const statusText = (status) => {
  const map = { 0: '外出中', 1: '已返回', 2: '逾期' }
  return map[status] || '未知'
}

// 状态标签类型
const statusTagType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
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

// 打开外出登记对话框
const openAddDialog = () => {
  addForm.customerId = null
  addForm.reason = ''
  addForm.expectedBackDate = ''
  addForm.expectedBackTime = ''
  customerOptions.value = []
  addDialogVisible.value = true
}

// 加载外出记录
const doSearch = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.pageSize
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterStatus.value !== '' && filterStatus.value !== null) {
      params.status = filterStatus.value
    }
    const res = await pageOutRecords(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

// 提交外出登记
const handleAdd = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addOutRecord(addForm)
    ElMessage.success('外出登记成功')
    addDialogVisible.value = false
    loadData()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 外出返回
const handleBack = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为 ${row.realName} 办理返回登记吗？`,
      '返回确认',
      { confirmButtonText: '确定返回', cancelButtonText: '取消', type: 'warning' }
    )
    await backOutRecord(row.id)
    ElMessage.success('返回登记成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 强制返回
const handleForceBack = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要强制将 ${row.realName} 标记为返回吗？`,
      '强制返回确认',
      { confirmButtonText: '确定强制返回', cancelButtonText: '取消', type: 'warning' }
    )
    await forceBackOutRecord(row.id)
    ElMessage.success('强制返回成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 删除记录
const handleDelete = async (row) => {
  try {    await ElMessageBox.confirm(
      `确定要删除 ${row.realName} 的外出记录吗？此操作不可恢复！`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'danger' }
    )
    // 这里调用删除接口，为 outrecord
    await deleteOutRecord(row.id)
    ElMessage.success('记录删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
.text-muted {
  color: #c0c4cc;
}
</style>
