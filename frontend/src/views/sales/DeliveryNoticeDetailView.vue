<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>发货通知详情</template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="通知号">{{ header?.noticeNo }}</el-descriptions-item>
      <el-descriptions-item label="订单ID">{{ header?.orderId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="通知数量">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="已出数量">{{ header?.outboundQty }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="orderLineId" label="订单行ID" width="120" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="noticeQty" label="通知数量" width="140" />
      <el-table-column prop="outboundQty" label="已出数量" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { deliveryNoticeApi } from '../../api/modules/sales'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async () => {
  loading.value = true
  try {
    const res = await deliveryNoticeApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value = false }
}

onMounted(load)
</script>
