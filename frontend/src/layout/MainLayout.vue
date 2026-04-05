<template>
  <div class="layout-root">
    <header class="topbar">
      <div class="logo">ERP-MES</div>
      <div class="right">
        <span style="margin-right:12px">当前用户：{{ userStore.username }}</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </header>

    <div class="layout-body">
      <aside class="sidebar">
        <el-menu :default-active="activePath" router>
          <template v-for="m in renderMenus" :key="m.key">
            <el-sub-menu v-if="m.children && m.children.length" :index="m.key">
              <template #title>{{ m.title }}</template>
              <el-menu-item v-for="c in m.children" :key="c.key" :index="c.path || c.key">
                {{ c.title }}
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="m.path || m.key">{{ m.title }}</el-menu-item>
          </template>
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
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTabsStore } from '../stores/tabs'
import { useUserStore } from '../stores/user'
import { useMenuStore } from '../stores/menu'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()
const userStore = useUserStore()
const menuStore = useMenuStore()

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

const logout = async () => {
  userStore.logout()
  await router.replace('/login')
}

onMounted(async () => {
  try {
    await menuStore.loadMenus()
  } catch (e) {
    // 后端菜单接口未就绪/无DB时，前端可降级为静态菜单
  }
})

type RenderMenu = { key: string; title: string; path?: string; parentId?: number | null; children?: RenderMenu[] }

const renderMenus = computed<RenderMenu[]>(() => {
  if (!menuStore.menus.length) {
    return [
      { key: '/', title: '首页', path: '/' },
      { key: '/base/users', title: '用户管理', path: '/base/users' },
      { key: '/base/menus', title: '菜单管理', path: '/base/menus' },
    ]
  }

  const list = menuStore.menus
    .filter((m) => m.menuType === 'MENU')
    .map((m) => ({
      key: String(m.id),
      title: m.menuName,
      path: m.path || undefined,
      parentId: (m.parentId ?? null) as any,
    }))

  const byId = new Map<string, RenderMenu>()
  list.forEach((i) => byId.set(i.key, i))

  const roots: RenderMenu[] = []
  list.forEach((i) => {
    if (!i.parentId) {
      roots.push(i)
    } else {
      const parent = byId.get(String(i.parentId))
      if (parent) {
        parent.children = parent.children || []
        parent.children.push(i)
      } else {
        roots.push(i)
      }
    }
  })

  return roots
})
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
