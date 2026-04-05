import { defineStore } from 'pinia';
import { listMenusApi } from '../api/modules/menu';
export const useMenuStore = defineStore('menu', {
    state: () => ({
        menus: [],
        loaded: false,
    }),
    actions: {
        async loadMenus() {
            if (this.loaded)
                return;
            const res = await listMenusApi();
            this.menus = res.data;
            this.loaded = true;
        },
    },
});
