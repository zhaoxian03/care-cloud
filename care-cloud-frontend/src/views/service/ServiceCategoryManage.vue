<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>服务分类管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" style="width: 100%" />
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
import { listServiceCategories, createServiceCategory, updateServiceCategory, deleteServiceCategory } from '../../api/serviceCategory'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({ name: '', sort: 0 })
const formRules = { name: [{ required: true, message: '请输入名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await listServiceCategories()
    if (res.code === 200) tableData.value = res.data || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

const openDialog = (row) => {
  isEdit.value = !!row
  editId.value = row ? row.id : null
  formData.name = row ? row.name : ''
  formData.sort = row ? row.sort : 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateServiceCategory(editId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      await createServiceCategory(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类 ${row.name} 吗？`, '提示', { type: 'warning' })
    await deleteServiceCategory(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') { /* handled */ } }
}

onMounted(() => loadData())
</script>
