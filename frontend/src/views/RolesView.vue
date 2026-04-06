<template>
  <div>
    <SearchForm :model="query" @search="onSearch" @reset="onReset">
      <el-form-item label="角色关键字">
        <el-input v-model="query.keyword" placeholder="角色编码/名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新增角色</el-button>
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
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" link type="warning" @click="openGrant(row)">授权菜单</el-button>
          <el-button size="small" link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <el-dialog v-model="editVisible" :title="form.id ? '编辑角色' : '新增角色'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="角色编码"><el-input v-model="form.roleCode" /></el-form-item>
        <el-form-item label="角色名称"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="停用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="grantVisible" title="角色菜单授权" width="520px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        default-expand-all
        :props="{ label: 'menuName', children: 'children' }"
      />
      <template #footer>
        <el-button @click="grantVisible = false">取消</el-button>
        <el-button type="primary" :loading="granting" @click="onGrantSave">保存授权</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ElTree } from 'element-plus'
import SearchForm from '../components/common/SearchForm.vue'
import DataTable from '../components/common/DataTable.vue'
import { deleteRoleApi, listMenusBaseApi, listRoleMenusApi, listRolesApi, saveRoleApi, saveRoleMenusApi } from '../api/modules/base'
import type { MdMenu, MdRole } from '../types/base'

const loading = ref(false)
const saving = ref(false)
const granting = ref(false)
const rows = ref<MdRole[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const query = reactive({ keyword: '' })

const editVisible = ref(false)
const grantVisible = ref(false)
const treeRef = ref<InstanceType<typeof ElTree>>()
const currentRoleId = ref<number | null>(null)
const menuTree = ref<any[]>([])

const form = reactive<MdRole>({ roleCode: '', roleName: '', status: 'ENABLED' })

const columns = computed(() => [
  { prop: 'id', label: 'ID' },
  { prop: 'roleCode', label: '角色编码' },
  { prop: 'roleName', label: '角色名称' },
  { prop: 'status', label: '状态' },
])

const load = async () => {
  loading.value = true
  try {
    const res = await listRolesApi({ pageNo: pageNo.value, pageSize: pageSize.value, keyword: query.keyword || undefined })
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
  Object.assign(form, { id: undefined, roleCode: '', roleName: '', status: 'ENABLED' })
  editVisible.value = true
}

const openEdit = (row: MdRole) => {
  Object.assign(form, row)
  editVisible.value = true
}

const onSave = async () => {
  if (!form.roleCode || !form.roleName) {
    ElMessage.warning('请填写角色编码与角色名称')
    return
  }
  saving.value = true
  try {
    await saveRoleApi(form)
    ElMessage.success('保存成功')
    editVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const onDelete = async (row: MdRole) => {
  if (!row.id) return
  await ElMessageBox.confirm(`确认删除角色【${row.roleName}】吗？`, '提示', { type: 'warning' })
  await deleteRoleApi(row.id)
  ElMessage.success('删除成功')
  load()
}

const toTree = (list: MdMenu[]) => {
  const map = new Map<number, any>()
  const roots: any[] = []
  list.filter((x) => x.menuType === 'MENU').forEach((m) => map.set(m.id!, { ...m, children: [] }))
  map.forEach((node) => {
    if (!node.parentId || !map.has(node.parentId)) roots.push(node)
    else map.get(node.parentId).children.push(node)
  })
  return roots
}

const openGrant = async (row: MdRole) => {
  if (!row.id) return
  currentRoleId.value = row.id
  const [menusRes, roleMenusRes] = await Promise.all([listMenusBaseApi(), listRoleMenusApi(row.id)])
  menuTree.value = toTree(menusRes.data)
  grantVisible.value = true
  setTimeout(() => {
    treeRef.value?.setCheckedKeys(roleMenusRes.data || [])
  }, 0)
}

const onGrantSave = async () => {
  if (!currentRoleId.value) return
  granting.value = true
  try {
    const checked = treeRef.value?.getCheckedKeys(false) || []
    await saveRoleMenusApi(currentRoleId.value, checked as number[])
    ElMessage.success('授权保存成功')
    grantVisible.value = false
  } finally {
    granting.value = false
  }
}

onMounted(load)
</script>
