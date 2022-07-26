import { defineStore } from "pinia";
import router from "@/router";
import { login, getUserInfo } from "@/api/login"
import { LoginData } from "@/types/api/login";
import useRouterStore from "./router"
import type { Route } from "@/types/store/user"

const useUserStore = defineStore({
  id: "user",
  state() {
    return {
      token: localStorage.getItem("token"),
      // 所属角色
      role: "",
      // 拥有的路由信息
      routes: <Array<Route>>[]
    };
  },
  getters: {
    // 导航菜单列表
    menuList: state => {
      const handleRoute = (routes: Array<Route>): Array<Route> => {
        const temp = <Array<Route>>[];
        routes.forEach(route => {
          if (!route?.meta?.hidden) {
            let childrens = <Array<Route>>[];
            if (route.children?.length) {
              childrens = handleRoute(route.children);
            }
            route.children = childrens.length ? route.children : undefined;
            temp.push(route);
          }
        })
        return temp;
      }
      return handleRoute(state.routes);
    }
  },
  actions: {
    // 设置token
    setToken(val: string) {
      this.token = val;
      localStorage.setItem("token", val);
    },
    // 登出
    logout() {
      const routerStore = useRouterStore();
      // 重置路由
      routerStore.resetRouter();
      // 重置持久化token
      localStorage.setItem("token", "");
      // 重置user仓库
      this.$reset();
      router.replace({
        name: "Login",
      });
    },
    // 登录
    async login(data: LoginData) {
      const res = await login({
        username: data.username,
        password: data.password,
      });
      this.setToken(res.data || "");
    },
    // 获取用户信息
    async getInfo() {
      const res = await getUserInfo();
      const { routes, role } = res.data || {};
      this.$patch({
        role,
        routes: routes || []
      })
    }
  },
});

export default useUserStore