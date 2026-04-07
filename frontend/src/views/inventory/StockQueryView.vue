<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="仓库ID"><el-input-number v-model="query.warehouseId" :min="1" style="width:140px" /></el-form-item>
      <el-form-item label="物料ID"><el-input-number v-model="query.materialId" :min="1" style="width:140px" /></el-form-item>
      <el-form-item label="批次"><el-input v-model="query.batchNo" clearable style="width:160px" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width:140px">
          <el-option label="可用" value="AVAILABLE" />
          <el-option label="冻结" value="LOCKED" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="rows.length" :page-no="1" :page-size="rows.length || 10">
      <el-table-column label="可用数量" width="140">
        <template #default="{ row }">{{ Number(row.qty || 0) - Number(row.lockedQty || 0) }}</template>
      </el-table-column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { inventoryStockApi } from '../../api/modules/inventory'
import type { InventoryStock } from '../../types/inventory'

const query = reactive<any>({ warehouseId: undefined, materialId: undefined, batchNo: '', status: 'AVAILABLE' })
const rows = ref<InventoryStock[]>([])
const loading = ref(false)

const columns = computed(() => [
  { prop: 'warehouseId', label: '仓库ID' },
  { prop: 'locationId', label: '库位ID' },
  { prop: 'materialId', label: '物料ID' },
  { prop: 'batchNo', label: '批次号' },
  { prop: 'serialNo', label: '序列号' },
  { prop: 'status', label: '库存状态' },
  { prop: 'qty', label: '库存数量' },
  { prop: 'lockedQty', label: '锁定数量' },
])

const load = async () => {
  loading.value = true
  try {
    const payload: any = {
      warehouseId: query.warehouseId,
      materialId: query.materialId,
      batchNo: query.batchNo || undefined,
      status: query.status || undefined,
    }
    const res = await inventoryStockApi.query(payload)
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.warehouseId = undefined
  query.materialId = undefined
  query.batchNo = ''
  query.status = 'AVAILABLE'
  rows.value = []
}
</script>
