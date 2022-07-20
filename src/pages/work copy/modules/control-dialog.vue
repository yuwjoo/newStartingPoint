<script setup>
import { ref, computed, watch, watchEffect, onMounted } from "vue";
import { useDraggable } from "@vueuse/core";
import { ShrinkOutlined, ArrowsAltOutlined } from "@ant-design/icons-vue";
import BaseTab from "./base-tab/index.vue"

const visible = ref(true);
const modalTitleRef = ref(null);
const baseTabRef= ref(null);
const isShrink = ref(false);
const activeTabs = ref("base");
const isCodeMode = ref(false);

const { x, y, isDragging, ...arg } = useDraggable(
  computed(() =>
    modalTitleRef.value
      ? modalTitleRef.value.$el.parentElement.parentElement
      : null
  )
);

const startX = ref(0);
const startY = ref(0);
const startedDrag = ref(false);
const transformX = ref(0);
const transformY = ref(0);
const preTransformX = ref(0);
const preTransformY = ref(0);
const dragRect = ref({
  left: 0,
  right: 0,
  top: 0,
  bottom: 0,
});
watch([x, y], () => {
  if (!startedDrag.value) {
    startX.value = x.value;
    startY.value = y.value;
    const bodyRect = document.body.getBoundingClientRect();
    const titleRect = modalTitleRef.value.$el.getBoundingClientRect();
    dragRect.value.left = 50 - titleRect.width;
    dragRect.value.right = bodyRect.width - 100;
    dragRect.value.bottom = bodyRect.height - titleRect.height;
    preTransformX.value = transformX.value;
    preTransformY.value = transformY.value;
  }
  startedDrag.value = true;
});
watch(isDragging, () => {
  if (!isDragging) {
    startedDrag.value = false;
  }
});
watchEffect(() => {
  if (startedDrag.value) {
    transformX.value =
      preTransformX.value +
      Math.min(Math.max(dragRect.value.left, x.value), dragRect.value.right) -
      startX.value;
    transformY.value =
      preTransformY.value +
      Math.min(Math.max(dragRect.value.top, y.value), dragRect.value.bottom) -
      startY.value;
  }
});
const transformStyle = computed(() => {
  return {
    transform: `translate(${transformX.value}px, ${transformY.value}px)`,
  };
});

function selectMark(markNode) {
  switch(activeTabs.value) {
    case "base":
      baseTabRef.value.selectMark(markNode);
    break;
  }
}

defineExpose({
  selectMark
})
</script>

<template>
  <a-modal
    :width="650"
    :keyboard="false"
    :mask="false"
    :maskClosable="false"
    v-model:visible="visible"
    :body-style="{ display: isShrink ? 'none' : 'block', paddingTop: 0 }"
    :wrap-style="{ overflow: 'hidden', pointerEvents: 'none' }"
    :footer="null"
    :closable="false"
  >
    <template #title>
      <a-row ref="modalTitleRef" align="middle">
        <a-col :span="21" style="display: flex">
          <a-input placeholder="URL" />
          <a-button type="primary" ghost style="margin-left: 5px">加载</a-button>
        </a-col>
        <a-col offset="2" :span="1">
          <shrink-outlined v-if="!isShrink" @click="isShrink = true" />
          <arrows-alt-outlined v-else @click="isShrink = false" />
        </a-col>
      </a-row>
    </template>
    <template #modalRender="{ originVNode }">
      <div :style="transformStyle">
        <component :is="originVNode" />
      </div>
    </template>
    <a-tabs v-model:activeKey="activeTabs">
      <template #renderTabBar="{ DefaultTabBar, ...props }">
        <component :is="DefaultTabBar" v-bind="props" style="margin-bottom: 10px;" />
        <div style="margin-bottom: 6px;">
          <a-switch v-model:checked="isCodeMode" checked-children="源码" un-checked-children="列表" />
        </div>
      </template>
      <a-tab-pane key="base" tab="基本"><base-tab ref="baseTabRef" :code-mode="isCodeMode" /></a-tab-pane>
      <a-tab-pane key="searchTable" tab="筛选表格">Content of Tab Pane 2</a-tab-pane>
      <a-tab-pane key="form" tab="表单">Content of Tab Pane 3</a-tab-pane>
    </a-tabs>
  </a-modal>
</template>
