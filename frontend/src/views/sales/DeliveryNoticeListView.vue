<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="通知号" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="router.push('/sales/delivery-notices/form')">新增通知</el-button></el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="pageNo" :page-size="pageSize" @page-change="onPage">
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/sales/delivery-notices/detail/${row.id}`)">详情</el-button>
          <el-button link type="success" @click="router.push(`/sales/delivery-notices/form/${row.id}`)">编辑</el-button>
          <el-button link type="warning" @click="audit(row.id)">审核</el-button>
          <el-button link @click="close(row.id)">关闭</el-button>
        </template>
      </el-table-column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchForm from '../../components/common/SearchForm.vue'
import DataTable from '../../components/common/DataTable.vue'
import { deliveryNoticeApi } from '../../api/modules/sales'
import type { DeliveryNoticeH } from '../../types/sales'

const router = useRouter()
const query = reactive({ keyword: '' })
const loading = ref(false)
const rows = ref<DeliveryNoticeH[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const columns = computed(() => [
  { prop: 'noticeNo', label: '通知号' },
  { prop: 'orderId', label: '销售订单ID' },
  { prop: 'bizDate', label: '业务日期' },
  { prop: 'totalQty', label: '通知数量' },
  { prop: 'outboundQty', label: '已出数量' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await deliveryNoticeApi.list({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const reset = () => { query.keyword=''; pageNo.value=1; load() }
const onPage = (p:number, s:number)=>{ pageNo.value=p; pageSize.value=s; load() }
const audit = async(id:number)=>{ await deliveryNoticeApi.audit(id); ElMessage.success('审核完成'); load() }
const close = async(id:number)=>{ await deliveryNoticeApi.close(id); ElMessage.success('已关闭'); load() }

onMounted(load)
</script>
