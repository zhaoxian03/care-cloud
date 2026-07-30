<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>床位管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增床位
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchRoom" placeholder="搜索房间号" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px" @change="loadData">
          <el-option label="空闲" :value="0" />
          <el-option label="占用" :value="1" />
        </el-select>
        <el-button type="primary" @click="search">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchRoom = ''; filterStatus = ''; loadData()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column prop="bedNumber" label="床号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" align="center" />
        <el-table-column prop="orientation" label="朝向" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '空闲' : '占用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑床位' : '新增床位'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="formData.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="床号" prop="bedNumber">
          <el-input v-model="formData.bedNumber" placeholder="请输入床号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="formData.floor" :min="1" :max="99" style="width: 100%" />
        </el-form-item>
        <el-form-item label="朝向" prop="orientation">
          <el-select v-model="formData.orientation" placeholder="请选择朝向" style="width: 100%">
            <el-option label="南" value="南" />
            <el-option label="北" value="北" />
            <el-option label="东" value="东" />
            <el-option label="西" value="西" />
            <el-option label="东南" value="东南" />
            <el-option label="东北" value="东北" />
            <el-option label="西南" value="西南" />
            <el-option label="西北" value="西北" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { pageBeds, addBed, updateBed, deleteBed } from '../../api/bed'

const loading = ref(false)
const submitLoading = ref(false)
const searchRoom = ref('')
const filterStatus = ref('')
const tableData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const formData = reactive({
  roomNumber: '',
  bedNumber: '',
  floor: 1,
  orientation: '',
  remark: ''
})

const formRules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  bedNumber: [{ required: true, message: '请输入床号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }]
}

const resetForm = () => {
  formData.roomNumber = ''
  formData.bedNumber = ''
  formData.floor = 1
  formData.orientation = ''
  formData.remark = ''
  editId.value = null
}

const openDialog = (row) => {
  resetForm()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    formData.roomNumber = row.roomNumber
    formData.bedNumber = row.bedNumber
    formData.floor = row.floor
    formData.orientation = row.orientation
    formData.remark = row.remark
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
    const res = await pageBeds({
      page: pagination.page,
      size: pagination.pageSize,
      roomNumber: searchRoom.value,
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
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateBed(editId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      await addBed(formData)
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

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除房间 ${row.roomNumber} - 床位 ${row.bedNumber} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBed(row.id)
    ElMessage.success('删除成功')
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
