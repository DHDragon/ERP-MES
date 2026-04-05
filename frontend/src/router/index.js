import { createRouter, createWebHistory } from 'vue-router';
import MainLayout from '../layout/MainLayout.vue';
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/login', component: () => import('../views/LoginView.vue'), meta: { title: '登录', public: true } },
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
});
router.beforeEach((to) => {
    const isPublic = Boolean(to.meta.public);
    const token = localStorage.getItem('erp_token');
    if (!isPublic && !token) {
        return '/login';
    }
    return true;
});
export default router;
