<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="onReset">
      <el-form-item label="字典类型">
        <el-select v-model="query.dictType" style="width: 220px" clearable placeholder="请选择字典类型">
          <el-option v-for="item in dictTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-card shadow="never">
      <el-table :data="rows" border v-loading="loading">
        <el-table-column prop="code" label="编码" width="220" />
        <el-table-column prop="name" label="名称" min-width="220" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import SearchForm from '../components/common/SearchForm.vue'
import { dictByTypeApi } from '../api/modules/base'
import type { DictItem } from '../types/base'

const dictTypeOptions = ['STATUS', 'APPROVAL_STATUS', 'UNIT', 'ORG_TYPE', 'WAREHOUSE_TYPE', 'INSPECTION_METHOD', 'ALERT_TYPE']

const query = reactive({ dictType: 'STATUS' })
const loading = ref(false)
const rows = ref<DictItem[]>([])

const load = async () => {
  if (!query.dictType) {
    rows.value = []
    return
  }
  loading.value = true
  try {
    const res = await dictByTypeApi(query.dictType)
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const onReset = () => {
  query.dictType = 'STATUS'
  load()
}

onMounted(load)
</script>
