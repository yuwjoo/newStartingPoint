import request from "@/permission/request";
import { LoginData } from "./index.d";

// 登录
export function login(data: LoginData) {
  return request({
    url: "work/login",
    method: "post",
    data,
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: "work/getUserInfo",
    method: "get",
  });
}
