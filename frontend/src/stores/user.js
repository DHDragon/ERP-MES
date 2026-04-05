import { defineStore } from 'pinia';
export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('erp_token') || '',
        username: 'admin',
    }),
    actions: {
        setToken(token) {
            this.token = token;
            localStorage.setItem('erp_token', token);
        },
        logout() {
            this.token = '';
            localStorage.removeItem('erp_token');
        },
    },
});
