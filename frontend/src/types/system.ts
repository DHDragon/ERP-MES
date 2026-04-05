export interface SysMenu {
  id: number
  orgId: number
  parentId?: number
  menuCode: string
  menuName: string
  path?: string
  component?: string
  icon?: string
  sortNo: number
  menuType: string
  status: string
}
