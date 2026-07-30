<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>护理项目</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon>添加项目
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="itemName" label="项目名称" width="200" />
        <el-table-column prop="defaultDurationMinutes" label="预计耗时（分钟）" width="150" align="center" />
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
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
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

    <!-- 添加对话框 -->
    <el-dialog v-model="dialogVisible" title="添加护理项目" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="formData.itemName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="预计耗时" prop="defaultDurationMinutes">
          <el-input-number v-model="formData.defaultDurationMinutes" :min="1" style="width: 100%" />
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
import { pageCareItems, addCareItem, deleteCareItem, updateCareItem } from '../../api/care'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

// 分页
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const formRef = ref(null)
const formData = reactive({
  itemName: '',
  defaultDurationMinutes: 30
})
const formRules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageCareItems({
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

const openDialog = () => {
  formData.itemName = ''
  formData.defaultDurationMinutes = 30
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addCareItem(formData)
    ElMessage.success('护理项目添加成功')
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
    await ElMessageBox.confirm(`确定要删除护理项目"${row.itemName}"吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await deleteCareItem(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 切换启用/停用状态
const toggleStatus = async (row) => {
  const newStatus = row.isActive === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定要${statusText}护理项目"${row.itemName}"吗？`, '状态切换确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateCareItem({
      id: row.id,
      itemName: row.itemName,
      defaultDurationMinutes: row.defaultDurationMinutes,
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
