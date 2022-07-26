import request from "@/permission/request";
import { LoginData } from "@/types/api/login";

// 登录
export function login(data: LoginData) {
  return request({
    url: "user/login",
    method: "post",
    data,
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: "user/getUserInfo",
    method: "get",
  });
}
