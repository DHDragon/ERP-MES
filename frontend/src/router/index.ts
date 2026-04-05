import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', component: () => import('../views/HomeView.vue'), meta: { title: '首页' } },
        { path: '/base/users', component: () => import('../views/UsersView.vue'), meta: { title: '用户管理' } },
        { path: '/base/menus', component: () => import('../views/MenusView.vue'), meta: { title: '菜单管理' } },
      ],
    },
  ],
})

export default router
