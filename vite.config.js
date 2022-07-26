import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import Components from "unplugin-vue-components/vite";
import { AntDesignVueResolver } from "unplugin-vue-components/resolvers";
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
import vueJsx from "@vitejs/plugin-vue-jsx";
import path from "path";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      reactivityTransform: true
    }),
    vueJsx(),
    Components({
      resolvers: [AntDesignVueResolver({
        importStyle: false
      })],
    }),
    VueSetupExtend()
  ],
  resolve: {
    alias: {
      '~': path.resolve(__dirname, './'),
      '@': path.resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: {
          hack: `true; @import (reference) "${path.resolve(__dirname, 'src/assets/less/color.less')}";`,
        },
        // 第二种方式
        // additionalData:  `@import "${path.resolve(__dirname, 'src/assets/less/color.less')}";`,
        javascriptEnabled: true,
      }
    }
  },
  server: {
    port: 4550,
    proxy: {
      "/api": {
        target: "https://6jd7gy.axshare.com",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    },
  },
});
