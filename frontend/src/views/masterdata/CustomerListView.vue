<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="客户编码/名称" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="goCreate">新增客户</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
          <el-button link type="success" @click="goEdit(row.id)">编辑</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { customerApi } from '../../api/modules/masterdata'
import type { MdCustomer } from '../../types/masterdata'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<MdCustomer[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const columns = computed(() => [
  { prop: 'customerCode', label: '客户编码' },
  { prop: 'customerName', label: '客户名称' },
  { prop: 'contactName', label: '联系人' },
  { prop: 'contactPhone', label: '联系电话' },
  { prop: 'status', label: '状态' },
  { prop: 'approvalStatus', label: '审批状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await customerApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const onSearch = () => { pageNo.value = 1; load() }
const onReset = () => { query.keyword = ''; pageNo.value = 1; load() }
const onPage = (p:number, s:number) => { pageNo.value = p; pageSize.value = s; load() }

const goCreate = () => router.push('/masterdata/customers/form')
const goEdit = (id:number) => router.push(`/masterdata/customers/form/${id}`)
const goDetail = (id:number) => router.push(`/masterdata/customers/detail/${id}`)

const submit = async (id:number) => { await customerApi.submit(id); ElMessage.success('已提审'); load() }
const audit = async (id:number, approved:boolean) => { await customerApi.audit(id, { approved, remark: approved ? '通过':'驳回' }); ElMessage.success('审核完成'); load() }
const toggle = async (row:MdCustomer) => { if(!row.id) return; row.status === 'ENABLED' ? await customerApi.disable(row.id) : await customerApi.enable(row.id); ElMessage.success('状态更新成功'); load() }

const mockImport = async () => {
  await customerApi.import([{ customerCode: 'CUS-IMP-001', customerName: '导入客户' }])
  ElMessage.success('导入成功')
  load()
}

const doExport = async () => {
  const res = await customerApi.export()
  ElMessage.info(res.data.message)
}

onMounted(load)
</script>
