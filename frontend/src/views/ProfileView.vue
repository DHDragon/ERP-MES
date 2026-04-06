<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>个人信息</template>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="用户ID">{{ profile?.id }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ profile?.username }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ profile?.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="组织ID">{{ profile?.orgId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="部门ID">{{ profile?.deptId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag v-for="r in profile?.roles || []" :key="r" style="margin-right:6px">{{ r }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="权限点">
        <el-tag v-for="p in profile?.permissions || []" :key="p" size="small" style="margin-right:6px; margin-bottom: 4px">{{ p }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { meApi } from '../api/modules/system'
import type { UserProfile } from '../types/base'

const loading = ref(false)
const profile = ref<UserProfile | null>(null)

const load = async () => {
  loading.value = true
  try {
    const res = await meApi()
    profile.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
