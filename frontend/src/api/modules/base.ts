import http from '../request'
import type { PageResult } from '../../types/common'
import type { CfgSystemParam, DictItem, MdMenu, MdRole, MdUser } from '../../types/base'

export const listUsersApi = (params: { pageNo: number; pageSize: number; keyword?: string }) =>
  http.get<PageResult<MdUser>>('/base/users', { params })

export const saveUserApi = (data: MdUser) => http.post<MdUser>('/base/users', data)

export const deleteUserApi = (id: number) => http.delete<string>(`/base/users/${id}`)

export const listRolesApi = (params: { pageNo: number; pageSize: number; keyword?: string }) =>
  http.get<PageResult<MdRole>>('/base/roles', { params })

export const saveRoleApi = (data: MdRole) => http.post<MdRole>('/base/roles', data)

export const deleteRoleApi = (id: number) => http.delete<string>(`/base/roles/${id}`)

export const listMenusBaseApi = () => http.get<MdMenu[]>('/base/menus')

export const saveMenuApi = (data: MdMenu) => http.post<MdMenu>('/base/menus', data)

export const deleteMenuApi = (id: number) => http.delete<string>(`/base/menus/${id}`)

export const listRoleMenusApi = (roleId: number) => http.get<number[]>(`/base/role-menus/${roleId}`)

export const saveRoleMenusApi = (roleId: number, menuIds: number[]) =>
  http.post<number[]>(`/base/role-menus/${roleId}`, { menuIds })

export const listParamsApi = () => http.get<CfgSystemParam[]>('/base/params')

export const updateParamApi = (paramKey: string, paramValue: string) =>
  http.put<CfgSystemParam>(`/base/param/${paramKey}`, { paramValue })

export const dictByTypeApi = (dictType: string) => http.get<DictItem[]>(`/base/dict/${dictType}`)

export const batchDictApi = (dictTypes: string[]) =>
  http.post<Record<string, DictItem[]>>('/base/dicts/batch', { dictTypes })
