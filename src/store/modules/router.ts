import { defineStore } from "pinia";

const useRouterStore = defineStore({
  id: "router",
  state() {
    return {
      // 白名单
      whiteRoutes: ["Login", "404"],
      // 异步添加的路由
      addRoutes: [],
    };
  },
  actions: {},
});

export default useRouterStore;
