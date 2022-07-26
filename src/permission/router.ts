/**
 * 路由权限
 */
import router from "@/router";
import { useUserStore, useRouterStore } from "@/store";
import { useLoading } from "@/components/fullScreenLoading/index";

router.beforeEach(async (to, from, next) => {
  const routerStore = useRouterStore();
  const userStore = useUserStore();

  // 白名单不拦截
  if (routerStore.whiteRoutes.includes(<string>to.name)) {
    next();
    return;
  }

  if (userStore.token) {
    // 没有角色说明未获取过用户信息，需要初始化
    if (!userStore.role) {
      try {
        useLoading().show();
        await userStore.getInfo();
        routerStore.appendRouter(routerStore.generateRouter(userStore.routes));
        // replace?：如果访问一个不存在的路径to会指向404页面，初始化后可能这个路径又配置到路由上了，所以重新导航一遍，而不是直接放行
        next({
          path: to.path,
          replace: true
        });
      } catch (err: any) {
        // 不是token过期问题，统一处理成跳到异常页面
        if (err?.data?.msg !== 10003) {
          next({
            name: 'Error',
            query: {
              state: "500",
              redirect: to.path
            },
            replace: true
          });
        }
      }
      useLoading().hide();
    } else {
      // 既有token, 又已经初始化过，放行
      next();
    }
  } else {
    // 既没在白名单，又没登录过，只能引导到登录页了
    next({
      name: "Login",
      replace: true,
    });
  }
});