import axios from 'axios';
import { ElMessage } from 'element-plus';
const http = axios.create({
    baseURL: '/api',
    timeout: 15000,
});
http.interceptors.request.use((config) => {
    const token = localStorage.getItem('erp_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
http.interceptors.response.use((response) => {
    const body = response.data;
    if (body && typeof body === 'object' && 'code' in body) {
        if (body.code !== 0) {
            ElMessage.error(body.message || '请求失败');
            return Promise.reject(new Error(body.message || '请求失败'));
        }
        return body;
    }
    // 非 Result 结构（比如文件流等）直接透传
    return response.data;
}, (error) => {
    const status = error.response?.status;
    if (status === 401) {
        ElMessage.error('登录已过期，请重新登录');
        localStorage.removeItem('erp_token');
    }
    else {
        ElMessage.error(error.message || '网络异常');
    }
    return Promise.reject(error);
});
export default http;
