<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>健康管家管理</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>新增管家
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
        <el-button @click="searchKeyword = ''; filterStatus = undefined; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="boundElderCount" label="服务老人数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createDate" label="创建日期" width="120" />
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑健康管家' : '新增健康管家'" width="450px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="账号" prop="username" v-if="!isEdit">
          <el-input v-model="formData.username" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码（至少6位）" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">{{ isEdit ? '保存' : '确定' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageCaregivers, updateCaregiverStatus, deleteCaregiver, createCaregiver, getCaregiver, updateCaregiver } from '../../api/caregiver'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref(undefined)
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({
  username: '',
  password: '',
  realName: '',
  phone: ''
})

const formRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const search = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageCaregivers({
      page: pagination.page,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
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

const resetForm = () => {
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.phone = ''
  editId.value = null
}

const openCreateDialog = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const openEditDialog = async (row) => {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  try {
    const res = await getCaregiver(row.id)
    if (res.code === 200) {
      formData.realName = res.data.realName || ''
      formData.phone = res.data.phone || ''
    }
  } catch (e) {
    // handled
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCaregiver(editId.value, {
        realName: formData.realName,
        phone: formData.phone
      })
      ElMessage.success('保存成功')
    } else {
      await createCaregiver(formData)
      ElMessage.success('创建成功')
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
    await ElMessageBox.confirm(`确定要${action}健康管家 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateCaregiverStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除健康管家 ${row.realName} 吗？\n注意：删除后该管家的账户将被禁用，绑定关系不受影响。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCaregiver(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') { /* handled */ }
  }
}

onMounted(() => {
  loadData()
})
</script>
