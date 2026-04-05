import { defineStore } from 'pinia'

export interface TabItem {
  path: string
  title: string
}

export const useTabsStore = defineStore('tabs', {
  state: () => ({
    tabs: [{ path: '/', title: '首页' }] as TabItem[],
    activePath: '/',
  }),
  actions: {
    openTab(tab: TabItem) {
      if (!this.tabs.find((t) => t.path === tab.path)) this.tabs.push(tab)
      this.activePath = tab.path
    },
    removeTab(path: string) {
      this.tabs = this.tabs.filter((t) => t.path !== path)
      if (this.activePath === path) this.activePath = this.tabs[this.tabs.length - 1]?.path || '/'
    },
  },
})
