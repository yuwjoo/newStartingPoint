<template>
  <router-view></router-view>
</template>

<script lang="ts" setup>
import { useEventListener } from "@/utils/event";
import { useCommonStore } from "@/store";

const commonStore = useCommonStore();

useEventListener(window, "keyup", (ev: KeyboardEvent) => {
  switch (ev.key) {
    // esc: 退出全屏
    case "Escape":
      commonStore.setIsFullScreen(false);
      break;
  }
});
</script>

<style lang="less">
#app {
  height: 100%;
}
</style>
