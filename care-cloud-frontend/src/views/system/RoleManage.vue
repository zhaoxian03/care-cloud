<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加角色</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索角色名称/编码" style="width: 240px" clearable @keyup.enter="loadData" />
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        <el-button @click="keyword = ''; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="角色名称" min-width="140" />
        <el-table-column prop="code" label="角色编码" min-width="140" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="系统预置" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isSystem === 1 ? 'warning' : 'info'" size="small">
              {{ row.isSystem === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isDisabled === 1 ? 'danger' : 'success'" size="small">
              {{ row.isDisabled === 1 ? '禁用' : '启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAssignPermission(row)">分配权限</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.isSystem !== 1" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-button link :type="row.isDisabled === 1 ? 'success' : 'warning'" size="small" @click="handleToggleStatus(row)">
              {{ row.isDisabled === 1 ? '启用' : '禁用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="size"
        layout="total, prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '添加角色'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="如：超级管理员" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="如：super_admin" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="角色说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px" destroy-on-close>
      <el-tree
        ref="permTreeRef"
        :data="allPermissions"
        show-checkbox
        check-strictly
        node-key="id"
        :default-checked-keys="checkedPermissionIds"
        :props="{ label: 'name', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getRolePage, createRole, updateRole, deleteRole, updateRoleStatus, getRolePermissions, saveRolePermissions } from '../../api/role'
import { getPermissionTree } from '../../api/permission'

const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({ id: null, name: '', code: '', remark: '' })

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permDialogVisible = ref(false)
const permTreeRef = ref(null)
const allPermissions = ref([])
const checkedPermissionIds = ref([])
const currentRoleId = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getRolePage({ page: page.value, size: size.value, keyword: keyword.value })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.code = ''
  form.remark = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.remark = row.remark
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const data = { name: form.name, code: form.code, remark: form.remark }
  if (isEdit.value) {
    await updateRole(form.id, data)
    ElMessage.success('更新成功')
  } else {
    await createRole(data)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该角色吗？', '提示', { type: 'warning' })
  } catch { return }
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleToggleStatus(row) {
  const newStatus = row.isDisabled === 1 ? 0 : 1
  await updateRoleStatus(row.id, newStatus)
  ElMessage.success('操作成功')
  loadData()
}

async function handleAssignPermission(row) {
  currentRoleId.value = row.id
  permDialogVisible.value = true

  const [permRes, checkedRes] = await Promise.all([
    getPermissionTree(),
    getRolePermissions(row.id)
  ])
  if (permRes.code === 200) {
    allPermissions.value = permRes.data
  }
  if (checkedRes.code === 200) {
    checkedPermissionIds.value = checkedRes.data
  }
}

async function handleSavePermissions() {
  const checkedIds = permTreeRef.value.getCheckedKeys()
  await saveRolePermissions(currentRoleId.value, checkedIds)
  ElMessage.success('权限分配成功')
  permDialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-container {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
}
</style>
