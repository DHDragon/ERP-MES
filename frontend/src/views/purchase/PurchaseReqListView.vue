<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="申请单号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="router.push('/purchase/reqs/form')">新增申请</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="420">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/purchase/reqs/detail/${row.id}`)">详情</el-button>
          <el-button link type="success" @click="router.push(`/purchase/reqs/form/${row.id}`)">编辑</el-button>
          <el-button link type="warning" @click="submit(row.id)">提审</el-button>
          <el-button link type="success" @click="audit(row.id, true)">审核通过</el-button>
          <el-button link type="danger" @click="audit(row.id, false)">驳回</el-button>
          <el-button link type="primary" @click="pushOrder(row.id)">下推采购订单</el-button>
        </template>
      </el-table-column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { purchaseReqApi } from '../../api/modules/purchase'
import type { PurchaseReqH } from '../../types/purchase'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<PurchaseReqH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const columns = computed(() => [
  { prop: 'reqNo', label: '申请单号' },
  { prop: 'sourceType', label: '来源' },
  { prop: 'supplierId', label: '供应商ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '申请数量' },
  { prop: 'orderedQty', label: '已下单' },
  { prop: 'approvalStatus', label: '审批状态' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await purchaseReqApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const reset = () => { query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number, s:number)=>{ pageNo.value=p; pageSize.value=s; load() }

const submit = async(id:number)=>{ await purchaseReqApi.submit(id); ElMessage.success('已提交审批'); load() }
const audit = async(id:number, approved:boolean)=>{ await purchaseReqApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }

const pushOrder = async(id:number)=>{
  const detail = await purchaseReqApi.detail(id)
  const lines = (detail.data.lines || []).map((x:any)=>({ sourceLineId: x.id, qty: Number(x.reqQty) - Number(x.orderedQty || 0) })).filter((x:any)=>x.qty>0)
  if(!lines.length){ ElMessage.warning('无可下推数量'); return }
  await purchaseReqApi.pushOrder({ reqId:id, lines })
  ElMessage.success('下推采购订单成功')
  load()
}

onMounted(load)
</script>
