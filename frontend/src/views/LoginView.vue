<template>
  <div class="login-root">
    <el-card class="login-card">
      <template #header>登录</template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="color:#666;font-size:12px">默认账号：admin / admin123</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi } from '../api/modules/system'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: 'admin', password: 'admin123' })
const loading = ref(false)

const submit = async () => {
  loading.value = true
  try {
    const res = await loginApi(form)
    userStore.setToken(res.data.token)
    ElMessage.success('登录成功')
    await router.replace('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-root { height: 100vh; display:flex; align-items:center; justify-content:center; background:#f5f7fa; }
.login-card { width: 420px; }
</style>
