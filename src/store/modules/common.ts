import { defineStore } from "pinia";

const useCommonStore = defineStore({
  id: "common",
  state() {
    return {
      isFullScreen: localStorage.getItem("isFull") === 'true', // 全屏模式
    };
  },
  actions: {
    setIsFullScreen(isCan: boolean) {
      this.isFullScreen = isCan;
      localStorage.setItem("isFull", isCan + "");
    },
  },
});

export default useCommonStore;
