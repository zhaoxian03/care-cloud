<template>
  <div class="page-container">
    <!-- 护理记录 -->
    <el-card shadow="hover" class="section-card">
      <template #header>
        <div class="card-header">
          <span>护理记录</span>
          <el-button type="primary" @click="openRecordDialog">
            <el-icon><Plus /></el-icon>添加记录
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-select
          v-model="selectedUserId"
          filterable
          remote
          clearable
          :remote-method="searchCustomers"
          :loading="customerLoading"
          placeholder="请输入客户姓名搜索（留空显示全部）"
          style="width: 300px"
          @change="loadRecords"
          @clear="loadRecords"
        >
          <el-option
            v-for="item in customerOptions"
            :key="item.id"
            :label="`${item.realName} (${item.phone})`"
            :value="item.id"
          />
        </el-select>
        <el-button type="primary" @click="loadRecords">
          <el-icon><Search /></el-icon>搜索
        </el-button>
      </div>

      <el-table :data="recordData" v-loading="recordLoading" border stripe>
        <el-table-column prop="id" label="记录ID" width="80" align="center" />
        <el-table-column prop="userName" label="客户姓名" width="120" />
        <el-table-column prop="careItemName" label="护理项目" width="150" />
        <el-table-column prop="nurseName" label="护理人员" width="120" />
        <el-table-column label="执行时间" width="170">
          <template #default="{ row }">
            {{ row.recordDate }} {{ row.recordTime }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="recordPagination.page"
        v-model:page-size="recordPagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="recordPagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRecords"
        @current-change="loadRecords"
      />
    </el-card>

    <!-- 护理等级 -->
    <el-card shadow="hover" class="section-card">
      <template #header>
        <div class="card-header">
          <span>护理等级</span>
          <el-button type="primary" @click="openLevelDialog">
            <el-icon><Plus /></el-icon>添加等级
          </el-button>
        </div>
      </template>

      <el-table :data="levelData" v-loading="levelLoading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="levelName" label="等级名称" width="150" />
        <el-table-column prop="price" label="每日费用（元）" width="150" align="right" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteLevel(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 护理项目 -->
    <el-card shadow="hover" class="section-card">
      <template #header>
        <div class="card-header">
          <span>护理项目</span>
          <el-button type="primary" @click="openItemDialog">
            <el-icon><Plus /></el-icon>添加项目
          </el-button>
        </div>
      </template>

      <el-table :data="itemData" v-loading="itemLoading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="itemName" label="项目名称" width="200" />
        <el-table-column prop="defaultDurationMinutes" label="预计耗时（分钟）" width="150" align="center" />
        <el-table-column prop="isActive" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'info'">
              {{ row.isActive === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加护理记录对话框 -->
    <el-dialog v-model="recordDialogVisible" title="添加护理记录" width="500px" destroy-on-close>
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="选择客户" prop="customerId">
          <el-select
            v-model="recordForm.customerId"
            filterable
            remote
            :remote-method="searchCustomers"
            :loading="customerLoading"
            placeholder="请输入客户姓名搜索"
            style="width: 100%"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="`${item.realName} (${item.phone})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="护理项目" prop="careItemId">
          <el-select v-model="recordForm.careItemId" placeholder="请选择护理项目" style="width: 100%">
            <el-option
              v-for="item in itemData"
              :key="item.id"
              :label="item.itemName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="recordForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddRecord">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加护理等级对话框 -->
    <el-dialog v-model="levelDialogVisible" title="添加护理等级" width="500px" destroy-on-close>
      <el-form ref="levelFormRef" :model="levelForm" :rules="levelRules" label-width="100px">
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="levelForm.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="每日费用" prop="price">
          <el-input-number v-model="levelForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="levelForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddLevel">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加护理项目对话框 -->
    <el-dialog v-model="itemDialogVisible" title="添加护理项目" width="500px" destroy-on-close>
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="itemForm.itemName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="预计耗时" prop="defaultDurationMinutes">
          <el-input-number v-model="itemForm.defaultDurationMinutes" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddItem">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listCareLevels, addCareLevel, deleteCareLevel,
  listCareItems, addCareItem, deleteCareItem,
  addCareRecord, pageAllCareRecords, pageCareRecords, deleteCareRecord
} from '../../api/care'
import { pageCustomers } from '../../api/customer'

const submitLoading = ref(false)

// 客户选择
const selectedUserId = ref(null)
const customerOptions = ref([])
const customerLoading = ref(false)

// 护理记录
const recordLoading = ref(false)
const recordData = ref([])
const recordPagination = reactive({ page: 1, pageSize: 10, total: 0 })
const recordDialogVisible = ref(false)
const recordFormRef = ref(null)
const recordForm = reactive({
  customerId: null,
  careItemId: null,
  remark: ''
})
const recordRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  careItemId: [{ required: true, message: '请选择护理项目', trigger: 'change' }]
}

// 护理等级
const levelLoading = ref(false)
const levelData = ref([])
const levelDialogVisible = ref(false)
const levelFormRef = ref(null)
const levelForm = reactive({
  levelName: '',
  price: 0,
  description: ''
})
const levelRules = {
  levelName: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入每日费用', trigger: 'blur' }]
}

// 护理项目
const itemLoading = ref(false)
const itemData = ref([])
const itemDialogVisible = ref(false)
const itemFormRef = ref(null)
const itemForm = reactive({
  itemName: '',
  defaultDurationMinutes: 30
})
const itemRules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
}

// 搜索客户
const searchCustomers = async (keyword) => {
  if (!keyword) return
  customerLoading.value = true
  try {
    const res = await pageCustomers({ page: 1, size: 20, keyword })
    if (res.code === 200) {
      customerOptions.value = res.data.records || []
    }
  } catch (e) {
    // handled
  } finally {
    customerLoading.value = false
  }
}

// 加载护理记录（支持按客户筛选或加载全部）
const loadRecords = async () => {
  recordLoading.value = true
  try {
    let res
    if (selectedUserId.value) {
      res = await pageCareRecords(selectedUserId.value, {
        page: recordPagination.page,
        size: recordPagination.pageSize
      })
    } else {
      res = await pageAllCareRecords({
        page: recordPagination.page,
        size: recordPagination.pageSize
      })
    }
    if (res.code === 200) {
      recordData.value = res.data.records || []
      recordPagination.total = res.data.total || 0
    }
  } catch (e) {
    // handled
  } finally {
    recordLoading.value = false
  }
}

// 加载护理等级
const loadLevels = async () => {
  levelLoading.value = true
  try {
    const res = await listCareLevels()
    if (res.code === 200) {
      levelData.value = res.data || []
    }
  } catch (e) {
    // handled
  } finally {
    levelLoading.value = false
  }
}

// 加载护理项目
const loadItems = async () => {
  itemLoading.value = true
  try {
    const res = await listCareItems()
    if (res.code === 200) {
      itemData.value = res.data || []
    }
  } catch (e) {
    // handled
  } finally {
    itemLoading.value = false
  }
}

// 打开对话框
const openRecordDialog = () => {
  recordForm.customerId = selectedUserId.value
  recordForm.careItemId = null
  recordForm.remark = ''
  recordDialogVisible.value = true
}

const openLevelDialog = () => {
  levelForm.levelName = ''
  levelForm.price = 0
  levelForm.description = ''
  levelDialogVisible.value = true
}

const openItemDialog = () => {
  itemForm.itemName = ''
  itemForm.defaultDurationMinutes = 30
  itemDialogVisible.value = true
}

// 提交护理记录
const handleAddRecord = async () => {
  const valid = await recordFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addCareRecord(recordForm)
    ElMessage.success('护理记录添加成功')
    recordDialogVisible.value = false
    loadRecords()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 提交护理等级
const handleAddLevel = async () => {
  const valid = await levelFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addCareLevel(levelForm)
    ElMessage.success('护理等级添加成功')
    levelDialogVisible.value = false
    loadLevels()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 提交护理项目
const handleAddItem = async () => {
  const valid = await itemFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addCareItem(itemForm)
    ElMessage.success('护理项目添加成功')
    itemDialogVisible.value = false
    loadItems()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 删除护理记录
const handleDeleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除该护理记录吗？`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'danger' }
    )
    await deleteCareRecord(row.id)
    ElMessage.success('删除成功')
    loadRecords()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 删除护理等级
const handleDeleteLevel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除护理等级"${row.levelName}"吗？`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'danger' }
    )
    await deleteCareLevel(row.id)
    ElMessage.success('删除成功')
    loadLevels()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 删除护理项目
const handleDeleteItem = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除护理项目"${row.itemName}"吗？`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'danger' }
    )
    await deleteCareItem(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadRecords()
  loadLevels()
  loadItems()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
.section-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
</style>
