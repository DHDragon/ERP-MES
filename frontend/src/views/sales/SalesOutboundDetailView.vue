<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>销售出库详情</template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="出库号">{{ header?.outboundNo }}</el-descriptions-item>
      <el-descriptions-item label="通知ID">{{ header?.noticeId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="总数量">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="PDA记录">{{ header?.pdaRecordJson || '{}' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="noticeLineId" label="通知行ID" width="120" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="outboundQty" label="出库数量" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { salesOutboundApi } from '../../api/modules/sales'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async () => {
  loading.value = true
  try {
    const res = await salesOutboundApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value = false }
}

onMounted(load)
</script>
