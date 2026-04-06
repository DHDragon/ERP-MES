<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>权限点清单（当前登录用户）</template>
    <el-empty v-if="!permissions.length" description="暂无权限点" />
    <div v-else>
      <el-tag v-for="item in permissions" :key="item" style="margin-right:8px; margin-bottom:8px">{{ item }}</el-tag>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../api/request'

const loading = ref(false)
const permissions = ref<string[]>([])

const load = async () => {
  loading.value = true
  try {
    const res = await http.get<string[]>('/auth/permissions')
    permissions.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
