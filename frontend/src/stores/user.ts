import { defineStore } from 'pinia'
import { meApi } from '../api/modules/system'
import type { UserProfile } from '../types/base'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('erp_token') || '',
    username: 'admin',
    profile: null as UserProfile | null,
    permissions: [] as string[],
  }),
  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('erp_token', token)
    },
    async loadProfile() {
      if (!this.token) return
      const res = await meApi()
      this.profile = res.data
      this.username = res.data?.realName || res.data?.username || 'admin'
      this.permissions = res.data?.permissions || []
    },
    hasPermission(code?: string) {
      if (!code) return true
      return this.permissions.includes(code)
    },
    logout() {
      this.token = ''
      this.profile = null
      this.permissions = []
      this.username = 'admin'
      localStorage.removeItem('erp_token')
    },
  },
})
