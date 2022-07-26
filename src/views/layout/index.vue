<template>
  <a-layout style="height: 100vh">
    <a-layout-sider collapsible defaultCollapsed v-if="!isFullScreen">
      <comp-sider-menu></comp-sider-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header v-if="!isFullScreen">
        <a-row type="flex">
          <a-col :flex="1"></a-col>
          <a-col>
            <fullscreen-outlined style="font-size: 18px; cursor: pointer" @click="changeFullScreen" />
            <!-- <a-dropdown>
                <a class="ant-dropdown-link" @click.prevent>
                  管理员
                  <DownOutlined />
                </a>
                <template #overlay>
                  <a-menu>
                    <a-menu-item>
                      <a href="javascript:;">1st menu item</a>
                    </a-menu-item>
                    <a-menu-item>
                      <a href="javascript:;">2nd menu item</a>
                    </a-menu-item>
                    <a-menu-item>
                      <a href="javascript:;">3rd menu item</a>
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown> -->
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout-content class="navbar-layout-content">
        <a-breadcrumb v-if="!isFullScreen">
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

<script lang="ts" setup>
import { computed } from "vue";
import { FullscreenOutlined } from "@ant-design/icons-vue";
import { useCommonStore } from "@/store";
import compSiderMenu from "./modules/siderMenu/index.vue";

const commonStore = useCommonStore();

const isFullScreen = computed(() => commonStore.isFullScreen);
function changeFullScreen() {
  commonStore.setIsFullScreen(true);
}
</script>

<style lang="less" scoped>
// 内容背景色
@bg-color: #fff;

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
