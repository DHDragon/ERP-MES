<template>
  <el-card shadow="never">
    <template #header>{{ isEdit ? '编辑供应商' : '新增供应商' }}</template>
    <el-form :model="form" label-width="100px">
      <el-form-item label="供应商编码"><el-input v-model="form.supplierCode" /></el-form-item>
      <el-form-item label="供应商名称"><el-input v-model="form.supplierName" /></el-form-item>
      <el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item>
      <el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item>
      <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
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
import { supplierApi } from '../../api/modules/masterdata'
import type { MdSupplier } from '../../types/masterdata'

const route = useRoute()
const router = useRouter()
const id = computed(() => Number(route.params.id || 0))
const isEdit = computed(() => id.value > 0)
const saving = ref(false)
const form = reactive<MdSupplier>({ supplierCode: '', supplierName: '' })

const load = async () => {
  if (!isEdit.value) return
  const res = await supplierApi.detail(id.value)
  Object.assign(form, res.data)
}

const save = async () => {
  saving.value = true
  try {
    await supplierApi.save({ ...form, id: isEdit.value ? id.value : undefined })
    ElMessage.success('保存成功')
    router.push('/masterdata/suppliers')
  } finally { saving.value = false }
}

onMounted(load)
</script>
