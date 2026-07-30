<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span>本周膳食日历</span>
        <el-tag style="margin-left:10px" size="small" type="info">{{ dateRange }}</el-tag>
      </template>

      <el-table v-if="weekDays.length > 0" :data="weekDays" border stripe size="medium">
        <el-table-column prop="label" label="日期" width="150" />
        <el-table-column label="早餐">
          <template #default="{ row }">
            <el-tag v-for="d in row.breakfast" :key="d" size="small" style="margin:2px">{{ d }}</el-tag>
            <span v-if="!row.breakfast || row.breakfast.length===0" style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="午餐">
          <template #default="{ row }">
            <el-tag v-for="d in row.lunch" :key="d" size="small" type="success" style="margin:2px">{{ d }}</el-tag>
            <span v-if="!row.lunch || row.lunch.length===0" style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="晚餐">
          <template #default="{ row }">
            <el-tag v-for="d in row.dinner" :key="d" size="small" type="warning" style="margin:2px">{{ d }}</el-tag>
            <span v-if="!row.dinner || row.dinner.length===0" style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" :disabled="row.isPast" @click="editDay(row)">
              {{ row.hasAnyMeal ? '修改' : '定制' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无膳食安排" />
    </el-card>

    <el-dialog v-model="editVisible" title="定制膳食" width="520px">
      <el-form label-width="80px">
        <el-form-item label="日期"><span>{{ editRow.date || '-' }}</span></el-form-item>
        <el-form-item label="餐次">
          <el-checkbox-group v-model="editMealTypes">
            <el-checkbox v-for="type in mealTypeOptions" :key="type.value" :value="type.value">
              {{ type.label }}
              <el-tag v-if="type.existing" size="small" type="success" style="margin-left:4px">已有</el-tag>
              <el-tag v-else size="small" type="info" style="margin-left:4px">新增</el-tag>
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="菜品">
          <el-select v-model="editDishIds" multiple placeholder="选择菜品" style="width:100%">
            <el-option v-for="d in allDishes" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" :loading="editSaving" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getMealCalendar } from '../../api/portal'
import request from '../../api/request'

const rawMeals = ref([])
const allDishes = ref([])
const today = new Date().toISOString().slice(0, 10)

const mealTypeOptions = [
  { value: 1, label: '早餐', existing: false },
  { value: 2, label: '午餐', existing: false },
  { value: 3, label: '晚餐', existing: false }
]

function parseDate(str) {
  if (!str) return null
  if (str.length === 10) return str
  return str.slice(0, 10)
}

const weekDays = computed(() => {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const now = new Date()
  const monday = new Date(now)
  monday.setDate(now.getDate() - ((now.getDay() + 6) % 7))

  return days.map((label, i) => {
    const d = new Date(monday)
    d.setDate(monday.getDate() + i)
    const ds = d.toISOString().slice(0, 10)
    const dayMeals = rawMeals.value.filter(m => parseDate(m.mealDate) === ds)

    return {
      label: `${label} ${ds}`,
      date: ds,
      isPast: ds < today,
      hasAnyMeal: dayMeals.length > 0,
      rawMeals: dayMeals,
      breakfast: dayMeals.filter(m => m.mealType === 1).flatMap(m => m.dishNames || []),
      lunch: dayMeals.filter(m => m.mealType === 2).flatMap(m => m.dishNames || []),
      dinner: dayMeals.filter(m => m.mealType === 3).flatMap(m => m.dishNames || [])
    }
  })
})

const dateRange = computed(() => {
  if (weekDays.value.length === 0) return ''
  return `${weekDays.value[0].date} ~ ${weekDays.value[weekDays.value.length - 1].date}`
})

const editVisible = ref(false)
const editSaving = ref(false)
const editRow = ref({})
const editMealTypes = ref([])
const editDishIds = ref([])
const editMealTypeOptions = ref([])

onMounted(async () => {
  const [mealRes, dishRes] = await Promise.all([
    getMealCalendar(),
    request({ url: '/api/app/dish/list', method: 'GET' }).catch(() => ({ data: [] }))
  ])
  if (mealRes.code === 200) rawMeals.value = mealRes.data || []
  if (dishRes.code === 200) allDishes.value = dishRes.data || []
})

function editDay(row) {
  editRow.value = row
  editDishIds.value = []

  const existingTypes = new Set(row.rawMeals.map(m => m.mealType))
  const options = mealTypeOptions.map(o => ({
    ...o,
    existing: existingTypes.has(o.value)
  }))
  editMealTypeOptions.value = options

  // pre-check all meal types (user can uncheck)
  editMealTypes.value = [1, 2, 3]

  // pre-fill dish IDs from existing meals
  const allDishIds = new Set()
  row.rawMeals.forEach(m => {
    (m.dishIds || []).forEach(id => allDishIds.add(id))
  })
  editDishIds.value = [...allDishIds]

  editVisible.value = true
}

async function doSave() {
  if (editMealTypes.value.length === 0) {
    ElMessage.warning('请选择餐次')
    return
  }
  editSaving.value = true
  try {
    await request({
      method: 'POST',
      url: '/api/app/meal/custom',
      data: {
        mealDate: editRow.value.date,
        mealTypes: editMealTypes.value,
        dishIds: editDishIds.value
      }
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    const r = await getMealCalendar()
    if (r.code === 200) rawMeals.value = r.data || []
  } catch (e) {
    // handled by request interceptor
  }
  editSaving.value = false
}
</script>
