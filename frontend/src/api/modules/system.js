import http from '../request';
export const loginApi = (data) => http.post('/auth/login', data);
export const meApi = () => http.get('/auth/me');
export const healthApi = () => http.get('/health');
