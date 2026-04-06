<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>采购申请详情</template>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="申请单号">{{ header?.reqNo }}</el-descriptions-item>
      <el-descriptions-item label="来源">{{ header?.sourceType }}</el-descriptions-item>
      <el-descriptions-item label="供应商ID">{{ header?.supplierId }}</el-descriptions-item>
      <el-descriptions-item label="业务日期">{{ header?.bizDate }}</el-descriptions-item>
      <el-descriptions-item label="审批状态">{{ header?.approvalStatus }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ header?.status }}</el-descriptions-item>
      <el-descriptions-item label="申请数量">{{ header?.totalQty }}</el-descriptions-item>
      <el-descriptions-item label="已下单">{{ header?.orderedQty }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ header?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />
    <el-table :data="lines" border>
      <el-table-column prop="lineNo" label="行号" width="80" />
      <el-table-column prop="materialId" label="物料ID" width="120" />
      <el-table-column prop="reqQty" label="申请数量" width="140" />
      <el-table-column prop="orderedQty" label="已下单" width="140" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="remark" label="备注" min-width="160" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { purchaseReqApi } from '../../api/modules/purchase'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const header = ref<any>()
const lines = ref<any[]>([])

const load = async()=>{
  loading.value = true
  try{
    const res = await purchaseReqApi.detail(id.value)
    header.value = res.data.header
    lines.value = res.data.lines || []
  } finally { loading.value=false }
}

onMounted(load)
</script>
