/**
 * 路由权限
 */
import { RouteLocationRaw } from "vue-router"
import router from "@/router";
import { useUserStore, useRouterStore } from "@/store";

router.beforeEach(async (to, from, next) => {
  console.log(to)
  const routerStore = useRouterStore();

  // 白名单不拦截
  if (routerStore.whiteRoutes.includes(<string>to.name)) {
    next();
    return;
  }

  const userStore = useUserStore();

  // 没有token需要重新登录
  if (!userStore.token) {
    next(<RouteLocationRaw>{
      name: "Login",
      query: {
        redirect: to.name,
      },
      replace: true,
    });
    return;
  }
  // 没有角色说明未获取过用户信息
  if (!userStore.role) {
    try {
      await userStore.getInfo();
      userStore.routes.forEach(route => {
        router.addRoute(route)
      })
      next({ ...to, replace: true });
    } catch (err) {
      next(<RouteLocationRaw>{
        name: "Login",
        query: {
          redirect: to.name,
        },
        replace: true
      });
    }
    return;
  }
  next();
});