<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="仓库编码/名称" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增仓库</el-button></el-form-item>
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

    <el-dialog v-model="visible" :title="form.id ? '编辑仓库' : '新增仓库'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="仓库编码"><el-input v-model="form.warehouseCode" /></el-form-item>
        <el-form-item label="仓库名称"><el-input v-model="form.warehouseName" /></el-form-item>
        <el-form-item label="仓库类型"><el-input v-model="form.warehouseType" /></el-form-item>
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
import { warehouseApi } from '../../api/modules/masterdata'
import type { MdWarehouse } from '../../types/masterdata'

const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<MdWarehouse[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const visible = ref(false)
const form = reactive<MdWarehouse>({ warehouseCode: '', warehouseName: '' })

const columns = computed(() => [
  { prop: 'warehouseCode', label: '仓库编码' },
  { prop: 'warehouseName', label: '仓库名称' },
  { prop: 'warehouseType', label: '仓库类型' },
  { prop: 'status', label: '状态' },
  { prop: 'approvalStatus', label: '审批状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await warehouseApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const onSearch = ()=>{ pageNo.value=1; load() }
const onReset = ()=>{ query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number,s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const openCreate = ()=>{ Object.assign(form, { id: undefined, warehouseCode:'', warehouseName:'', warehouseType:'' }); visible.value=true }
const openEdit = (row:MdWarehouse)=>{ Object.assign(form,row); visible.value=true }
const save = async()=>{ await warehouseApi.save(form); ElMessage.success('保存成功'); visible.value=false; load() }
const submit = async(id:number)=>{ await warehouseApi.submit(id); ElMessage.success('已提审'); load() }
const audit = async(id:number, approved:boolean)=>{ await warehouseApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }
const toggle = async(row:MdWarehouse)=>{ if(!row.id) return; row.status === 'ENABLED' ? await warehouseApi.disable(row.id) : await warehouseApi.enable(row.id); ElMessage.success('状态更新成功'); load() }

const mockImport = async()=>{ await warehouseApi.import([{ warehouseCode:'WH-IMP-001', warehouseName:'导入仓库' }]); ElMessage.success('导入成功'); load() }
const doExport = async()=>{ const res = await warehouseApi.export(); ElMessage.info(res.data.message) }

onMounted(load)
</script>
