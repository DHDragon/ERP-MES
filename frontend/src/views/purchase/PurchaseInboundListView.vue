<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="入库单号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增入库</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/purchase/inbounds/detail/${row.id}`)">详情</el-button>
          <el-button link type="warning" @click="audit(row.id)">审核</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" title="新增采购入库" width="760px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="收货ID"><el-input-number v-model="form.receiptId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="启用质检"><el-switch v-model="form.qcRequired" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item v-if="form.qcRequired===1" label="检验已通过"><el-switch v-model="form.qcPassed" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <el-divider>入库行</el-divider>
      <el-button @click="addLine" type="primary" plain>新增行</el-button>
      <el-table :data="form.lines" border style="margin-top:8px">
        <el-table-column label="收货行ID" width="140"><template #default="{ row }"><el-input-number v-model="row.receiptLineId" :min="1" /></template></el-table-column>
        <el-table-column label="数量" width="140"><template #default="{ row }"><el-input-number v-model="row.inboundQty" :min="0.001" :precision="3" /></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{ $index }"><el-button link type="danger" @click="removeLine($index)">删除</el-button></template></el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
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
import { purchaseInboundApi } from '../../api/modules/purchase'
import type { PurchaseInboundH } from '../../types/purchase'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<PurchaseInboundH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const visible = ref(false)
const saving = ref(false)
const form = reactive<any>({ receiptId: 1, qcRequired: 0, qcPassed: 0, lines: [{ receiptLineId: 1, inboundQty: 1 }] })

const columns = computed(() => [
  { prop: 'inboundNo', label: '入库单号' },
  { prop: 'receiptId', label: '收货ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '入库数量' },
  { prop: 'qcRequired', label: '质检' },
  { prop: 'inspectionHookStatus', label: '质检挂点' },
  { prop: 'status', label: '状态' },
])

const load = async()=>{
  loading.value = true
  try{
    const res = await purchaseInboundApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value=false }
}

const reset = ()=>{ query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number,s:number)=>{ pageNo.value=p; pageSize.value=s; load() }

const openCreate = ()=>{ form.receiptId=1; form.qcRequired=0; form.qcPassed=0; form.lines=[{ receiptLineId:1, inboundQty:1 }]; visible.value=true }
const addLine = ()=>form.lines.push({ receiptLineId:1, inboundQty:1 })
const removeLine = (i:number)=>{ form.lines.splice(i,1); if(!form.lines.length) addLine() }

const save = async()=>{
  saving.value = true
  try{
    await purchaseInboundApi.save({ header: { receiptId: form.receiptId, qcRequired: form.qcRequired, qcPassed: form.qcPassed }, lines: form.lines })
    ElMessage.success('保存成功')
    visible.value=false
    load()
  } finally { saving.value=false }
}

const audit = async(id:number)=>{ await purchaseInboundApi.audit(id, { approved: true, remark: '通过' }); ElMessage.success('审核完成并已回写上游'); load() }

onMounted(load)
</script>
