<template>
  <el-drawer :model-value="visible" :title="title" size="50%" @close="$emit('update:visible', false)">
    <slot />
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="$emit('submit')">保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
defineProps<{ visible: boolean; title: string }>()
defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'submit'): void }>()
</script>
