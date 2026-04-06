<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="收货单号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openConfirm">确认收货</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/purchase/receipts/detail/${row.id}`)">详情</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" title="确认收货" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单ID"><el-input-number v-model="form.orderId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="启用质检"><el-switch v-model="form.qcEnabled" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <el-divider>收货行</el-divider>
      <el-button @click="addLine" type="primary" plain>新增行</el-button>
      <el-table :data="form.lines" border style="margin-top:8px">
        <el-table-column label="订单行ID" width="140"><template #default="{ row }"><el-input-number v-model="row.orderLineId" :min="1" /></template></el-table-column>
        <el-table-column label="数量" width="140"><template #default="{ row }"><el-input-number v-model="row.receiptQty" :min="0.001" :precision="3" /></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{ $index }"><el-button link type="danger" @click="removeLine($index)">删除</el-button></template></el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="confirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { purchaseReceiptApi } from '../../api/modules/purchase'
import type { PurchaseReceiptH } from '../../types/purchase'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<PurchaseReceiptH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const visible = ref(false)
const saving = ref(false)
const form = reactive<any>({ orderId: 1, qcEnabled: 0, lines: [{ orderLineId: 1, receiptQty: 1 }] })

const columns = computed(() => [
  { prop: 'receiptNo', label: '收货单号' },
  { prop: 'orderId', label: '订单ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '收货数量' },
  { prop: 'inboundQty', label: '已入库' },
  { prop: 'qcEnabled', label: '质检' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await purchaseReceiptApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const reset = ()=>{ query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number,s:number)=>{ pageNo.value=p; pageSize.value=s; load() }

const openConfirm = ()=>{ form.orderId=1; form.qcEnabled=0; form.lines=[{ orderLineId:1, receiptQty:1 }]; visible.value=true }
const addLine = ()=>form.lines.push({ orderLineId:1, receiptQty:1 })
const removeLine = (i:number)=>{ form.lines.splice(i,1); if(!form.lines.length) addLine() }

const confirm = async()=>{
  saving.value = true
  try{
    await purchaseReceiptApi.confirm({ header: { orderId: form.orderId, qcEnabled: form.qcEnabled }, lines: form.lines })
    ElMessage.success('确认收货成功')
    visible.value=false
    load()
  } finally { saving.value=false }
}

onMounted(load)
</script>
