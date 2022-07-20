<template>
  <div class="login">
    <h1 class="login-title">登录系统</h1>
    <a-form :model="form" name="basic" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" autocomplete="off"
      @submit="hanldeLogin">
      <a-form-item label="账号" name="username" :rules="[{ required: true, message: '请输入账号!' }]">
        <a-input v-model:value="form.username" />
      </a-form-item>

      <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码!' }]">
        <a-input-password v-model:value="form.password" />
      </a-form-item>

      <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
        <a-checkbox v-model:checked="form.remember">记住密码</a-checkbox>
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { useUserStore } from "@/store";
import { useRouter, useRoute } from "vue-router";

interface Form {
  username: string;
  password: string;
  remember: boolean;
}

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const form = reactive<Form>({
  username: localStorage.getItem("username") || "",
  password: localStorage.getItem("password") || "",
  remember: false,
});

// 登录
async function hanldeLogin() {
  await userStore.login(form);
  handleRemember();
  router.push({
    name: <string>route.query.redirect || "/",
  });
}

// 处理记住密码
function handleRemember() {
  localStorage.setItem("username", form.remember ? form.username : "");
  localStorage.setItem("password", form.remember ? form.password : "");
}
</script>

<style lang="less" scoped>
.login {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;

  .login-title {
    margin-bottom: 20px;
  }
}
</style>
