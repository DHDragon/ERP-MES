<template>
  <div class="layout-root">
    <header class="topbar">
      <div class="logo">ERP-MES</div>
      <div class="right">当前用户：{{ userStore.username }}</div>
    </header>

    <div class="layout-body">
      <aside class="sidebar">
        <el-menu :default-active="activePath" router>
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/base/users">用户管理</el-menu-item>
          <el-menu-item index="/base/menus">菜单管理</el-menu-item>
        </el-menu>
      </aside>

      <main class="content-wrap">
        <div class="tabs-line">
          <el-tag
            v-for="item in tabsStore.tabs"
            :key="item.path"
            :type="tabsStore.activePath === item.path ? 'primary' : 'info'"
            style="margin-right: 8px; cursor: pointer"
            @click="go(item.path)"
          >
            {{ item.title }}
          </el-tag>
        </div>
        <section class="page-container">
          <RouterView />
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTabsStore } from '../stores/tabs'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()
const userStore = useUserStore()

const activePath = computed(() => route.path)

watch(
  () => route.fullPath,
  () => {
    const title = (route.meta.title as string) || '未命名页面'
    tabsStore.openTab({ path: route.path, title })
  },
  { immediate: true },
)

const go = (path: string) => router.push(path)
</script>

<style scoped>
.layout-root { height: 100vh; display: flex; flex-direction: column; }
.topbar { height: 56px; background: #1f2937; color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; }
.logo { font-weight: 700; }
.layout-body { flex: 1; display: flex; }
.sidebar { width: 220px; border-right: 1px solid #e5e7eb; background: #fff; }
.content-wrap { flex: 1; display: flex; flex-direction: column; }
.tabs-line { height: 44px; display: flex; align-items: center; padding: 0 12px; background: #fff; border-bottom: 1px solid #eee; }
</style>
