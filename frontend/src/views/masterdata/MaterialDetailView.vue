<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>物料详情</template>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="物料编码">{{ detail?.materialCode }}</el-descriptions-item>
      <el-descriptions-item label="物料名称">{{ detail?.materialName }}</el-descriptions-item>
      <el-descriptions-item label="分类ID">{{ detail?.categoryId }}</el-descriptions-item>
      <el-descriptions-item label="规格">{{ detail?.spec }}</el-descriptions-item>
      <el-descriptions-item label="型号">{{ detail?.model }}</el-descriptions-item>
      <el-descriptions-item label="单位">{{ detail?.unit }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detail?.status }}</el-descriptions-item>
      <el-descriptions-item label="审批状态">{{ detail?.approvalStatus }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detail?.auditRemark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { materialApi } from '../../api/modules/masterdata'
import type { MdMaterial } from '../../types/masterdata'

const route = useRoute()
const id = computed(() => Number(route.params.id || 0))
const loading = ref(false)
const detail = ref<MdMaterial>()

const load = async () => {
  loading.value = true
  try {
    const res = await materialApi.detail(id.value)
    detail.value = res.data
  } finally { loading.value = false }
}

onMounted(load)
</script>
