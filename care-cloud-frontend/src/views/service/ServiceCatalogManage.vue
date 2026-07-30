<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>服务产品目录</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增服务
          </el-button>
        </div>
      </template>
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索服务名称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterCategory" placeholder="按分类筛选" clearable style="width: 200px" @change="loadData">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="search"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="searchKeyword = ''; filterCategory = ''; loadData()"><el-icon><Refresh /></el-icon>重置</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="服务名称" min-width="140" />
        <el-table-column prop="categoryName" label="所属分类" width="120" />
        <el-table-column label="定价" width="100" align="center">
          <template #default="{ row }">{{ row.price ? '¥' + row.price : '-' }}</template>
        </el-table-column>
        <el-table-column label="计价单位" width="100" align="center">
          <template #default="{ row }">{{ unitLabel(row.unit) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'info'">{{ row.isActive === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务' : '新增服务'" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="定价" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计价单位" prop="unit">
          <el-select v-model="formData.unit" placeholder="请选择" style="width: 100%">
            <el-option label="次" value="once" />
            <el-option label="日" value="day" />
            <el-option label="周" value="week" />
            <el-option label="月" value="month" />
            <el-option label="年" value="year" />
            <el-option label="长期" value="long" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否上架" prop="isActive">
          <el-switch v-model="formData.isActive" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="服务描述" />
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
import { Refresh } from '@element-plus/icons-vue'
import { pageServiceCatalogs, createServiceCatalog, updateServiceCatalog, deleteServiceCatalog } from '../../api/serviceCatalog'
import { listServiceCategories } from '../../api/serviceCategory'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const categories = ref([])
const filterCategory = ref('')
const searchKeyword = ref('')
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({ categoryId: null, name: '', price: 0, unit: 'month', isActive: 1, description: '' })
const formRules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const unitLabel = (unit) => ({ once: '次', day: '日', week: '周', month: '月', year: '年', long: '长期' }[unit] || unit)

const search = () => {
  pagination.page = 1
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pageServiceCatalogs({
      page: pagination.page, size: pagination.pageSize,
      categoryId: filterCategory.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

const loadCategories = async () => {
  const res = await listServiceCategories()
  if (res.code === 200) categories.value = res.data || []
}

const openDialog = (row) => {
  isEdit.value = !!row
  editId.value = row ? row.id : null
  formData.categoryId = row ? row.categoryId : null
  formData.name = row ? row.name : ''
  formData.price = row ? row.price : 0
  formData.unit = row ? row.unit : 'month'
  formData.isActive = row ? row.isActive : 1
  formData.description = row ? row.description : ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateServiceCatalog(editId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      await createServiceCatalog(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除服务 ${row.name} 吗？`, '提示', { type: 'warning' })
    await deleteServiceCatalog(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') { /* handled */ } }
}

onMounted(() => { loadCategories(); loadData() })
</script>
