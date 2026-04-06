<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>销售订单详情</template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="订单号">{{ header?.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="客户ID">{{ header?.customerId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="审批状态">{{ header?.approvalStatus }}</el-descriptions-item>
      <el-descriptions-item label="总数量">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="已发数量">{{ header?.deliveredQty }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ header?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="orderQty" label="订单数量" width="140" />
      <el-table-column prop="deliveredQty" label="已发数量" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="remark" label="备注" min-width="160" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { salesOrderApi } from '../../api/modules/sales'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async () => {
  loading.value = true
  try {
    const res = await salesOrderApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value = false }
}

onMounted(load)
</script>
