import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
// 路由权限
import "@/permission/router";

const app = createApp(App);
app.use(router).use(createPinia());

app.mount("#app");
