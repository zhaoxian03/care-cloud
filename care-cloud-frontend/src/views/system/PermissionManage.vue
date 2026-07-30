<template>
  <div class="permission-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限资源管理</span>
          <el-button type="primary" @click="handleAdd(null)">添加根权限</el-button>
        </div>
      </template>

      <el-table
        :data="permissionTree"
        row-key="id"
        border
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="权限名称" min-width="180" />
        <el-table-column prop="code" label="权限编码" min-width="160" />
        <el-table-column prop="type" label="权限类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'MENU' ? 'primary' : 'info'" size="small">
              {{ row.type === 'MENU' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="前端路径" width="180" />
        <el-table-column prop="backUrl" label="后端URL" min-width="200" />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAdd(row)">添加下级</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该权限吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑权限' : '添加权限'"
      width="550px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="如：客户管理" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="如：customer:view" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'MENU'" label="前端路径">
          <el-input v-model="form.path" placeholder="如：/customer" />
        </el-form-item>
        <el-form-item v-if="form.type === 'MENU'" label="菜单图标">
          <el-input v-model="form.icon" placeholder="如：User" />
        </el-form-item>
        <el-form-item label="后端URL" prop="backUrl">
          <el-input v-model="form.backUrl" placeholder="如：/api/customer/**（多个用逗号分隔）" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '../../api/permission'

const permissionTree = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  code: '',
  type: 'MENU',
  path: '',
  icon: '',
  backUrl: '',
  sort: 0,
  parentId: null
})

const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
  backUrl: [{ required: true, message: '请输入后端URL', trigger: 'blur' }]
}

async function loadData() {
  const res = await getPermissionTree()
  if (res.code === 200) {
    permissionTree.value = res.data
  }
}

function handleAdd(parent) {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.code = ''
  form.type = 'MENU'
  form.path = ''
  form.icon = ''
  form.backUrl = ''
  form.sort = 0
  form.parentId = parent ? parent.id : null
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.type = row.type
  form.path = row.path || ''
  form.icon = row.icon || ''
  form.backUrl = row.backUrl || ''
  form.sort = row.sort || 0
  form.parentId = row.parentId
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const data = {
    name: form.name,
    code: form.code,
    type: form.type,
    path: form.path || null,
    icon: form.icon || null,
    backUrl: form.backUrl || null,
    sort: form.sort,
    parentId: form.parentId
  }

  if (isEdit.value) {
    await updatePermission(form.id, data)
    ElMessage.success('更新成功')
  } else {
    await createPermission(data)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await deletePermission(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.permission-container {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
