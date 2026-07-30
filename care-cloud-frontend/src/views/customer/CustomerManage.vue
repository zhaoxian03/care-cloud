<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增客户
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索姓名/手机号" clearable style="width: 250px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px" @change="loadData">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="search">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchKeyword = ''; filterStatus = ''; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="姓名" width="120">
          <template #default="{ row }">
            <el-popover trigger="hover" placement="right" :width="180" popper-class="avatar-popover">
              <template #reference>
                <span class="name-cell">{{ row.realName }}</span>
              </template>
              <div class="avatar-preview-card">
                <div class="avatar-preview-img" v-if="row.avatarUrl">
                  <img :src="minioBaseUrl + '/' + row.avatarUrl" alt="头像" />
                </div>
                <div class="avatar-preview-empty" v-else>
                  <el-icon :size="40"><UserFilled /></el-icon>
                  <span>暂无头像</span>
                </div>
                <div class="avatar-preview-name">{{ row.realName }}</div>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="gender" label="性别" width="80" align="center" />
        <el-table-column prop="selfCareAbility" label="自理能力" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.selfCareAbility === '自理'" type="success">自理</el-tag>
            <el-tag v-else-if="row.selfCareAbility === '介助'" type="warning">介助</el-tag>
            <el-tag v-else-if="row.selfCareAbility === '介护'" type="danger">介护</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="健康管家" width="100" align="center">
          <template #default="{ row }">
            <el-button type="success" link size="small" @click="openCaregiverDialog(row)">
              <el-icon><UserFilled /></el-icon> 管家
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="服务订阅" width="100" align="center">
          <template #default="{ row }">
            <el-button type="warning" link size="small" @click="openSubscriptionDialog(row)">
              <el-icon><Ticket /></el-icon> 订阅
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'danger' : 'success'" link size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="formData.age" :min="1" :max="150" style="width: 100%" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人手机号" />
        </el-form-item>
        <el-form-item label="关系" prop="emergencyRelation">
          <el-input v-model="formData.emergencyRelation" placeholder="请输入与紧急联系人关系" />
        </el-form-item>
        <el-form-item label="自理能力" prop="selfCareAbility">
          <el-select v-model="formData.selfCareAbility" placeholder="请选择自理能力" style="width: 100%">
            <el-option label="自理" value="自理" />
            <el-option label="介助" value="介助" />
            <el-option label="介护" value="介护" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像（可选）">
          <div class="avatar-upload-area">
            <div class="avatar-upload-preview">
              <img
                v-if="formData.avatarUrl"
                :src="minioBaseUrl + '/' + formData.avatarUrl"
                alt="头像预览"
              />
              <el-icon v-else :size="32"><UserFilled /></el-icon>
            </div>
            <div class="avatar-upload-actions">
              <el-upload
                :show-file-list="false"
                :http-request="handleDialogUpload"
                accept="image/*"
              >
                <el-button type="primary" size="small">上传头像</el-button>
              </el-upload>
              <el-button
                v-if="formData.avatarUrl"
                type="danger"
                size="small"
                link
                @click="formData.avatarUrl = ''"
              >移除</el-button>
              <div class="avatar-upload-tip">支持 JPG/PNG/WebP，建议 200×200 以上</div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="caregiverDialogVisible" :title="caregiverDialogTitle" width="600px" destroy-on-close @open="loadBoundCaregivers" @close="resetCaregiverState">
      <el-table :data="boundCaregivers" v-loading="caregiverLoading" border stripe>
        <template #empty>
          <span>暂未绑定健康管家</span>
        </template>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="createDate" label="绑定日期" width="120" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="showBindSelector = true">
          <el-icon><Plus /></el-icon>绑定管家
        </el-button>
        <el-button @click="caregiverDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showBindSelector" title="选择健康管家" width="500px" destroy-on-close append-to-body @open="loadAvailableCaregivers">
      <el-table :data="availableCaregivers" v-loading="availableLoading" border stripe>
        <template #empty>
          <span>没有可绑定的健康管家</span>
        </template>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="boundElderCount" label="服务老人数" width="100" align="center" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleBind(row)">绑定</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showBindSelector = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="subscriptionDialogVisible" :title="subscriptionDialogTitle" width="700px" destroy-on-close @open="loadSubscriptions" @close="resetSubscriptionState">
      <el-table :data="subscriptionList" v-loading="subscriptionLoading" border stripe>
        <template #empty>
          <span>暂未订阅任何服务</span>
        </template>
        <el-table-column prop="catalogName" label="服务名称" min-width="140" />
        <el-table-column prop="categoryName" label="服务分类" width="110" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="到期日期" width="110">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isExpiringSoon(row.endDate) }">{{ row.endDate || '长期' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '活跃' : row.status === 'EXPIRED' ? '已过期' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 'ACTIVE'" type="primary" link size="small" @click="handleRenew(row)">续期</el-button>
            <el-button v-if="row.status === 'ACTIVE'" type="danger" link size="small" @click="handleDeleteSubscription(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="showCreateSubscription = true">
          <el-icon><Plus /></el-icon>新增订阅
        </el-button>
        <el-button @click="subscriptionDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCreateSubscription" title="新增服务订阅" width="500px" destroy-on-close append-to-body>
      <el-form ref="subFormRef" :model="subForm" :rules="subFormRules" label-width="100px">
        <el-form-item label="服务项目" prop="catalogId">
          <el-select v-model="subForm.catalogId" placeholder="请选择服务" style="width: 100%">
            <el-option v-for="s in catalogList" :key="s.id" :label="s.categoryName + ' - ' + s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="subForm.startDate" type="date" placeholder="选择开始日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="到期日期" prop="endDate">
          <el-date-picker v-model="subForm.endDate" type="date" placeholder="选择到期日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateSubscription = false">取消</el-button>
        <el-button type="primary" :loading="subSubmitLoading" @click="handleCreateSubscription">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageCustomers, createCustomer, updateCustomer, updateCustomerStatus, deleteCustomer } from '../../api/customer'
import { bindCaregiver, unbindCaregiver, getCaregiversByCustomer, getAvailableCaregivers } from '../../api/caregiver'
import { pageSubscriptions, createSubscription, deleteSubscription, renewSubscription } from '../../api/subscription'
import { listServiceCatalogs } from '../../api/serviceCatalog'
import { uploadFile } from '../../api/storage'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const minioBaseUrl = 'http://localhost:9000'

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const originalPhone = ref('')

const formRef = ref(null)

const formData = reactive({
  phone: '',
  realName: '',
  age: null,
  gender: '',
  emergencyContact: '',
  emergencyRelation: '',
  selfCareAbility: '',
  avatarUrl: ''
})

const formRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const resetForm = () => {
  formData.phone = ''
  formData.realName = ''
  formData.age = null
  formData.gender = ''
  formData.emergencyContact = ''
  formData.emergencyRelation = ''
  formData.selfCareAbility = ''
  formData.avatarUrl = ''
  editId.value = null
  originalPhone.value = ''
}

const openDialog = (row) => {
  resetForm()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    formData.phone = row.phone
    originalPhone.value = row.phone
    formData.realName = row.realName
    formData.age = row.age
    formData.gender = row.gender
    formData.emergencyContact = row.emergencyContact
    formData.emergencyRelation = row.emergencyRelation
    formData.selfCareAbility = row.selfCareAbility
    formData.avatarUrl = row.avatarUrl || ''
  } else {
    isEdit.value = false
  }
  dialogVisible.value = true
}

const search = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageCustomers({
      page: pagination.page,
      size: pagination.pageSize,
      keyword: searchKeyword.value,
      status: filterStatus.value
    })
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

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (isEdit.value && formData.phone !== originalPhone.value) {
    try {
      await ElMessageBox.confirm('您修改了手机号，确定要保存吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch (e) {
      if (e === 'cancel') return
    }
  }

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCustomer(editId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      await createCustomer(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}客户 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateCustomerStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

const handleDialogUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file)
    if (res.code === 200) {
      formData.avatarUrl = res.data
      ElMessage.success('头像上传成功')
    }
  } catch (e) {
    // handled
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除客户 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCustomer(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

const caregiverDialogVisible = ref(false)
const caregiverDialogTitle = ref('')
const caregiverLoading = ref(false)
const currentCustomer = ref(null)
const boundCaregivers = ref([])

const showBindSelector = ref(false)
const availableCaregivers = ref([])
const availableLoading = ref(false)

const openCaregiverDialog = (row) => {
  currentCustomer.value = row
  caregiverDialogTitle.value = `${row.realName} 的健康管家`
  caregiverDialogVisible.value = true
}

const loadBoundCaregivers = async () => {
  if (!currentCustomer.value) return
  caregiverLoading.value = true
  try {
    const res = await getCaregiversByCustomer(currentCustomer.value.id)
    if (res.code === 200) {
      boundCaregivers.value = res.data || []
    }
  } catch (e) {
    // handled
  } finally {
    caregiverLoading.value = false
  }
}

const resetCaregiverState = () => {
  boundCaregivers.value = []
  currentCustomer.value = null
  showBindSelector.value = false
  availableCaregivers.value = []
}

const loadAvailableCaregivers = async () => {
  if (!currentCustomer.value) return
  availableLoading.value = true
  try {
    const res = await getAvailableCaregivers()
    if (res.code === 200) {
      const boundIds = new Set(boundCaregivers.value.map(c => c.adminId))
      availableCaregivers.value = (res.data || []).filter(c => !boundIds.has(c.id))
    }
  } catch (e) {
    // handled
  } finally {
    availableLoading.value = false
  }
}

const handleBind = async (caregiver) => {
  try {
    await ElMessageBox.confirm(`确定要绑定健康管家 ${caregiver.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await bindCaregiver({
      customerId: currentCustomer.value.id,
      adminId: caregiver.id
    })
    ElMessage.success('绑定成功')
    showBindSelector.value = false
    loadBoundCaregivers()
  } catch (e) {
    if (e !== 'cancel') { /* handled by interceptor */ }
  }
}

const handleUnbind = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要解绑健康管家 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unbindCaregiver(row.relationId)
    ElMessage.success('解绑成功')
    loadBoundCaregivers()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

const subscriptionDialogVisible = ref(false)
const subscriptionDialogTitle = ref('')
const subscriptionLoading = ref(false)
const subscriptionList = ref([])

const showCreateSubscription = ref(false)
const subSubmitLoading = ref(false)
const subFormRef = ref(null)
const subForm = reactive({
  catalogId: null,
  startDate: '',
  endDate: ''
})
const subFormRules = {
  catalogId: [{ required: true, message: '请选择服务项目', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{
    validator: (rule, value, callback) => {
      if (value && subForm.startDate && value < subForm.startDate) {
        callback(new Error('到期日期不能早于开始日期'))
      } else {
        callback()
      }
    },
    trigger: 'change'
  }]
}
const catalogList = ref([])

const isExpiringSoon = (endDate) => {
  if (!endDate) return false
  const soon = new Date()
  soon.setDate(soon.getDate() + 7)
  return new Date(endDate) <= soon
}

const openSubscriptionDialog = (row) => {
  currentCustomer.value = row
  subscriptionDialogTitle.value = `${row.realName} 的服务订阅`
  subscriptionDialogVisible.value = true
  loadCatalogs()
}

const loadCatalogs = async () => {
  try {
    const res = await listServiceCatalogs({ isActive: 1 })
    if (res.code === 200) catalogList.value = res.data || []
  } catch (e) {
    // handled
  }
}

const loadSubscriptions = async () => {
  if (!currentCustomer.value) return
  subscriptionLoading.value = true
  try {
    const res = await pageSubscriptions({ customerId: currentCustomer.value.id, page: 1, size: 999 })
    if (res.code === 200) {
      subscriptionList.value = res.data.records || []
    }
  } catch (e) {
    // handled
  } finally {
    subscriptionLoading.value = false
  }
}

const resetSubscriptionState = () => {
  subscriptionList.value = []
  showCreateSubscription.value = false
  subForm.catalogId = null
  subForm.startDate = ''
  subForm.endDate = ''
}

const handleCreateSubscription = async () => {
  const valid = await subFormRef.value.validate().catch(() => false)
  if (!valid) return

  subSubmitLoading.value = true
  try {
    await createSubscription({
      customerId: currentCustomer.value.id,
      catalogId: subForm.catalogId,
      startDate: subForm.startDate,
      endDate: subForm.endDate || null
    })
    ElMessage.success('订阅成功')
    showCreateSubscription.value = false
    loadSubscriptions()
  } catch (e) {
    // handled
  } finally {
    subSubmitLoading.value = false
  }
}

const handleRenew = async (row) => {
  try {
    const { value: newEndDate } = await ElMessageBox.prompt('请输入新的到期日期 (YYYY-MM-DD)', '续期', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^\d{4}-\d{2}-\d{2}$/,
      inputErrorMessage: '日期格式不正确'
    })
    await renewSubscription(row.id, { newEndDate })
    ElMessage.success('续期成功')
    loadSubscriptions()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

const handleDeleteSubscription = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消订阅 ${row.catalogName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSubscription(row.id)
    ElMessage.success('已取消订阅')
    loadSubscriptions()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.name-cell {
  cursor: default;
  color: #409eff;
}

.avatar-upload-area {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.avatar-upload-preview {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  border: 1px dashed #dcdfe6;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  flex-shrink: 0;
  color: #c0c4cc;
}

.avatar-upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-upload-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-upload-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
