<template>
  <div>
    <SearchForm :model="query" @search="load" @reset="reset">
      <el-form-item label="用户名">
        <el-input v-model="query.username" placeholder="请输入用户名" clearable />
      </el-form-item>
    </SearchForm>

    <DataTable :columns="columns" :data="rows" :loading="loading" :total="total" :page-no="1" :page-size="10" @page-change="load" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import SearchForm from '../components/common/SearchForm.vue'
import DataTable from '../components/common/DataTable.vue'

const query = reactive({ username: '' })
const loading = ref(false)
const total = ref(1)
const rows = ref([{ username: 'admin', status: 'ENABLED' }])
const columns = [{ prop: 'username', label: '用户名' }, { prop: 'status', label: '状态' }]

const load = () => {}
const reset = () => { query.username = '' }
</script>
