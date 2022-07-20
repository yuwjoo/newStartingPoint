<script setup>
import { ref, computed, watch, watchEffect, onMounted, toRefs, toRef, reactive } from "vue";
import { ShrinkOutlined, ArrowsAltOutlined } from "@ant-design/icons-vue";
import draggable from "./draggable"
import CellsTab from "./cellsTab/index.vue"

const props = defineProps({
  // 标记管理
  markManage: {
    type: Object
  }
})

const modal = reactive({
  // 是否显示弹出框
  visible: true,
  // 是否收起
  isShrink: false,
  // 弹出框位移样式
  transformStyle: ""
})
// 弹出框标题
const modalTitleRef = ref(null);

const tabs = reactive({
  // 当前激活tab
  activeTab: "cells",
  // 是否源码模式
  isCodeMode: false,
})
// 单元tab
const cellTabRef= ref(null);


// 拖拽功能
onMounted(() => {
  const { transformX, transformY } = draggable(modalTitleRef.value.$el.parentElement.parentElement);
  modal.transformStyle = computed(() => `transform: translate(${transformX.value}px, ${transformY.value}px)`);
})

// 标记管理初始化
const markList = ref([]);
const markManage = toRef(props, "markManage");
watch(markManage, manage => {
  manage.on("select-mark", marks => {
    console.log("marks", marks);
  })
  markList.value = manage.markList;
})

defineExpose({})
</script>

<template>
  <a-modal
    :width="650"
    :keyboard="false"
    :mask="false"
    :maskClosable="false"
    v-model:visible="modal.visible"
    :body-style="{ display: modal.isShrink ? 'none' : 'block', paddingTop: 0 }"
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
          <shrink-outlined v-if="!modal.isShrink" @click="modal.isShrink = true" />
          <arrows-alt-outlined v-else @click="modal.isShrink = false" />
        </a-col>
      </a-row>
    </template>
    <template #modalRender="{ originVNode }">
      <div :style="modal.transformStyle">
        <component :is="originVNode" />
      </div>
    </template>
    <a-tabs v-model:activeKey="tabs.activeTab">
      <a-tab-pane key="cells" tab="单元">
        <cells-tab ref="cellTabRef" />
      </a-tab-pane>
      <a-tab-pane key="searchForm" tab="筛选项">Content of Tab Pane 2</a-tab-pane>
      <a-tab-pane key="table" tab="表格">Content of Tab Pane 2</a-tab-pane>
      <a-tab-pane key="searchTable" tab="筛选表格">Content of Tab Pane 2</a-tab-pane>
    </a-tabs>
  </a-modal>
</template>
