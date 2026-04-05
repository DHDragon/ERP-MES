import http from '../request';
export const listMenusApi = () => http.get('/system/menus');
