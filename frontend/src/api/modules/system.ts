import http from '../request'

export interface LoginReq {
  username: string
  password: string
}

export const loginApi = (data: LoginReq) => http.post('/auth/login', data)
export const meApi = () => http.get('/auth/me')
export const healthApi = () => http.get('/health')
