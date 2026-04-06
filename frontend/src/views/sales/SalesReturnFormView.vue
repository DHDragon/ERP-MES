<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>{{ isEdit ? '编辑销售退货' : '新增销售退货' }}</template>

    <el-form :model="header" label-width="100px">
      <el-form-item label="客户ID"><el-input-number v-model="header.customerId" :min="1" style="width:100%" /></el-form-item>
      <el-form-item label="业务日期"><el-date-picker v-model="header.bizDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="header.remark" /></el-form-item>
    </el-form>

    <el-divider>退货行</el-divider>
    <el-button @click="addLine" type="primary" plain>新增行</el-button>
    <el-table :data="lines" border style="margin-top:8px">
      <el-table-column label="行号" width="70"><template #default="{ $index }">{{ $index + 1 }}</template></el-table-column>
      <el-table-column label="物料ID" width="140"><template #default="{ row }"><el-input-number v-model="row.materialId" :min="1" /></template></el-table-column>
      <el-table-column label="数量" width="140"><template #default="{ row }"><el-input-number v-model="row.returnQty" :min="0.001" :precision="3" /></template></el-table-column>
      <el-table-column label="操作" width="90"><template #default="{ $index }"><el-button link type="danger" @click="removeLine($index)">删除</el-button></template></el-table-column>
    </el-table>

    <div style="margin-top:16px">
      <el-button @click="router.back()">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { salesReturnApi } from '../../api/modules/sales'

const route = useRoute()
const router = useRouter()
const id = computed(() => Number(route.params.id || 0))
const isEdit = computed(() => id.value > 0)
const loading = ref(false)
const saving = ref(false)

const header = reactive<any>({ customerId: 1, bizDate: '', remark: '' })
const lines = ref<any[]>([{ materialId: 1, returnQty: 1 }])

const load = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await salesReturnApi.detail(id.value)
    Object.assign(header, res.data.header)
    lines.value = (res.data.lines || []).map((x:any)=>({ materialId: x.materialId, returnQty: Number(x.returnQty) }))
  } finally { loading.value = false }
}

const addLine = ()=>lines.value.push({ materialId:1, returnQty:1 })
const removeLine = (i:number)=>{ lines.value.splice(i,1); if(!lines.value.length) addLine() }

const save = async()=>{
  saving.value = true
  try {
    await salesReturnApi.save({ header: { ...header, id: isEdit.value ? id.value : undefined }, lines: lines.value })
    ElMessage.success('保存成功')
    router.push('/sales/returns')
  } finally { saving.value = false }
}

onMounted(load)
</script>
