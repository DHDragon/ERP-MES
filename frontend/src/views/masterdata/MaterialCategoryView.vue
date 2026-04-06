<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="分类编码/名称" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="openCreate">新增分类</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="warning" @click="submit(row.id)">提审</el-button>
          <el-button link type="success" @click="audit(row.id, true)">通过</el-button>
          <el-button link type="danger" @click="audit(row.id, false)">驳回</el-button>
          <el-button link @click="toggle(row)">{{ row.status === 'ENABLED' ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" :title="form.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="分类编码"><el-input v-model="form.categoryCode" /></el-form-item>
        <el-form-item label="分类名称"><el-input v-model="form.categoryName" /></el-form-item>
        <el-form-item label="父分类ID"><el-input-number v-model="form.parentId" :min="0" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
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
import { materialCategoryApi } from '../../api/modules/masterdata'
import type { MdMaterialCategory } from '../../types/masterdata'

const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<MdMaterialCategory[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const visible = ref(false)
const form = reactive<MdMaterialCategory>({ categoryCode: '', categoryName: '' })

const columns = computed(() => [
  { prop: 'categoryCode', label: '分类编码' },
  { prop: 'categoryName', label: '分类名称' },
  { prop: 'status', label: '状态' },
  { prop: 'approvalStatus', label: '审批状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await materialCategoryApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const reset = () => { query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number, s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const openCreate = ()=>{ Object.assign(form, { id: undefined, categoryCode:'', categoryName:'', parentId: undefined }); visible.value=true }
const openEdit = (row:MdMaterialCategory)=>{ Object.assign(form, row); visible.value=true }
const save = async()=>{ await materialCategoryApi.save(form); ElMessage.success('保存成功'); visible.value=false; load() }
const submit = async(id:number)=>{ await materialCategoryApi.submit(id); ElMessage.success('已提审'); load() }
const audit = async(id:number, approved:boolean)=>{ await materialCategoryApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }
const toggle = async(row:MdMaterialCategory)=>{ if(!row.id) return; row.status === 'ENABLED' ? await materialCategoryApi.disable(row.id) : await materialCategoryApi.enable(row.id); ElMessage.success('状态更新成功'); load() }

onMounted(load)
</script>
