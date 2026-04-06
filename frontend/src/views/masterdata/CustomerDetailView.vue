<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>客户详情</template>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="客户编码">{{ detail?.customerCode }}</el-descriptions-item>
      <el-descriptions-item label="客户名称">{{ detail?.customerName }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detail?.contactName }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ detail?.contactPhone }}</el-descriptions-item>
      <el-descriptions-item label="地址" :span="2">{{ detail?.address }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detail?.status }}</el-descriptions-item>
      <el-descriptions-item label="审批状态">{{ detail?.approvalStatus }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detail?.auditRemark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { customerApi } from '../../api/modules/masterdata'
import type { MdCustomer } from '../../types/masterdata'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const detail = ref<MdCustomer>()

const load = async () => {
  loading.value = true
  try {
    const res = await customerApi.detail(id.value)
    detail.value = res.data
  } finally { loading.value = false }
}

onMounted(load)
</script>
