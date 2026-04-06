<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="菜单关键字">
        <el-input v-model="query.keyword" placeholder="菜单编码/名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新增菜单</el-button>
      </el-form-item>
    </SearchForm>

    <el-card shadow="never" v-loading="loading">
      <el-table :data="filteredRows" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="menuCode" label="菜单编码" min-width="180" />
        <el-table-column prop="menuName" label="菜单名称" min-width="160" />
        <el-table-column prop="menuType" label="类型" width="100" />
        <el-table-column prop="path" label="路由" min-width="160" />
        <el-table-column prop="permissionCode" label="权限点" min-width="180" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑菜单' : '新增菜单'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="父菜单ID"><el-input-number v-model="form.parentId" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="菜单编码"><el-input v-model="form.menuCode" /></el-form-item>
        <el-form-item label="菜单名称"><el-input v-model="form.menuName" /></el-form-item>
        <el-form-item label="菜单类型">
          <el-select v-model="form.menuType" style="width:100%">
            <el-option label="MENU" value="MENU" />
            <el-option label="BUTTON" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由"><el-input v-model="form.path" /></el-form-item>
        <el-form-item label="组件"><el-input v-model="form.component" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="权限点"><el-input v-model="form.permissionCode" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortNo" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
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
import { deleteMenuApi, listMenusBaseApi, saveMenuApi } from '../api/modules/base'
import type { MdMenu } from '../types/base'

const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const rows = ref<MdMenu[]>([])
const query = reactive({ keyword: '' })
const form = reactive<MdMenu>({ menuCode: '', menuName: '', menuType: 'MENU', status: 'ENABLED', sortNo: 0, orgId: 1 })

const filteredRows = computed(() => {
  if (!query.keyword.trim()) return rows.value
  const kw = query.keyword.trim().toLowerCase()
  return rows.value.filter((x) => `${x.menuCode} ${x.menuName}`.toLowerCase().includes(kw))
})

const load = async () => {
  loading.value = true
  try {
    const res = await listMenusBaseApi()
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const onSearch = () => load()

const onReset = () => {
  query.keyword = ''
}

const openCreate = () => {
  Object.assign(form, {
    id: undefined,
    parentId: undefined,
    menuCode: '',
    menuName: '',
    menuType: 'MENU',
    path: '',
    component: '',
    icon: '',
    permissionCode: '',
    sortNo: 0,
    status: 'ENABLED',
    orgId: 1,
  })
  visible.value = true
}

const openEdit = (row: MdMenu) => {
  Object.assign(form, row)
  visible.value = true
}

const onSave = async () => {
  if (!form.menuCode || !form.menuName) {
    ElMessage.warning('请填写菜单编码和菜单名称')
    return
  }
  saving.value = true
  try {
    await saveMenuApi(form)
    ElMessage.success('保存成功')
    visible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const onDelete = async (row: MdMenu) => {
  if (!row.id) return
  await ElMessageBox.confirm(`确认删除菜单【${row.menuName}】吗？`, '提示', { type: 'warning' })
  await deleteMenuApi(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
