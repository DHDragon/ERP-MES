export interface MenuNode {
  id: number
  orgId?: number
  parentId?: number | null
  menuCode: string
  menuName: string
  path?: string
  component?: string
  icon?: string
  permissionCode?: string
  sortNo?: number
  menuType: 'MENU' | 'BUTTON'
  status?: string
  children?: MenuNode[]
}

export type SysMenu = MenuNode
