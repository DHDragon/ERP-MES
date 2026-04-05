import { computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTabsStore } from '../stores/tabs';
import { useUserStore } from '../stores/user';
import { useMenuStore } from '../stores/menu';
const route = useRoute();
const router = useRouter();
const tabsStore = useTabsStore();
const userStore = useUserStore();
const menuStore = useMenuStore();
const activePath = computed(() => route.path);
watch(() => route.fullPath, () => {
    const title = route.meta.title || '未命名页面';
    tabsStore.openTab({ path: route.path, title });
}, { immediate: true });
const go = (path) => router.push(path);
const logout = async () => {
    userStore.logout();
    await router.replace('/login');
};
onMounted(async () => {
    try {
        await menuStore.loadMenus();
    }
    catch (e) {
        // 后端菜单接口未就绪/无DB时，前端可降级为静态菜单
    }
});
const renderMenus = computed(() => {
    if (!menuStore.menus.length) {
        return [
            { key: '/', title: '首页', path: '/' },
            { key: '/base/users', title: '用户管理', path: '/base/users' },
            { key: '/base/menus', title: '菜单管理', path: '/base/menus' },
        ];
    }
    const list = menuStore.menus
        .filter((m) => m.menuType === 'MENU')
        .map((m) => ({
        key: String(m.id),
        title: m.menuName,
        path: m.path || undefined,
        parentId: (m.parentId ?? null),
    }));
    const byId = new Map();
    list.forEach((i) => byId.set(i.key, i));
    const roots = [];
    list.forEach((i) => {
        if (!i.parentId) {
            roots.push(i);
        }
        else {
            const parent = byId.get(String(i.parentId));
            if (parent) {
                parent.children = parent.children || [];
                parent.children.push(i);
            }
            else {
                roots.push(i);
            }
        }
    });
    return roots;
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
// CSS variable injection 
// CSS variable injection end 
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "layout-root" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.header, __VLS_intrinsicElements.header)({
    ...{ class: "topbar" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "logo" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "right" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({
    ...{ style: {} },
});
(__VLS_ctx.userStore.username);
const __VLS_0 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    ...{ 'onClick': {} },
    size: "small",
}));
const __VLS_2 = __VLS_1({
    ...{ 'onClick': {} },
    size: "small",
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
let __VLS_4;
let __VLS_5;
let __VLS_6;
const __VLS_7 = {
    onClick: (__VLS_ctx.logout)
};
__VLS_3.slots.default;
var __VLS_3;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "layout-body" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.aside, __VLS_intrinsicElements.aside)({
    ...{ class: "sidebar" },
});
const __VLS_8 = {}.ElMenu;
/** @type {[typeof __VLS_components.ElMenu, typeof __VLS_components.elMenu, typeof __VLS_components.ElMenu, typeof __VLS_components.elMenu, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
    defaultActive: (__VLS_ctx.activePath),
    router: true,
}));
const __VLS_10 = __VLS_9({
    defaultActive: (__VLS_ctx.activePath),
    router: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_9));
__VLS_11.slots.default;
for (const [m] of __VLS_getVForSourceType((__VLS_ctx.renderMenus))) {
    (m.key);
    if (m.children && m.children.length) {
        const __VLS_12 = {}.ElSubMenu;
        /** @type {[typeof __VLS_components.ElSubMenu, typeof __VLS_components.elSubMenu, typeof __VLS_components.ElSubMenu, typeof __VLS_components.elSubMenu, ]} */ ;
        // @ts-ignore
        const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
            index: (m.key),
        }));
        const __VLS_14 = __VLS_13({
            index: (m.key),
        }, ...__VLS_functionalComponentArgsRest(__VLS_13));
        __VLS_15.slots.default;
        {
            const { title: __VLS_thisSlot } = __VLS_15.slots;
            (m.title);
        }
        for (const [c] of __VLS_getVForSourceType((m.children))) {
            const __VLS_16 = {}.ElMenuItem;
            /** @type {[typeof __VLS_components.ElMenuItem, typeof __VLS_components.elMenuItem, typeof __VLS_components.ElMenuItem, typeof __VLS_components.elMenuItem, ]} */ ;
            // @ts-ignore
            const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
                key: (c.key),
                index: (c.path || c.key),
            }));
            const __VLS_18 = __VLS_17({
                key: (c.key),
                index: (c.path || c.key),
            }, ...__VLS_functionalComponentArgsRest(__VLS_17));
            __VLS_19.slots.default;
            (c.title);
            var __VLS_19;
        }
        var __VLS_15;
    }
    else {
        const __VLS_20 = {}.ElMenuItem;
        /** @type {[typeof __VLS_components.ElMenuItem, typeof __VLS_components.elMenuItem, typeof __VLS_components.ElMenuItem, typeof __VLS_components.elMenuItem, ]} */ ;
        // @ts-ignore
        const __VLS_21 = __VLS_asFunctionalComponent(__VLS_20, new __VLS_20({
            index: (m.path || m.key),
        }));
        const __VLS_22 = __VLS_21({
            index: (m.path || m.key),
        }, ...__VLS_functionalComponentArgsRest(__VLS_21));
        __VLS_23.slots.default;
        (m.title);
        var __VLS_23;
    }
}
var __VLS_11;
__VLS_asFunctionalElement(__VLS_intrinsicElements.main, __VLS_intrinsicElements.main)({
    ...{ class: "content-wrap" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "tabs-line" },
});
for (const [item] of __VLS_getVForSourceType((__VLS_ctx.tabsStore.tabs))) {
    const __VLS_24 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
        ...{ 'onClick': {} },
        key: (item.path),
        type: (__VLS_ctx.tabsStore.activePath === item.path ? 'primary' : 'info'),
        ...{ style: {} },
    }));
    const __VLS_26 = __VLS_25({
        ...{ 'onClick': {} },
        key: (item.path),
        type: (__VLS_ctx.tabsStore.activePath === item.path ? 'primary' : 'info'),
        ...{ style: {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_25));
    let __VLS_28;
    let __VLS_29;
    let __VLS_30;
    const __VLS_31 = {
        onClick: (...[$event]) => {
            __VLS_ctx.go(item.path);
        }
    };
    __VLS_27.slots.default;
    (item.title);
    var __VLS_27;
}
__VLS_asFunctionalElement(__VLS_intrinsicElements.section, __VLS_intrinsicElements.section)({
    ...{ class: "page-container" },
});
const __VLS_32 = {}.RouterView;
/** @type {[typeof __VLS_components.RouterView, ]} */ ;
// @ts-ignore
const __VLS_33 = __VLS_asFunctionalComponent(__VLS_32, new __VLS_32({}));
const __VLS_34 = __VLS_33({}, ...__VLS_functionalComponentArgsRest(__VLS_33));
/** @type {__VLS_StyleScopedClasses['layout-root']} */ ;
/** @type {__VLS_StyleScopedClasses['topbar']} */ ;
/** @type {__VLS_StyleScopedClasses['logo']} */ ;
/** @type {__VLS_StyleScopedClasses['right']} */ ;
/** @type {__VLS_StyleScopedClasses['layout-body']} */ ;
/** @type {__VLS_StyleScopedClasses['sidebar']} */ ;
/** @type {__VLS_StyleScopedClasses['content-wrap']} */ ;
/** @type {__VLS_StyleScopedClasses['tabs-line']} */ ;
/** @type {__VLS_StyleScopedClasses['page-container']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            tabsStore: tabsStore,
            userStore: userStore,
            activePath: activePath,
            go: go,
            logout: logout,
            renderMenus: renderMenus,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
