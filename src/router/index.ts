import { createRouter, createWebHistory, createWebHashHistory, RouteRecordRaw } from "vue-router";

export const constantRoutes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Layout",
    component: () => import("@/views/layout/index.vue"),
    redirect: "/home",
    children: [
      {
        path: "home",
        name: "Home",
        component: () => import("@/pages/home/index.vue")
      }
    ],
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue")
  },
  {
    path: "/error",
    name: "Error",
    component: () => import("@/views/error/index.vue"),
    props: route => route.query
  },
  {
    path: "/:pathMatch(.*)*",
    name: "404",
    component: () => import("@/views/error/index.vue"),
    props: route => ({
      state: "404",
      redirect: route.path
    })
  }
];

/**
 * 创建路由
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
});

export default router;
