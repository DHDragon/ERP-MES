<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>调拨单详情</template>

    <el-descriptions :column="3" border>
      <el-descriptions-item label="调拨单号">{{ header?.transferNo }}</el-descriptions-item>
      <el-descriptions-item label="调出仓库">{{ header?.fromWarehouseId }}</el-descriptions-item>
      <el-descriptions-item label="调入仓库">{{ header?.toWarehouseId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="执行数量">{{ header?.executedQty }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="fromLocationId" label="调出库位" width="120" />
      <el-table-column prop="toLocationId" label="调入库位" width="120" />
      <el-table-column prop="qty" label="调拨数量" width="140" />
      <el-table-column prop="executedQty" label="已执行数量" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { transferApi } from '../../api/modules/inventory'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async()=>{
  loading.value = true
  try{
    const res = await transferApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value=false }
}

onMounted(load)
</script>
