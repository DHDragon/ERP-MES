import http from '../request'
import type { UserProfile } from '../../types/base'

export interface LoginReq {
  username: string
  password: string
}

export interface LoginResp {
  token: string
  expiresIn: number
}

export const loginApi = (data: LoginReq) => http.post<LoginResp>('/auth/login', data)
export const meApi = () => http.get<UserProfile>('/auth/me')
export const healthApi = () => http.get('/health')
