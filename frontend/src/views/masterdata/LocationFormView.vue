<template>
  <el-card shadow="never">
    <template #header>{{ isEdit ? '编辑库位' : '新增库位' }}</template>
    <el-form :model="form" label-width="100px">
      <el-form-item label="仓库ID"><el-input-number v-model="form.warehouseId" :min="1" style="width:100%" /></el-form-item>
      <el-form-item label="库位编码"><el-input v-model="form.locationCode" /></el-form-item>
      <el-form-item label="库位名称"><el-input v-model="form.locationName" /></el-form-item>
      <el-form-item>
        <el-button @click="router.back()">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { locationApi } from '../../api/modules/masterdata'
import type { MdLocation } from '../../types/masterdata'

const route = useRoute()
const router = useRouter()
const id = computed(() => Number(route.params.id || 0))
const isEdit = computed(() => id.value > 0)
const saving = ref(false)
const form = reactive<MdLocation>({ warehouseId: 1, locationCode: '', locationName: '' })

const load = async () => {
  if (!isEdit.value) return
  const res = await locationApi.detail(id.value)
  Object.assign(form, res.data)
}

const save = async () => {
  saving.value = true
  try {
    await locationApi.save({ ...form, id: isEdit.value ? id.value : undefined })
    ElMessage.success('保存成功')
    router.push('/masterdata/locations')
  } finally { saving.value = false }
}

onMounted(load)
</script>
