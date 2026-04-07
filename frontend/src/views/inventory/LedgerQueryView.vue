<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="业务类型"><el-input v-model="query.bizType" clearable style="width:140px" /></el-form-item>
      <el-form-item label="业务ID"><el-input-number v-model="query.bizId" :min="1" style="width:140px" /></el-form-item>
      <el-form-item label="物料ID"><el-input-number v-model="query.materialId" :min="1" style="width:140px" /></el-form-item>
      <el-form-item label="仓库ID"><el-input-number v-model="query.warehouseId" :min="1" style="width:140px" /></el-form-item>
      <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="rows.length" :page-no="1" :page-size="rows.length || 10" />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { inventoryLedgerApi } from '../../api/modules/inventory'
import type { InventoryLedger } from '../../types/inventory'

const query = reactive<any>({ bizType: '', bizId: undefined, materialId: undefined, warehouseId: undefined })
const rows = ref<InventoryLedger[]>([])
const loading = ref(false)

const columns = computed(() => [
  { prop: 'bizType', label: '业务类型' },
  { prop: 'bizId', label: '业务ID' },
  { prop: 'bizLineId', label: '业务行ID' },
  { prop: 'warehouseId', label: '仓库ID' },
  { prop: 'locationId', label: '库位ID' },
  { prop: 'materialId', label: '物料ID' },
  { prop: 'qtyBefore', label: '变更前' },
  { prop: 'qtyChange', label: '变更量' },
  { prop: 'qtyAfter', label: '变更后' },
  { prop: 'direction', label: '方向' },
  { prop: 'remark', label: '备注' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await inventoryLedgerApi.list({
      bizType: query.bizType || undefined,
      bizId: query.bizId,
      materialId: query.materialId,
      warehouseId: query.warehouseId,
    })
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.bizType = ''
  query.bizId = undefined
  query.materialId = undefined
  query.warehouseId = undefined
  rows.value = []
}
</script>
