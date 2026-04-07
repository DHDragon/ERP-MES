<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="调拨单号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增调拨</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="rows.length" :page-no="1" :page-size="rows.length || 10">
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/inventory/transfers/detail/${row.id}`)">详情</el-button>
          <el-button link type="warning" @click="audit(row.id)">审核</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" title="新增调拨单" width="860px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="调出仓库ID"><el-input-number v-model="form.fromWarehouseId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="调入仓库ID"><el-input-number v-model="form.toWarehouseId" :min="1" style="width:100%" /></el-form-item>
      </el-form>

      <el-divider>调拨行</el-divider>
      <el-button @click="addLine" type="primary" plain>新增行</el-button>
      <el-table :data="form.lines" border style="margin-top:8px">
        <el-table-column label="物料ID" width="120"><template #default="{ row }"><el-input-number v-model="row.materialId" :min="1" /></template></el-table-column>
        <el-table-column label="调出库位ID" width="140"><template #default="{ row }"><el-input-number v-model="row.fromLocationId" :min="1" /></template></el-table-column>
        <el-table-column label="调入库位ID" width="140"><template #default="{ row }"><el-input-number v-model="row.toLocationId" :min="1" /></template></el-table-column>
        <el-table-column label="数量" width="140"><template #default="{ row }"><el-input-number v-model="row.qty" :min="0.001" :precision="3" /></template></el-table-column>
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
import { transferApi } from '../../api/modules/inventory'
import type { TransferOrderH } from '../../types/inventory'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<TransferOrderH[]>([])
const visible = ref(false)
const saving = ref(false)

const form = reactive<any>({ fromWarehouseId: 1, toWarehouseId: 2, lines: [{ materialId: 1, fromLocationId: undefined, toLocationId: undefined, qty: 1 }] })

const columns = computed(() => [
  { prop: 'transferNo', label: '调拨单号' },
  { prop: 'fromWarehouseId', label: '调出仓' },
  { prop: 'toWarehouseId', label: '调入仓' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '调拨数量' },
  { prop: 'executedQty', label: '已执行数量' },
  { prop: 'status', label: '状态' },
])

const load = async()=>{
  loading.value = true
  try{
    const res = await transferApi.list({ keyword: query.keyword || undefined })
    rows.value = res.data || []
  } finally { loading.value=false }
}

const reset = ()=>{ query.keyword=''; load() }
const openCreate = ()=>{ form.fromWarehouseId=1; form.toWarehouseId=2; form.lines=[{ materialId:1, fromLocationId: undefined, toLocationId: undefined, qty:1 }]; visible.value=true }
const addLine = ()=>form.lines.push({ materialId:1, fromLocationId: undefined, toLocationId: undefined, qty:1 })
const removeLine = (i:number)=>{ form.lines.splice(i,1); if(!form.lines.length) addLine() }

const save = async()=>{
  saving.value = true
  try{
    await transferApi.save({ header: { fromWarehouseId: form.fromWarehouseId, toWarehouseId: form.toWarehouseId }, lines: form.lines })
    ElMessage.success('保存成功')
    visible.value = false
    load()
  } finally { saving.value=false }
}

const audit = async(id:number)=>{ await transferApi.audit(id, { approved: true, remark: '通过' }); ElMessage.success('审核完成并已执行库存调拨'); load() }

onMounted(load)
</script>
