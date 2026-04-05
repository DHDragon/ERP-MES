import http from '../request'
import type { SysMenu } from '../../types/system'

export const listMenusApi = () => http.get<SysMenu[]>('/system/menus')
