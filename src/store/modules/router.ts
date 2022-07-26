import { defineStore } from "pinia";
import router, { constantRoutes } from "@/router"
import { RouteRecordRaw } from "vue-router"
import { objectCopy } from "@/utils/common";
import type { Route } from "@/types/store/user"

const allModules = import.meta.glob("/src/pages/**/*.vue");

const useRouterStore = defineStore({
  id: "router",
  state() {
    return {
      // 白名单
      whiteRoutes: ["Login", "Error"] as Array<string>,
      // 基本路由
      baseRoutes: constantRoutes,
      // 异步添加的路由
      addRoutes: [] as Array<RouteRecordRaw>,
    };
  },
  actions: {
    /**
     * 生成路由（用于将异步获取的路由信息转换为路由对象）
     * @param routes 路由信息
     */
    generateRouter(routes: Array<Route>): Array<RouteRecordRaw> {
      const temp = objectCopy(routes);
      for (const route of temp) {
        // 解析路径对应的组件
        const compPath = (route.component || "").replace("@", "/src");
        const promiseComp = allModules[compPath];
        if (!promiseComp) {
          console.error("解析路由信息时，对应component不存在", JSON.parse(JSON.stringify(route)))
        }
        route.component = promiseComp;
        // 将路由扁平化，统一处在同一级
        if (route.children) {
          // 将子级路由提取出来，追加到被循环列表的尾部，等待后续处理
          const childrenRoutes = route.children.map((chRoute: { path: string }) => ({
            ...chRoute,
            path: `${route.path}/${chRoute.path}`
          }))
          temp.splice(temp.length, 0, ...childrenRoutes);
          delete route.children;
        }
      }
      return <Array<RouteRecordRaw>>temp
    },
    /**
     * 追加路由
     * @param routes 路由对象
     */
    appendRouter(routes: Array<RouteRecordRaw>) {
      routes.forEach(route => {
        router.addRoute("Layout", route);
        this.addRoutes.push(route);
      })
    },
    /**
     * 重置路由（删除所有追加的路由）
     */
    resetRouter() {
      this.addRoutes.forEach(({ name }) => {
        if (name && router.hasRoute(name)) {
          router.removeRoute(name);
        }
      });
      this.addRoutes.length = 0;
    }
  },
});

export default useRouterStore;
