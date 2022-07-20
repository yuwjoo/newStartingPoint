import { defineStore } from "pinia";

const useCommonStore = defineStore({
  id: "common",
  state() {
    return {
      isFullScreen: false, // 全屏模式
    };
  },
  actions: {
    setIsFullScreen(isCan: boolean) {
      this.isFullScreen = isCan;
    },
  },
});

export default useCommonStore;
