/**
 * 接口权限
 */
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import { useUserStore } from "@/store";
import { message } from 'ant-design-vue';

const whiteList = ["user/login"]

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
  } else if (!whiteList.includes(config.url || "")) {
    userStore.logout()
    throw new Error('token无效, 终止请求');
  }
  return config;
})

// 响应拦截器
service.interceptors.response.use((response: AxiosResponse) => {
  const { code, msg } = response.data || {};
  if (code === 200) {
    return response.data;
  }
  message.error(msg || "网络繁忙！");
  switch (code) {
    // token无效，重新登录
    case 10003:
      useUserStore().logout();
      break;
  }
  return Promise.reject(response);
}, err => {
  const { response } = err;
  message.error(response?.data?.msg || "网络繁忙！");
  return Promise.reject(response)
})

export default service;