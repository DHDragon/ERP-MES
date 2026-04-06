<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>采购入库详情</template>

    <el-descriptions :column="3" border>
      <el-descriptions-item label="入库单号">{{ header?.inboundNo }}</el-descriptions-item>
      <el-descriptions-item label="收货ID">{{ header?.receiptId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="入库数量">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="质检">{{ header?.qcRequired }}</el-descriptions-item>
      <el-descriptions-item label="质检挂点">{{ header?.inspectionHookStatus }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="receiptLineId" label="收货行ID" width="120" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="inboundQty" label="入库数量" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { purchaseInboundApi } from '../../api/modules/purchase'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async()=>{
  loading.value = true
  try{
    const res = await purchaseInboundApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value=false }
}

onMounted(load)
</script>
