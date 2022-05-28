<script setup>
  import compSiderMenu from "./modules/sider-menu.vue"
</script>

<template>
  <a-layout style="height: 100vh;">
    <a-layout-sider collapsible :collapsed-width="0" defaultCollapsed>
        <comp-sider-menu></comp-sider-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header>Header</a-layout-header>
      <a-layout-content class="navbar-layout-content">
        <a-breadcrumb>
            <a-breadcrumb-item>Home</a-breadcrumb-item>
            <a-breadcrumb-item>List</a-breadcrumb-item>
            <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
      </a-layout-content>
      <a-layout-content class="main-layout-content">
          <div class="main-layout-content__bg">
                <router-view />
          </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style lang="less" scoped>
// 内容背景色
@bg-color: #fff;
// 

.ant-layout-header {
    background-color: @bg-color;
}

.navbar-layout-content {
    margin: 5px 10px;
    flex: 0 0 auto;
}

.main-layout-content {
    overflow: auto;
    margin: 0 0 10px 10px;
    padding-right: 10px;

    .main-layout-content__bg {
        min-height: 100%;
        background-color: @bg-color;
        padding: 10px;
    }
}
</style>