<template>
  <el-tag :type="tagType">{{ status }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ status: string }>()

const tagType = computed(() => {
  if (['ENABLED', 'APPROVED'].includes(props.status)) return 'success'
  if (['DISABLED', 'REJECTED'].includes(props.status)) return 'danger'
  if (['DRAFT'].includes(props.status)) return 'info'
  return 'warning'
})
</script>
