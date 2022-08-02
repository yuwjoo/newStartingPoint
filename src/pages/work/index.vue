<script lang="ts" setup>
import { ref } from "vue";
import compControlDialog from "./modules/controlDialog/index.vue";
import MarkCss from "./modules/mark/index.less";
import useManage from "./hooks/manage";

const manage = useManage();
// iframe dom
const iframeRef = ref<HTMLIFrameElement | null>(null);

// iframe加载完成，初始化页面
function initIFrame() {
  let iWindow = iframeRef.value!.contentWindow;
  let iDocument = iWindow?.document;
  if (!iWindow || !iDocument) {
    throw new Error("iframe网页加载失败");
  }
  // 追加标记样式
  let styleNode = iDocument.createElement("style");
  styleNode.setAttribute("type", "text/css");
  styleNode.innerHTML = MarkCss;
  iDocument.head.appendChild(styleNode);
  // 初始化标记
  let nodes = iDocument.body.querySelector("#base")!.querySelectorAll("[id]");
  manage.generate(Array.from(<NodeListOf<Element & HTMLElement>>nodes));
}
</script>

<template>
  <iframe ref="iframeRef" src="/api/e-database.html" width="100%" height="540px" frameborder="0" @load="initIFrame" />
  <comp-control-dialog />
</template>
