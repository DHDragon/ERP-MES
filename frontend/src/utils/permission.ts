import type { Directive } from 'vue'
import { useUserStore } from '../stores/user'

export const permissionDirective: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const perms = Array.isArray(binding.value) ? binding.value : [binding.value]
    const validPerms = perms.filter(Boolean)
    if (!validPerms.length) return

    const hasPermission = validPerms.some((p) => userStore.hasPermission(p))
    if (!hasPermission) {
      el.style.display = 'none'
    }
  },
}
