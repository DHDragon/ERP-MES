<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>{{ isEdit ? '编辑发货通知' : '新增发货通知' }}</template>

    <el-form :model="header" label-width="100px">
      <el-form-item label="订单ID"><el-input-number v-model="header.orderId" :min="1" style="width: 100%" /></el-form-item>
      <el-form-item label="业务日期"><el-date-picker v-model="header.bizDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="header.remark" /></el-form-item>
    </el-form>

    <el-divider>通知行</el-divider>
    <el-button @click="addLine" type="primary" plain>新增行</el-button>
    <el-table :data="lines" border style="margin-top:8px">
      <el-table-column label="行号" width="70"><template #default="{ $index }">{{ $index + 1 }}</template></el-table-column>
      <el-table-column label="订单行ID" width="140"><template #default="{ row }"><el-input-number v-model="row.orderLineId" :min="1" /></template></el-table-column>
      <el-table-column label="物料ID" width="120"><template #default="{ row }"><el-input-number v-model="row.materialId" :min="1" /></template></el-table-column>
      <el-table-column label="通知数量" width="140"><template #default="{ row }"><el-input-number v-model="row.noticeQty" :min="0.001" :precision="3" /></template></el-table-column>
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
import { deliveryNoticeApi } from '../../api/modules/sales'

const route = useRoute()
const router = useRouter()
const id = computed(() => Number(route.params.id || 0))
const isEdit = computed(() => id.value > 0)
const loading = ref(false)
const saving = ref(false)

const header = reactive<any>({ orderId: 1, bizDate: '', remark: '' })
const lines = ref<any[]>([{ orderLineId: 1, materialId: 1, noticeQty: 1 }])

const load = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await deliveryNoticeApi.detail(id.value)
    Object.assign(header, res.data.header)
    lines.value = (res.data.lines || []).map((x:any)=>({ orderLineId: x.orderLineId, materialId: x.materialId, noticeQty: Number(x.noticeQty) }))
  } finally { loading.value = false }
}

const addLine = ()=>lines.value.push({ orderLineId:1, materialId:1, noticeQty:1 })
const removeLine = (i:number)=>{ lines.value.splice(i,1); if(!lines.value.length) addLine() }

const save = async()=>{
  if (!header.orderId) { ElMessage.warning('订单ID必填'); return }
  saving.value = true
  try {
    await deliveryNoticeApi.save({ header: { ...header, id: isEdit.value ? id.value : undefined }, lines: lines.value })
    ElMessage.success('保存成功')
    router.push('/sales/delivery-notices')
  } finally { saving.value = false }
}

onMounted(load)
</script>
