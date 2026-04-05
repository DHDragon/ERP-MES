import type { Directive } from 'vue'

export const permissionDirective: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    // 占位：后续接 RBAC/数据权限策略
    const perms = Array.isArray(binding.value) ? binding.value : [binding.value]
    if (!perms || perms.length === 0) return
    const hasPermission = true
    if (!hasPermission) {
      el.style.display = 'none'
    }
  },
}
