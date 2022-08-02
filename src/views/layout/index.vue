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
            <a-space align="center" size="large">
              <fullscreen-outlined style="font-size: 18px; cursor: pointer; transform: translateY(2px);"
                @click="changeFullScreen" />
              <a-dropdown>
                <a>
                  管理员
                  <down-outlined />
                </a>
                <template #overlay>
                  <a-menu>
                    <a-menu-item>
                      <a href="javascript:;" @click="userStore.logout">退出登录</a>
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
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
import { FullscreenOutlined, DownOutlined } from "@ant-design/icons-vue";
import { useCommonStore, useUserStore } from "@/store";
import compSiderMenu from "./modules/siderMenu/index.vue";

const commonStore = useCommonStore();
const userStore = useUserStore();

const isFullScreen = computed(() => commonStore.isFullScreen);
function changeFullScreen() {
  commonStore.setIsFullScreen(true);
}
</script>

<style lang="less" scoped>
.ant-layout-header {
  background-color: @component-background;
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
    background-color: @component-background;
    padding: 10px;
  }
}
</style>
