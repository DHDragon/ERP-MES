<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="参数关键字">
        <el-input v-model="query.keyword" placeholder="参数键/名称" clearable />
      </el-form-item>
    </SearchForm>

    <el-card shadow="never">
      <el-table :data="filteredRows" border v-loading="loading">
        <el-table-column prop="paramKey" label="参数键" min-width="220" />
        <el-table-column prop="paramName" label="参数名称" min-width="180" />
        <el-table-column prop="paramValue" label="参数值" min-width="220" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" title="编辑参数" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="参数键"><el-input v-model="form.paramKey" disabled /></el-form-item>
        <el-form-item label="参数名称"><el-input v-model="form.paramName" disabled /></el-form-item>
        <el-form-item label="参数值"><el-input v-model="form.paramValue" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm from '../components/common/SearchForm.vue'
import { listParamsApi, updateParamApi } from '../api/modules/base'
import type { CfgSystemParam } from '../types/base'

const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const rows = ref<CfgSystemParam[]>([])
const form = reactive<CfgSystemParam>({ paramKey: '', paramName: '', paramValue: '' })
const query = reactive({ keyword: '' })

const filteredRows = computed(() => {
  if (!query.keyword.trim()) return rows.value
  const kw = query.keyword.trim().toLowerCase()
  return rows.value.filter((x) => `${x.paramKey} ${x.paramName}`.toLowerCase().includes(kw))
})

const load = async () => {
  loading.value = true
  try {
    const res = await listParamsApi()
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.keyword = ''
}

const openEdit = (row: CfgSystemParam) => {
  Object.assign(form, row)
  visible.value = true
}

const onSave = async () => {
  if (!form.paramKey) return
  saving.value = true
  try {
    await updateParamApi(form.paramKey, form.paramValue || '')
    ElMessage.success('保存成功')
    visible.value = false
    load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
