<script lang="ts" setup>
import { ref, shallowRef } from "vue";
import ControlDialog from "./modules/controlDialog/index.vue";
import MarkCss from "./modules/mark/index.less";
import MarkManage from "./utils/MarkManage";

// iframe dom
const iframeRef = ref(null);
// 标记管理器
const markManage = shallowRef(null);

// iframe加载完成
function onIframeLoad() {
  let iWindow = iframeRef.value.contentWindow;
  let iDocument = iWindow.document;
  let styleNode = iDocument.createElement("style");
  styleNode.setAttribute("type", "text/css");
  styleNode.innerHTML = MarkCss;
  iDocument.head.appendChild(styleNode);
  let nodes = iDocument.body.querySelector("#base").querySelectorAll("[id]");
  markManage.value = new MarkManage(nodes);
}
</script>

<template>
  <iframe ref="iframeRef" src="/api/e-database.html" width="100%" height="540px" frameborder="0" @load="onIframeLoad" />
  <control-dialog :mark-manage="markManage" />
</template>
