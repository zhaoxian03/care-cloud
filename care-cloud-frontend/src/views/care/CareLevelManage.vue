<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>护理等级</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon>添加等级
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="levelName" label="等级名称" width="150" />
        <el-table-column prop="price" label="每日费用（元）" width="150" align="right" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
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
            <el-button type="primary" link size="small" @click="openConfigDialog(row)">配置项目</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加对话框 -->
    <el-dialog v-model="dialogVisible" title="添加护理等级" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="formData.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="每日费用" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 配置护理项目对话框 -->
    <el-dialog v-model="configDialogVisible" title="配置护理项目" width="600px" destroy-on-close>
      <div class="config-header">
        <span>当前等级：<strong>{{ currentLevel?.levelName }}</strong></span>
      </div>
      <el-transfer
        v-model="selectedItemIds"
        :data="allItems"
        :titles="['可选项目', '已选项目']"
        :props="{ key: 'id', label: 'itemName' }"
        style="width: 100%"
      />
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="configLoading" @click="handleSaveConfig">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listCareLevels, addCareLevel, deleteCareLevel, updateCareLevel,
  listCareItems, getCareLevelItems, saveCareLevelItems
} from '../../api/care'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

// 添加对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const formData = reactive({
  levelName: '',
  price: 0,
  description: ''
})
const formRules = {
  levelName: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入每日费用', trigger: 'blur' }]
}

// 配置项目对话框
const configDialogVisible = ref(false)
const configLoading = ref(false)
const currentLevel = ref(null)
const allItems = ref([])
const selectedItemIds = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await listCareLevels()
    if (res.code === 200) {
      tableData.value = res.data || []
    }
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  formData.levelName = ''
  formData.price = 0
  formData.description = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addCareLevel(formData)
    ElMessage.success('护理等级添加成功')
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
    await ElMessageBox.confirm(`确定要删除护理等级"${row.levelName}"吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await deleteCareLevel(row.id)
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
    await ElMessageBox.confirm(`确定要${statusText}护理等级"${row.levelName}"吗？`, '状态切换确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateCareLevel({
      id: row.id,
      levelName: row.levelName,
      price: row.price,
      description: row.description,
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

// 打开配置项目对话框
const openConfigDialog = async (row) => {
  currentLevel.value = row
  selectedItemIds.value = []
  configLoading.value = false

  try {
    // 并行加载所有护理项目和当前等级已关联的项目
    const [itemsRes, levelItemsRes] = await Promise.all([
      listCareItems(),
      getCareLevelItems(row.id)
    ])

    if (itemsRes.code === 200) {
      allItems.value = itemsRes.data || []
    }
    if (levelItemsRes.code === 200) {
      selectedItemIds.value = (levelItemsRes.data || []).map(item => item.careItemId)
    }

    configDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载数据失败')
  }
}

// 保存配置
const handleSaveConfig = async () => {
  configLoading.value = true
  try {
    await saveCareLevelItems(currentLevel.value.id, selectedItemIds.value)
    ElMessage.success('配置保存成功')
    configDialogVisible.value = false
  } catch (e) {
    ElMessage.error('配置保存失败')
  } finally {
    configLoading.value = false
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
.config-header {
  margin-bottom: 15px;
  font-size: 14px;
}
</style>
