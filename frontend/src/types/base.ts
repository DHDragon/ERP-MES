export interface MdUser {
  id?: number
  orgId?: number
  deptId?: number
  username: string
  passwordHash?: string
  realName?: string
  mobile?: string
  email?: string
  status?: string
}

export interface MdRole {
  id?: number
  orgId?: number
  roleCode: string
  roleName: string
  status?: string
}

export interface MdMenu {
  id?: number
  orgId?: number
  parentId?: number | null
  menuCode: string
  menuName: string
  menuType: 'MENU' | 'BUTTON'
  path?: string
  component?: string
  icon?: string
  permissionCode?: string
  sortNo?: number
  status?: string
}

export interface CfgSystemParam {
  id?: number
  orgId?: number
  paramKey: string
  paramName: string
  paramValue: string
  status?: string
}

export interface DictItem {
  code: string
  name: string
  sort?: number
  status?: string
}

export interface UserProfile {
  id: number
  username: string
  realName?: string
  orgId?: number
  deptId?: number
  roles: string[]
  permissions: string[]
}
