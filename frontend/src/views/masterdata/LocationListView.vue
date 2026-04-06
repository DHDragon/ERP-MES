<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="库位编码/名称" clearable /></el-form-item>
      <el-form-item label="仓库ID"><el-input-number v-model="query.warehouseId" :min="1" style="width:160px" /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增库位</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="warning" @click="submit(row.id)">提审</el-button>
          <el-button link type="success" @click="audit(row.id, true)">通过</el-button>
          <el-button link type="danger" @click="audit(row.id, false)">驳回</el-button>
          <el-button link @click="toggle(row)">{{ row.status === 'ENABLED' ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-card shadow="never" style="margin-top:12px">
      <el-button @click="mockImport">导入(示例)</el-button>
      <el-button @click="doExport">导出(骨架)</el-button>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑库位' : '新增库位'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="仓库ID"><el-input-number v-model="form.warehouseId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="库位编码"><el-input v-model="form.locationCode" /></el-form-item>
        <el-form-item label="库位名称"><el-input v-model="form.locationName" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { locationApi } from '../../api/modules/masterdata'
import type { MdLocation } from '../../types/masterdata'

const query = reactive<{ keyword: string; warehouseId?: number }>({ keyword: '', warehouseId: undefined })
const loading = ref(false)
const rows = ref<MdLocation[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const visible = ref(false)
const form = reactive<MdLocation>({ warehouseId: 1, locationCode: '', locationName: '' })

const columns = computed(() => [
  { prop: 'warehouseId', label: '仓库ID' },
  { prop: 'locationCode', label: '库位编码' },
  { prop: 'locationName', label: '库位名称' },
  { prop: 'status', label: '状态' },
  { prop: 'approvalStatus', label: '审批状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await locationApi.list({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: query.keyword || undefined,
      warehouseId: query.warehouseId,
    })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const onSearch = ()=>{ pageNo.value=1; load() }
const onReset = ()=>{ query.keyword=''; query.warehouseId=undefined; pageNo.value=1; load() }
const onPage = (p:number,s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const openCreate = ()=>{ Object.assign(form, { id: undefined, warehouseId: 1, locationCode:'', locationName:'' }); visible.value=true }
const openEdit = (row:MdLocation)=>{ Object.assign(form,row); visible.value=true }
const save = async()=>{ await locationApi.save(form); ElMessage.success('保存成功'); visible.value=false; load() }
const submit = async(id:number)=>{ await locationApi.submit(id); ElMessage.success('已提审'); load() }
const audit = async(id:number, approved:boolean)=>{ await locationApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }
const toggle = async(row:MdLocation)=>{ if(!row.id) return; row.status === 'ENABLED' ? await locationApi.disable(row.id) : await locationApi.enable(row.id); ElMessage.success('状态更新成功'); load() }

const mockImport = async()=>{ await locationApi.import([{ warehouseId:1, locationCode:'LOC-IMP-001', locationName:'导入库位' }]); ElMessage.success('导入成功'); load() }
const doExport = async()=>{ const res = await locationApi.export(); ElMessage.info(res.data.message) }

onMounted(load)
</script>
