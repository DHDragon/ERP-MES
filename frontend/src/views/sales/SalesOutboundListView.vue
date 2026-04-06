<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="出库号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增出库</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/sales/outbounds/detail/${row.id}`)">详情</el-button>
          <el-button link type="warning" @click="audit(row.id)">审核</el-button>
          <el-button link @click="pda(row.id)">查看PDA记录</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" title="新增销售出库" width="620px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="通知ID"><el-input-number v-model="form.noticeId" :min="1" style="width:100%" /></el-form-item>
      </el-form>

      <el-divider>出库行</el-divider>
      <el-button @click="addLine" type="primary" plain>新增行</el-button>
      <el-table :data="form.lines" border style="margin-top:8px">
        <el-table-column label="通知行ID" width="140"><template #default="{ row }"><el-input-number v-model="row.noticeLineId" :min="1" /></template></el-table-column>
        <el-table-column label="数量" width="140"><template #default="{ row }"><el-input-number v-model="row.outboundQty" :min="0.001" :precision="3" /></template></el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { salesOutboundApi } from '../../api/modules/sales'
import type { SalesOutboundH } from '../../types/sales'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<SalesOutboundH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const visible = ref(false)
const saving = ref(false)

const form = reactive<any>({ noticeId: 1, lines: [{ noticeLineId: 1, outboundQty: 1 }] })

const columns = computed(() => [
  { prop: 'outboundNo', label: '出库号' },
  { prop: 'noticeId', label: '通知ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '数量' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await salesOutboundApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const reset = ()=>{ query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number,s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const openCreate = ()=>{ form.noticeId = 1; form.lines = [{ noticeLineId:1, outboundQty:1 }]; visible.value=true }
const addLine = ()=>form.lines.push({ noticeLineId:1, outboundQty:1 })
const removeLine = (i:number)=>{ form.lines.splice(i,1); if(!form.lines.length) addLine() }

const save = async()=>{
  saving.value = true
  try {
    await salesOutboundApi.save({ header: { noticeId: form.noticeId }, lines: form.lines })
    ElMessage.success('保存成功')
    visible.value = false
    load()
  } finally { saving.value = false }
}

const audit = async(id:number)=>{ await salesOutboundApi.audit(id); ElMessage.success('审核完成并已回写上游'); load() }
const pda = async(id:number)=>{
  const res = await salesOutboundApi.pdaRecords(id)
  await ElMessageBox.alert(res.data || '{}', 'PDA记录')
}

onMounted(load)
</script>
