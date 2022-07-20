/**
 * 接口权限
 */
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import { useUserStore } from "@/store";

const service = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 30000
})

// 请求拦截器
service.interceptors.request.use((config: AxiosRequestConfig) => {
    if (!config.headers) {
      throw new Error(
        `Expected 'config' and 'config.headers' not to be undefined`
      );
    }
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = userStore.token;
    } else {
        userStore.logout()
    }
    return config;
})

// 响应拦截器
service.interceptors.response.use((response: AxiosResponse) => {
    const { code, msg } = response.data;
    if(code === 200) {
        return response.data;
    } else {
        return Promise.reject(new Error(msg || "Error"))
    }
})

export default service;