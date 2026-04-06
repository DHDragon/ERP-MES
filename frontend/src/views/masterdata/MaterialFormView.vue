<template>
  <el-card shadow="never">
    <template #header>{{ isEdit ? '编辑物料' : '新增物料' }}</template>
    <el-form :model="form" label-width="100px">
      <el-form-item label="物料编码"><el-input v-model="form.materialCode" /></el-form-item>
      <el-form-item label="物料名称"><el-input v-model="form.materialName" /></el-form-item>
      <el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="1" style="width:100%" /></el-form-item>
      <el-form-item label="规格"><el-input v-model="form.spec" /></el-form-item>
      <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
      <el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item>
      <el-form-item>
        <el-button @click="back">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { materialApi } from '../../api/modules/masterdata'
import type { MdMaterial } from '../../types/masterdata'

const route = useRoute()
const router = useRouter()
const id = computed(() => Number(route.params.id || 0))
const isEdit = computed(() => id.value > 0)
const saving = ref(false)

const form = reactive<MdMaterial>({ materialCode: '', materialName: '', categoryId: undefined, unit: 'PCS' })

const load = async () => {
  if (!isEdit.value) return
  const res = await materialApi.detail(id.value)
  Object.assign(form, res.data)
}

const save = async () => {
  saving.value = true
  try {
    await materialApi.save({ ...form, id: isEdit.value ? id.value : undefined })
    ElMessage.success('保存成功')
    router.push('/masterdata/materials')
  } finally { saving.value = false }
}

const back = () => router.back()

onMounted(load)
</script>
