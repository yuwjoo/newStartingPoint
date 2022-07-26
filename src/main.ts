import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
// 路由权限
import "@/permission/router";
// ant-design-vue样式
import 'ant-design-vue/dist/antd.less';

const app = createApp(App);
app.use(router).use(createPinia());

app.mount("#app");
