<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span>我的服务订阅</span>
        <el-button type="primary" size="small" style="float:right" @click="showCreate">新增订阅</el-button>
      </template>
      <el-table :data="list" border stripe v-loading="loading" size="medium">
        <el-table-column prop="catalogName" label="服务名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }">{{ formatPrice(row) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status==='ACTIVE'?'success':row.status==='PENDING'?'warning':row.status==='EXPIRED'?'warning':'danger'" size="small">
              {{ labelStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始" width="110" />
        <el-table-column label="到期" width="110">
          <template #default="{ row }">
            <span v-if="row.catalogUnit==='long'">长期有效</span>
            <span v-else>{{ row.endDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <el-button v-if="row.status==='ACTIVE' && row.catalogUnit!=='once' && row.catalogUnit!=='long'" type="primary" size="small" @click="openRenew(row)">续订</el-button>
            <el-button v-if="row.status==='ACTIVE'" type="danger" size="small" @click="doCancel(row)">取消</el-button>
            <el-tag v-if="row.status==='PENDING'" size="small" type="warning">等待支付</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev,pager,next,total" @current-change="load" />
      </div>
    </el-card>

    <el-dialog v-model="renewVisible" title="续订服务" width="420px">
      <el-form label-width="80px">
        <el-form-item label="服务">{{ renewRow.catalogName }}</el-form-item>
        <el-form-item label="计价单位">{{ labelUnit(renewRow.catalogUnit) }}</el-form-item>
        <el-form-item label="续约数量">
          <el-input-number v-model="renewDuration" :min="1" :max="999" />
          <span style="margin-left:8px;color:#909399">{{ labelUnit(renewRow.catalogUnit) }}</span>
        </el-form-item>
        <el-form-item label="新到期日">{{ computedEndDate }}</el-form-item>
        <el-form-item label="续约总价"><strong style="color:#e6a23c">¥{{ computedPrice }}</strong></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible=false">取消</el-button>
        <el-button type="primary" :loading="renewing" @click="doRenew">确定续订</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="createVisible" title="新增服务订阅" width="500px">
      <el-form label-width="80px">
        <el-form-item label="服务产品">
          <el-select v-model="createCatalogId" placeholder="选择服务" style="width:100%" @change="onCatalogChange">
            <el-option v-for="c in catalogs" :key="c.id" :label="`${c.name} (¥${c.price}/${labelUnit(c.unit)})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="createStartDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" style="width:100%" :disabled-date="d => d.getTime() < Date.now() - 86400000" />
        </el-form-item>
        <el-form-item label="订阅时长">
          <el-input-number v-model="createDuration" :min="1" :max="999" :disabled="createUnit==='once'||createUnit==='long'" />
          <span style="margin-left:8px;color:#909399">{{ labelUnit(createUnit) }}</span>
        </el-form-item>
        <el-form-item label="总价"><strong style="color:#e6a23c">¥{{ createPrice }}</strong></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="doCreate">确认订阅</el-button>
      </template>
    </el-dialog>

    <div id="alipay_form" v-html="alipayFormHtml" style="display:none"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSubscriptionList, cancelSubscription, createSubscription, getServiceCatalogs, createPayment } from '../../api/portal'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const alipayFormHtml = ref('')

async function load() {
  loading.value = true
  const res = await getSubscriptionList({ page: page.value, size: size.value })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
  loading.value = false
}

function formatPrice(row) {
  const p = row.catalogPrice != null ? row.catalogPrice : row.price
  const u = row.catalogUnit ? '/' + labelUnit(row.catalogUnit) : ''
  return p != null ? '¥' + p + u : '-'
}

function labelUnit(u) {
  const map = { day: '天', week: '周', month: '月', year: '年', once: '次', long: '长期' }
  return map[u] || u || '-'
}

function labelStatus(s) {
  const map = { ACTIVE: '生效中', PENDING: '待支付', EXPIRED: '已过期', CANCELLED: '已取消' }
  return map[s] || s
}

// Renew
const renewVisible = ref(false)
const renewing = ref(false)
const renewRow = ref({})
const renewDuration = ref(1)

const computedEndDate = computed(() => {
  const end = renewRow.value.endDate
  const u = renewRow.value.catalogUnit
  if (u === 'long') return '长期有效'
  if (u === 'once') return '单次服务'
  if (!end || !u) return '-'
  const d = new Date(end + 'T00:00:00')
  const dur = renewDuration.value
  if (u === 'day') d.setDate(d.getDate() + dur)
  else if (u === 'week') d.setDate(d.getDate() + dur * 7)
  else if (u === 'month') d.setMonth(d.getMonth() + dur)
  else if (u === 'year') d.setFullYear(d.getFullYear() + dur)
  return d.toISOString().slice(0, 10)
})

const computedPrice = computed(() => {
  const p = renewRow.value.catalogPrice
  if (!p) return '0.00'
  return (Number(p) * renewDuration.value).toFixed(2)
})

function openRenew(row) {
  renewRow.value = row
  renewDuration.value = 1
  renewVisible.value = true
}

async function doRenew() {
  renewing.value = true
  try {
    renewVisible.value = false
    const amount = Number(renewRow.value.catalogPrice) * renewDuration.value
    await triggerPayment(amount, renewRow.value.catalogName + ' - 续订', 'RENEW', renewRow.value.id, renewDuration.value)
    load()
  } catch (e) { /* handled */ }
  renewing.value = false
}

async function doCancel(row) {
  try {
    await ElMessageBox.confirm(`确定要取消"${row.catalogName}"吗？取消后不可恢复。`, '确认取消', { type: 'warning' })
  } catch { return }
  try {
    await cancelSubscription(row.id)
    ElMessage.success('已取消')
    load()
  } catch (e) { /* handled */ }
}

// Create
const createVisible = ref(false)
const creating = ref(false)
const catalogs = ref([])
const createCatalogId = ref(null)
const createStartDate = ref('')
const createDuration = ref(1)
const createUnit = ref('')
const createPrice = computed(() => {
  const c = catalogs.value.find(x => x.id === createCatalogId.value)
  if (!c || !c.price) return '0.00'
  if (createUnit.value === 'once' || createUnit.value === 'long') return Number(c.price).toFixed(2)
  return (Number(c.price) * createDuration.value).toFixed(2)
})

async function showCreate() {
  const res = await getServiceCatalogs()
  catalogs.value = (res.code === 200 ? res.data : []).filter(c => c.isActive === 1)
  createCatalogId.value = null
  createStartDate.value = new Date().toISOString().slice(0, 10)
  createDuration.value = 1
  createUnit.value = ''
  createVisible.value = true
}

function onCatalogChange(id) {
  const c = catalogs.value.find(x => x.id === id)
  createUnit.value = c ? c.unit : ''
  createDuration.value = 1
}

async function doCreate() {
  if (!createCatalogId.value) { ElMessage.warning('请选择服务'); return }
  if (!createStartDate.value) { ElMessage.warning('请选择开始日期'); return }
  const c = catalogs.value.find(x => x.id === createCatalogId.value)
  const dur = (c && (c.unit === 'once' || c.unit === 'long')) ? null : createDuration.value
  creating.value = true
  try {
    const res = await createSubscription({ catalogId: createCatalogId.value, duration: dur, startDate: createStartDate.value })
    if (res.code === 200 && res.data && res.data.subscriptionId) {
      createVisible.value = false
      const amount = Number(createPrice.value)
      await triggerPayment(amount, c.name + ' - 订阅', 'SUBSCRIPTION', res.data.subscriptionId)
    }
    load()
  } catch (e) { /* handled */ }
  creating.value = false
}

async function triggerPayment(amount, subject, bizType, bizId, duration) {
  const res = await createPayment({ totalAmount: amount, subject, bizType, bizId, duration })
  if (res.code === 200 && res.data) {
    alipayFormHtml.value = res.data
    await nextTick()
    const form = document.querySelector('#alipay_form form')
    if (form) form.submit()
  }
}

onMounted(load)
</script>
