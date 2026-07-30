<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>客户订阅管理</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>新增订阅
          </el-button>
        </div>
      </template>
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索客户姓名" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterCatalogId" placeholder="按服务筛选" clearable style="width: 200px" @change="loadData">
          <el-option v-for="c in catalogs" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="按状态筛选" clearable style="width: 120px" @change="loadData">
          <el-option label="活跃" value="ACTIVE" />
          <el-option label="已过期" value="EXPIRED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-button type="primary" @click="search"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="searchKeyword = ''; filterCatalogId = ''; filterStatus = ''; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="catalogName" label="服务名称" min-width="140" />
        <el-table-column prop="categoryName" label="服务分类" width="120" />
        <el-table-column label="价格" width="90" align="center">
          <template #default="{ row }">{{ row.price ? '¥' + row.price : '-' }}</template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="到期日期" width="110">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.endDate && isExpiringSoon(row.endDate) }">{{ row.endDate || '长期' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '活跃' : row.status === 'EXPIRED' ? '已过期' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 'ACTIVE'" type="primary" link size="small" @click="handleRenew(row)">续期</el-button>
            <el-button v-if="row.status === 'ACTIVE'" type="danger" link size="small" @click="handleCancel(row)">取消</el-button>
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
    <el-dialog v-model="showCreateDialog" title="新增服务订阅" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="formData.customerId" placeholder="请选择客户" filterable style="width: 100%">
            <el-option v-for="c in customerList" :key="c.id" :label="c.realName + ' (' + c.phone + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务项目" prop="catalogId">
          <el-select v-model="formData.catalogId" placeholder="请选择服务" style="width: 100%">
            <el-option v-for="s in catalogs" :key="s.id" :label="s.categoryName + ' - ' + s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="formData.startDate" type="date" placeholder="选择开始日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="到期日期" prop="endDate">
          <el-date-picker v-model="formData.endDate" type="date" placeholder="选择到期日期（可选）" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageSubscriptions, createSubscription, deleteSubscription, renewSubscription } from '../../api/subscription'
import { listServiceCatalogs } from '../../api/serviceCatalog'
import { pageCustomers } from '../../api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const filterCatalogId = ref('')
const filterStatus = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const catalogs = ref([])
const customerList = ref([])
const showCreateDialog = ref(false)
const formRef = ref(null)
const formData = reactive({ customerId: null, catalogId: null, startDate: '', endDate: '' })
const formRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  catalogId: [{ required: true, message: '请选择服务', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{
    validator: (rule, value, callback) => {
      if (value && formData.startDate && value < formData.startDate) {
        callback(new Error('到期日期不能早于开始日期'))
      } else { callback() }
    }, trigger: 'change'
  }]
}

const isExpiringSoon = (endDate) => {
  if (!endDate) return false
  const soon = new Date(); soon.setDate(soon.getDate() + 7)
  return new Date(endDate) <= soon
}

const search = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageSubscriptions({
      page: pagination.page, size: pagination.pageSize,
      catalogId: filterCatalogId.value || undefined,
      status: filterStatus.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

const loadCatalogs = async () => {
  const res = await listServiceCatalogs({ isActive: 1 })
  if (res.code === 200) catalogs.value = res.data || []
}

const loadCustomers = async () => {
  const res = await pageCustomers({ page: 1, size: 9999, status: 1 })
  if (res.code === 200) customerList.value = res.data.records || []
}

const handleCreate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await createSubscription({
      customerId: formData.customerId,
      catalogId: formData.catalogId,
      startDate: formData.startDate,
      endDate: formData.endDate || null
    })
    ElMessage.success('订阅成功')
    showCreateDialog.value = false
    formData.customerId = null; formData.catalogId = null; formData.startDate = ''; formData.endDate = ''
    loadData()
  } catch (e) { /* handled */ }
  finally { submitLoading.value = false }
}

const handleRenew = async (row) => {
  try {
    const { value: newEndDate } = await ElMessageBox.prompt('请输入新的到期日期 (YYYY-MM-DD)', '续期', {
      confirmButtonText: '确定', cancelButtonText: '取消',
      inputPattern: /^\d{4}-\d{2}-\d{2}$/, inputErrorMessage: '日期格式不正确'
    })
    await renewSubscription(row.id, { newEndDate })
    ElMessage.success('续期成功')
    loadData()
  } catch (e) { if (e !== 'cancel') { /* handled */ } }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消 ${row.customerName} 的 ${row.catalogName} 订阅吗？`, '提示', { type: 'warning' })
    await deleteSubscription(row.id)
    ElMessage.success('已取消订阅')
    loadData()
  } catch (e) { if (e !== 'cancel') { /* handled */ } }
}

onMounted(() => { loadCatalogs(); loadCustomers(); loadData() })
</script>
