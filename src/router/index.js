import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: "/",
            name: 'Layout',
            component: () => import('@/views/layout/index.vue'),
            redirect: "/home",
            children: [
                {
                    path: "home",
                    name: "Home",
                    component: () => import("@/pages/home/index.vue")
                }
            ]
        }
    ]
})

export default router