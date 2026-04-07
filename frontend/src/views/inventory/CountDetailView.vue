<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>盘点单详情</template>

    <el-descriptions :column="3" border>
      <el-descriptions-item label="盘点单号">{{ header?.countNo }}</el-descriptions-item>
      <el-descriptions-item label="仓库ID">{{ header?.warehouseId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="实盘总数">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="差异总数">{{ header?.diffQty }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="locationId" label="库位ID" width="120" />
      <el-table-column prop="qtyBook" label="账面数量" width="120" />
      <el-table-column prop="qtyActual" label="实盘数量" width="120" />
      <el-table-column prop="diffQty" label="差异数量" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { countApi } from '../../api/modules/inventory'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async()=>{
  loading.value = true
  try{
    const res = await countApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value=false }
}

onMounted(load)
</script>
