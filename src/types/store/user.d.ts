// 服务器返回的路由信息
export type Route = {
    path: string,
    name: string,
    icon?: string,
    component?: any,
    children?: Routes,
    meta?: {
        // 是否显示在侧边栏导航中
        hidden?: boolean
    }
}