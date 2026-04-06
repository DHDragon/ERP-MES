<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="用户关键字">
        <el-input v-model="query.keyword" placeholder="用户名/姓名" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新增用户</el-button>
      </el-form-item>
    </SearchForm>

    <DataTable
      :columns="columns"
      :data="rows"
      :loading="loading"
      :total="total"
      :page-no="pageNo"
      :page-size="pageSize"
      @page-change="onPageChange"
    >
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="visible" :title="form.id ? '编辑用户' : '新增用户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.mobile" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="停用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '../components/common/SearchForm.vue'
import DataTable from '../components/common/DataTable.vue'
import { deleteUserApi, listUsersApi, saveUserApi } from '../api/modules/base'
import type { MdUser } from '../types/base'

const loading = ref(false)
const saving = ref(false)
const rows = ref<MdUser[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const query = reactive({ keyword: '' })
const visible = ref(false)
const form = reactive<MdUser>({ username: '', status: 'ENABLED' })

const columns = computed(() => [
  { prop: 'id', label: 'ID' },
  { prop: 'username', label: '用户名' },
  { prop: 'realName', label: '姓名' },
  { prop: 'mobile', label: '手机号' },
  { prop: 'email', label: '邮箱' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await listUsersApi({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
    rows.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  pageNo.value = 1
  load()
}

const onReset = () => {
  query.keyword = ''
  pageNo.value = 1
  load()
}

const onPageChange = (p: number, s: number) => {
  pageNo.value = p
  pageSize.value = s
  load()
}

const openCreate = () => {
  Object.assign(form, {
    id: undefined,
    username: '',
    realName: '',
    mobile: '',
    email: '',
    status: 'ENABLED',
    passwordHash: '123456',
    orgId: 1,
  })
  visible.value = true
}

const openEdit = (row: MdUser) => {
  Object.assign(form, row)
  visible.value = true
}

const onSave = async () => {
  if (!form.username) {
    ElMessage.warning('请填写用户名')
    return
  }
  saving.value = true
  try {
    await saveUserApi(form)
    ElMessage.success('保存成功')
    visible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const onDelete = async (row: MdUser) => {
  if (!row.id) return
  await ElMessageBox.confirm(`确认删除用户【${row.username}】吗？`, '提示', { type: 'warning' })
  await deleteUserApi(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
