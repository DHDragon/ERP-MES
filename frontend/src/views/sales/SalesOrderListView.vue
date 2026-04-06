<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="订单号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="router.push('/sales/orders/form')">新增订单</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="520">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/sales/orders/detail/${row.id}`)">详情</el-button>
          <el-button link type="success" @click="router.push(`/sales/orders/form/${row.id}`)">编辑</el-button>
          <el-button link type="warning" @click="submit(row.id)">提审</el-button>
          <el-button link type="success" @click="audit(row.id, true)">审核通过</el-button>
          <el-button link type="danger" @click="audit(row.id, false)">驳回</el-button>
          <el-button link @click="unAudit(row.id)">反审核</el-button>
          <el-button link @click="closeOrder(row.id)">关闭</el-button>
          <el-button link @click="voidOrder(row.id)">作废</el-button>
          <el-button link type="primary" @click="pushNotice(row.id)">下推发货通知</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-card shadow="never" style="margin-top:12px">
      <div style="font-weight:600;margin-bottom:8px">订单执行汇总</div>
      <el-table :data="summaryRows" border size="small">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="orderTotalQty" label="订单总数" width="120" />
        <el-table-column prop="deliveredQty" label="已执行" width="120" />
        <el-table-column prop="undeliveredQty" label="未执行" width="120" />
        <el-table-column prop="orderStatus" label="状态" width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { orderExecutionApi, salesOrderApi } from '../../api/modules/sales'
import type { OrderExecutionSummary, SalesOrderH } from '../../types/sales'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<SalesOrderH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const summaryRows = ref<OrderExecutionSummary[]>([])

const columns = computed(() => [
  { prop: 'orderNo', label: '订单号' },
  { prop: 'customerId', label: '客户ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '总数量' },
  { prop: 'deliveredQty', label: '已发数量' },
  { prop: 'approvalStatus', label: '审批状态' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const [listRes, summaryRes] = await Promise.all([
      salesOrderApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined }),
      orderExecutionApi.list({ pageNo: 1, pageSize: 20, keyword: query.keyword || undefined }),
    ])
    rows.value = listRes.data.records || []
    total.value = listRes.data.total || 0
    summaryRows.value = summaryRes.data.records || []
  } finally { loading.value = false }
}

const onPage = (p:number, s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const reset = ()=>{ query.keyword=''; pageNo.value=1; load() }

const submit = async(id:number)=>{ await salesOrderApi.submit(id); ElMessage.success('已提交审批'); load() }
const audit = async(id:number, approved:boolean)=>{ await salesOrderApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }
const unAudit = async(id:number)=>{ await salesOrderApi.unAudit(id); ElMessage.success('反审核完成'); load() }
const closeOrder = async(id:number)=>{ await salesOrderApi.close(id); ElMessage.success('已关闭'); load() }
const voidOrder = async(id:number)=>{ await salesOrderApi.void(id); ElMessage.success('已作废'); load() }

const pushNotice = async(id:number)=>{
  const detail = await salesOrderApi.detail(id)
  const lines = (detail.data.lines || []).map((x:any)=>({ sourceLineId: x.id, qty: Number(x.orderQty) - Number(x.deliveredQty || 0) })).filter((x:any)=>x.qty > 0)
  if (!lines.length) {
    ElMessage.warning('无可下推数量')
    return
  }
  await salesOrderApi.pushDeliveryNotice({ orderId: id, lines })
  ElMessage.success('下推发货通知成功')
  load()
}

onMounted(load)
</script>
