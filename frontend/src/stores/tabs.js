import { defineStore } from 'pinia';
export const useTabsStore = defineStore('tabs', {
    state: () => ({
        tabs: [{ path: '/', title: '首页' }],
        activePath: '/',
    }),
    actions: {
        openTab(tab) {
            if (!this.tabs.find((t) => t.path === tab.path))
                this.tabs.push(tab);
            this.activePath = tab.path;
        },
        removeTab(path) {
            this.tabs = this.tabs.filter((t) => t.path !== path);
            if (this.activePath === path)
                this.activePath = this.tabs[this.tabs.length - 1]?.path || '/';
        },
    },
});
