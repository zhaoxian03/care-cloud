<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>菜品管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>添加菜品
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="菜品名称" width="200" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="isActive" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.isActive === 1 ? 'success' : 'info'"
              style="cursor: pointer"
              @click="toggleStatus(row)"
            >
              {{ row.isActive === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
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
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '添加菜品'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="主食" value="主食" />
            <el-option label="热菜" value="热菜" />
            <el-option label="凉菜" value="凉菜" />
            <el-option label="汤" value="汤" />
            <el-option label="水果" value="水果" />
            <el-option label="饮品" value="饮品" />
            <el-option label="其他" value="其他" />
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
import { pageDishes, addDish, updateDish, deleteDish } from '../../api/meal'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({
  name: '',
  category: ''
})
const formRules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageDishes({
      page: pagination.page,
      size: pagination.pageSize
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

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    formData.name = row.name
    formData.category = row.category
  } else {
    isEdit.value = false
    editId.value = null
    formData.name = ''
    formData.category = ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDish({
        id: editId.value,
        name: formData.name,
        category: formData.category
      })
      ElMessage.success('菜品更新成功')
    } else {
      await addDish({
        name: formData.name,
        category: formData.category
      })
      ElMessage.success('菜品添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除菜品"${row.name}"吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await deleteDish(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.isActive === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定要${statusText}菜品"${row.name}"吗？`, '状态切换确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateDish({
      id: row.id,
      name: row.name,
      category: row.category,
      isActive: newStatus
    })
    ElMessage.success(`${statusText}成功`)
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
</style>
