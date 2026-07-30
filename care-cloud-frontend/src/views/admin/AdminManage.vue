<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增管理员
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索姓名/账号" clearable style="width: 250px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="search">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchKeyword = ''; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="roleNames" label="角色" min-width="160">
          <template #default="{ row }">
            <template v-if="row.roleNames && row.roleNames.length > 0">
              <el-tag v-for="name in row.roleNames" :key="name" size="small" style="margin-right: 4px">
                {{ name }}
              </el-tag>
            </template>
            <el-tag v-else-if="row.roleLevel === 'super_admin'" type="danger" size="small">超级管理员</el-tag>
            <span v-else style="color: #909399">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAssignRole(row)">
              分配角色
            </el-button>
            <el-button 
              v-if="row.roleLevel !== 'super_admin'" 
              :type="row.status === 1 ? 'danger' : 'success'" 
              link size="small" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              v-if="row.roleLevel !== 'super_admin'" 
              type="danger" 
              link size="small" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
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

    <el-dialog v-model="dialogVisible" title="新增管理员" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="formData.username" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px" destroy-on-close>
      <el-checkbox-group v-model="selectedRoleIds">
        <div v-for="role in allRoles" :key="role.id" style="margin-bottom: 8px">
          <el-checkbox :label="role.id">
            {{ role.name }} <el-tag size="small" type="info" style="margin-left: 4px">{{ role.code }}</el-tag>
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageAdmins, createAdmin, updateAdminStatus, deleteAdmin } from '../../api/admin'
import { getAdminRoles, saveAdminRoles, getRoleList } from '../../api/role'

const loading = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const formRef = ref(null)

const roleDialogVisible = ref(false)
const currentAssignAdminId = ref(null)
const allRoles = ref([])
const selectedRoleIds = ref([])

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
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const resetForm = () => {
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.phone = ''
}

const openDialog = (row) => {
  resetForm()
  dialogVisible.value = true
}

const search = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageAdmins({
      page: pagination.page,
      size: pagination.pageSize,
      keyword: searchKeyword.value
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
  submitLoading.value = true
  try {
    await createAdmin(formData)
    ElMessage.success('新增成功')
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
    await ElMessageBox.confirm(`确定要${action}管理员 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateAdminStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除管理员 ${row.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAdmin(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

async function handleAssignRole(row) {
  currentAssignAdminId.value = row.id
  roleDialogVisible.value = true

  const [rolesRes, adminRolesRes] = await Promise.all([
    getRoleList(),
    getAdminRoles(row.id)
  ])
  if (rolesRes.code === 200) {
    allRoles.value = rolesRes.data
  }
  if (adminRolesRes.code === 200) {
    selectedRoleIds.value = adminRolesRes.data
  }
}

async function handleSaveRoles() {
  await saveAdminRoles(currentAssignAdminId.value, selectedRoleIds.value)
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>
