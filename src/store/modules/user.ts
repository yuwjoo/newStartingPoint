import { defineStore } from "pinia";
import router, { resetRouter } from "@/router";
import { login, getUserInfo } from "@/api/login"
import { LoginData } from "@/api/login/index.d";

const useUserStore = defineStore({
  id: "user",
  state() {
    return {
      token: localStorage.getItem("token"),
      // 所属角色
      role: "",
      // 拥有路由
      routes: []
    };
  },
  actions: {
    // 设置token
    setToken(val: string) {
      this.token = val;
      localStorage.setItem("token", val);
    },
    // 登出
    logout() {
      // 重置路由
      resetRouter();
      localStorage.setItem("token", "");
      // 重置user仓库
      this.$reset();
      router.replace({
        name: "Login",
      });
    },
    // 登入
    async login(data: LoginData) {
      const res = await login({
        username: data.username,
        password: data.password,
      });
      const { token } = res.data || {};
      this.setToken(token);
    },
    // 获取用户信息
    async getInfo() {
      const res = await getUserInfo();
      const { routes, role } = res.data || {};
      this.role = role;
      this.routes = routes || [];
    }
  },
});

export default useUserStore