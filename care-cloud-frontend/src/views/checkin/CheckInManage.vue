<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>入住管理</span>
          <el-button type="primary" @click="openCheckInDialog">
            <el-icon><Plus /></el-icon>入住登记
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索客户姓名" clearable style="width: 220px" @clear="doSearch" @keyup.enter="doSearch" />
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px">
          <el-option label="入住中" :value="0" />
          <el-option label="已退住" :value="1" />
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
        <el-table-column prop="userName" label="客户姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" align="center" />
        <el-table-column prop="careLevelName" label="护理级别" width="120" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{ row.status === 0 ? '入住中' : '已退住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="warning" link size="small" @click="handleCheckOut(row)">退住</el-button>
            <span v-else class="text-muted">-</span>
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

    <!-- 入住登记对话框 -->
    <el-dialog v-model="checkInDialogVisible" title="入住登记" width="550px" destroy-on-close>
      <el-form ref="checkInFormRef" :model="checkInForm" :rules="checkInRules" label-width="100px">
        <el-form-item label="选择客户" prop="userId">
          <el-select
            v-model="checkInForm.userId"
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
        <el-form-item label="选择床位" prop="bedId">
          <el-select v-model="checkInForm.bedId" placeholder="请选择空闲床位" style="width: 100%">
            <el-option
              v-for="item in bedOptions"
              :key="item.id"
              :label="`${item.roomNumber} - ${item.bedNumber}号床`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="护理级别" prop="careLevelId">
          <el-select v-model="checkInForm.careLevelId" placeholder="请选择护理级别" style="width: 100%">
            <el-option
              v-for="item in careLevelOptions"
              :key="item.id"
              :label="item.levelName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker
            v-model="checkInForm.checkInDate"
            type="date"
            placeholder="选择入住日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCheckIn">确定入住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { checkIn, checkOut, pageCheckIn } from '../../api/checkin'
import { getFreeBeds } from '../../api/bed'
import { pageCustomers } from '../../api/customer'
import request from '../../api/request'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

// 入住登记相关
const checkInDialogVisible = ref(false)
const checkInFormRef = ref(null)
const checkInForm = reactive({
  userId: null,
  bedId: null,
  careLevelId: null,
  checkInDate: ''
})

const checkInRules = {
  userId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  bedId: [{ required: true, message: '请选择床位', trigger: 'change' }],
  careLevelId: [{ required: true, message: '请选择护理级别', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
}

// 下拉选项
const customerOptions = ref([])
const customerLoading = ref(false)
const bedOptions = ref([])
const careLevelOptions = ref([])

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
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

// 加载空闲床位
const loadFreeBeds = async () => {
  try {
    const res = await getFreeBeds()
    if (res.code === 200) {
      bedOptions.value = res.data || []
    }
  } catch (e) {
    // handled
  }
}

// 加载护理级别
const loadCareLevels = async () => {
  try {
    const res = await request({ url: '/api/carelevel/list', method: 'get' })
    if (res.code === 200) {
      careLevelOptions.value = res.data || []
    }
  } catch (e) {
    // 如果API不存在，使用默认选项
    careLevelOptions.value = [
      { id: 1, levelName: '一级护理' },
      { id: 2, levelName: '二级护理' },
      { id: 3, levelName: '三级护理' }
    ]
  }
}

// 打开入住登记对话框
const openCheckInDialog = () => {
  checkInForm.userId = null
  checkInForm.bedId = null
  checkInForm.careLevelId = null
  checkInForm.checkInDate = ''
  customerOptions.value = []
  loadFreeBeds()
  loadCareLevels()
  checkInDialogVisible.value = true
}

// 加载入住记录
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
    const res = await pageCheckIn(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) {
  } finally {
    loading.value = false
  }
}

// 提交入住登记
const handleCheckIn = async () => {
  const valid = await checkInFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await checkIn(checkInForm)
    ElMessage.success('入住登记成功')
    checkInDialogVisible.value = false
    loadData()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 退住
const handleCheckOut = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为 ${row.userName} 办理退住吗？退住后床位将释放。`,
      '退住确认',
      { confirmButtonText: '确定退住', cancelButtonText: '取消', type: 'warning' }
    )
    await checkOut(row.id)
    ElMessage.success('退住成功')
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
