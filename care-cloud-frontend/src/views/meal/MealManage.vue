<template>
  <div class="page-container">
    <!-- 全部膳食记录表格 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>膳食日历</span>
          <div>
            <el-button type="success" @click="handleCopyWeek">
              <el-icon><CopyDocument /></el-icon>复制下周膳食
            </el-button>
            <el-button type="primary" @click="openAddDialog">
              <el-icon><Plus /></el-icon>自定义膳食
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索客户姓名（留空显示全部）" clearable style="width: 300px" @clear="doSearch" @keyup.enter="doSearch" />
        <el-button type="primary" @click="doSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="searchKeyword = ''; doSearch()">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>

      
      <el-table :data="mealData" v-loading="loading" border stripe>
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="mealDate" label="日期" width="120" />
        <el-table-column label="星期" width="80" align="center">
          <template #default="{ row }">
            {{ getWeekDay(row.mealDate) }}
          </template>
        </el-table-column>
        <el-table-column label="早餐" min-width="220">
          <template #default="{ row }">
            <div v-if="hasMeal(row, 1)" class="meal-with-actions">
              <div class="meal-dishes">{{ getMealDisplay(row, 1) }}</div>
              <div class="meal-actions">
                <el-button
                  :type="getMeal(row, 1).status === 0 ? 'success' : 'info'"
                  link size="small"
                  @click="toggleStatus(getMeal(row, 1))">
                  {{ getMeal(row, 1).status === 0 ? '停用' : '启用' }}
                </el-button>
                <el-button type="primary" link size="small" @click="openEditDialog(getMeal(row, 1))">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(getMeal(row, 1).id)">删除</el-button>
              </div>
            </div>
            <div v-else>
              <span class="text-muted">未设置</span>
              <el-button type="primary" link size="small" @click="openAddForMeal(row, 1)">添加</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="午餐" min-width="220">
          <template #default="{ row }">
            <div v-if="hasMeal(row, 2)" class="meal-with-actions">
              <div class="meal-dishes">{{ getMealDisplay(row, 2) }}</div>
              <div class="meal-actions">
                <el-button
                  :type="getMeal(row, 2).status === 0 ? 'success' : 'info'"
                  link size="small"
                  @click="toggleStatus(getMeal(row, 2))">
                  {{ getMeal(row, 2).status === 0 ? '停用' : '启用' }}
                </el-button>
                <el-button type="primary" link size="small" @click="openEditDialog(getMeal(row, 2))">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(getMeal(row, 2).id)">删除</el-button>
              </div>
            </div>
            <div v-else>
              <span class="text-muted">未设置</span>
              <el-button type="primary" link size="small" @click="openAddForMeal(row, 2)">添加</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="晚餐" min-width="220">
          <template #default="{ row }">
            <div v-if="hasMeal(row, 3)" class="meal-with-actions">
              <div class="meal-dishes">{{ getMealDisplay(row, 3) }}</div>
              <div class="meal-actions">
                <el-button
                  :type="getMeal(row, 3).status === 0 ? 'success' : 'info'"
                  link size="small"
                  @click="toggleStatus(getMeal(row, 3))">
                  {{ getMeal(row, 3).status === 0 ? '停用' : '启用' }}
                </el-button>
                <el-button type="primary" link size="small" @click="openEditDialog(getMeal(row, 3))">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(getMeal(row, 3).id)">删除</el-button>
              </div>
            </div>
            <div v-else>
              <span class="text-muted">未设置</span>
              <el-button type="primary" link size="small" @click="openAddForMeal(row, 3)">添加</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadMeals"
        @current-change="loadMeals"
      />
    </el-card>

    <!-- 添加/编辑膳食对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑膳食' : '自定义膳食'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="选择客户" prop="customerId">
          <el-select
            v-model="formData.customerId"
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
        <el-form-item label="膳食日期" prop="mealDate">
          <el-date-picker
            v-model="formData.mealDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="膳食类型" prop="mealTypes">
          <el-checkbox-group v-model="formData.mealTypes">
            <el-checkbox :label="1">早餐</el-checkbox>
            <el-checkbox :label="2">午餐</el-checkbox>
            <el-checkbox :label="3">晚餐</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="选择菜品">
          <el-tabs type="border-card" v-if="formData.mealTypes.length > 0">
            <el-tab-pane v-for="mt in formData.mealTypes" :key="mt" :label="mealTypeLabel(mt)" :name="String(mt)">
              <el-select
                v-model="selectedDishMap[mt]"
                multiple
                filterable
                :placeholder="'请选择' + mealTypeLabel(mt) + '菜品（可多选）'"
                style="width: 100%"
              >
                <el-option-group
                  v-for="group in dishGroups"
                  :key="group.category"
                  :label="group.category"
                >
                  <el-option
                    v-for="item in group.items"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-option-group>
              </el-select>
            </el-tab-pane>
          </el-tabs>
          <span v-else class="text-muted">请先选择膳食类型</span>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addMealCustom, addMealCustomBatch, updateMealCustom, pageMeals,
  deleteMealCustom, updateMealStatus, copyNextWeek, listDishes
} from '../../api/meal'
import { pageCustomers } from '../../api/customer'

const loading = ref(false)
const submitLoading = ref(false)

// 筛选
const searchKeyword = ref('')

// 菜品相关
const allDishes = ref([])
const selectedDishMap = reactive({ 1: [], 2: [], 3: [] })

// 按分类分组的菜品
const dishGroups = computed(() => {
  const groups = {}
  allDishes.value.forEach(dish => {
    const category = dish.category || '其他'
    if (!groups[category]) {
      groups[category] = []
    }
    groups[category].push(dish)
  })
  return Object.keys(groups).map(category => ({
    category,
    items: groups[category]
  }))
})

// 表格数据
const mealData = ref([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const formData = reactive({
  customerId: null,
  mealDate: '',
  mealTypes: [1, 2, 3]
})
const formRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  mealDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

// 下拉选项
const customerOptions = ref([])
const customerLoading = ref(false)

// 获取膳食显示内容
const getMealDisplay = (row, mealType) => {
  // 根据 mealType 获取对应的膳食内容
  const meal = row.meals?.find(m => m.mealType === mealType)
  if (!meal) return ''
  if (meal.dishNames && meal.dishNames.length > 0) {
    return meal.dishNames.join('、')
  }
  return ''
}
const getMeal = (row, mealType) => row.meals?.find(m => m.mealType === mealType)
const hasMeal = (row, mealType) => row.meals?.some(m => m.mealType === mealType)
const mealTypeLabel = (mt) => ({ 1: '早餐', 2: '午餐', 3: '晚餐' }[mt] || '')

// 获取星期
const getWeekDay = (dateStr) => {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const date = new Date(dateStr)
  return days[date.getDay()]
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

// 加载菜品列表
const loadDishes = async () => {
  try {
    const res = await listDishes()
    if (res.code === 200) {
      allDishes.value = res.data || []
    }
  } catch (e) {
    // handled
  }
}

// 搜索
const doSearch = () => {
  pagination.page = 1
  loadMeals()
}

// 加载膳食记录
const loadMeals = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.pageSize
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const res = await pageMeals(params)
    if (res.code === 200) {
      mealData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

// 限制不能选择今天之前的日期
const disabledDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

// 打开添加对话框
const openAddDialog = () => {
  isEdit.value = false
  editId.value = null
  formData.customerId = null
  formData.mealDate = ''
  formData.mealTypes = [1, 2, 3]
  selectedDishMap[1] = []
  selectedDishMap[2] = []
  selectedDishMap[3] = []
  dialogVisible.value = true
}

// 在表格中点击某餐次的"添加"按钮，补加缺失的餐次
const openAddForMeal = (row, mealType) => {
  isEdit.value = false
  editId.value = null
  formData.customerId = row.customerId
  formData.mealDate = row.mealDate
  formData.mealTypes = [mealType]
  selectedDishMap[1] = []
  selectedDishMap[2] = []
  selectedDishMap[3] = []
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (meal) => {
  isEdit.value = true
  editId.value = meal.id
  formData.customerId = meal.customerId
  formData.mealDate = meal.mealDate
  formData.mealTypes = [meal.mealType]
  selectedDishMap[1] = []
  selectedDishMap[2] = []
  selectedDishMap[3] = []
  selectedDishMap[meal.mealType] = meal.dishIds || []
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 至少选择一个餐次
  if (!formData.mealTypes || formData.mealTypes.length === 0) {
    ElMessage.warning('请至少选择一个餐次')
    return
  }

  // 至少为一个餐次选了菜品
  const hasAnyDish = formData.mealTypes.some(mt => selectedDishMap[mt] && selectedDishMap[mt].length > 0)
  if (!hasAnyDish && !isEdit.value) {
    ElMessage.warning('请至少为一个餐次选择菜品')
    return
  }

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const body = {
        id: editId.value,
        customerId: formData.customerId,
        mealDate: formData.mealDate,
        mealType: formData.mealTypes[0]
      }
      await updateMealCustom(body, selectedDishMap[formData.mealTypes[0]])
      ElMessage.success('膳食更新成功')
    } else {
      const mealTypeDishMap = {}
      formData.mealTypes.forEach(mt => {
        mealTypeDishMap[mt] = selectedDishMap[mt] || []
      })
      await addMealCustomBatch({
        customerId: formData.customerId,
        mealDate: formData.mealDate,
        mealTypes: formData.mealTypes,
        mealTypeDishMap
      })
      ElMessage.success('膳食添加成功')
    }
    dialogVisible.value = false
    loadMeals()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

// 删除膳食
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条膳食记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMealCustom(id)
    ElMessage.success('删除成功')
    loadMeals()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 切换状态
const toggleStatus = async (meal) => {
  const newStatus = meal.status === 0 ? 1 : 0
  const statusText = newStatus === 0 ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定要${statusText}该膳食记录吗？`, '状态切换确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateMealStatus(meal.id, newStatus)
    ElMessage.success(`${statusText}成功`)
    loadMeals()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

// 复制下周膳食
const handleCopyWeek = async () => {
  try {
    await ElMessageBox.confirm('确定要将本周膳食复制到下周吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await copyNextWeek()
    ElMessage.success('复制成功')
    loadMeals()
  } catch (e) {
    if (e !== 'cancel') {
      // handled
    }
  }
}

onMounted(() => {
  loadMeals()
  loadDishes()
})
</script>

<style scoped>
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
.meal-with-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.meal-dishes {
  line-height: 1.5;
}
.meal-actions {
  display: flex;
  gap: 2px;
  flex-wrap: nowrap;
}
.text-muted {
  color: #c0c4cc;
}
</style>
