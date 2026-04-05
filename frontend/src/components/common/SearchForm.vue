<template>
  <el-card shadow="never" style="margin-bottom: 12px">
    <el-form :model="model" inline>
      <slot />
      <el-form-item>
        <el-button type="primary" @click="$emit('search')">查询</el-button>
        <el-button @click="$emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts" generic="T extends Record<string, any>">
defineProps<{ model: T }>()
defineEmits<{ (e: 'search'): void; (e: 'reset'): void }>()
</script>
