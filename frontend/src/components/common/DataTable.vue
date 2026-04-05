<template>
  <el-card shadow="never">
    <el-table :data="data" v-loading="loading" border>
      <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" />
      <slot />
    </el-table>
    <div style="display:flex;justify-content:flex-end;margin-top:12px">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :current-page="pageNo"
        :page-size="pageSize"
        @current-change="(p:number)=>$emit('page-change', p, pageSize)"
        @size-change="(s:number)=>$emit('page-change', pageNo, s)"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  columns: Array<{ prop: string; label: string }>
  data: unknown[]
  loading?: boolean
  total: number
  pageNo: number
  pageSize: number
}>()

defineEmits<{ (e: 'page-change', pageNo: number, pageSize: number): void }>()
</script>
