<script lang="ts" setup>
import { ref, computed, onMounted, reactive } from "vue";
import { ShrinkOutlined, ArrowsAltOutlined } from "@ant-design/icons-vue";
import useDraggable from "./hooks/draggable"
import { useEventListener } from "@/utils/event";
import useManage from "../../hooks/manage";
import { message } from 'ant-design-vue';
import compFormTab from './modules/formTab.vue'
import compCodeInput from "./components/codeInput.vue"
import type { Row } from "ant-design-vue";
import type { ComputedRef } from "vue";
import type { MarkMessage } from "../mark";

interface Modal {
  visible: boolean,
  isShrink: boolean,
  transformStyle: ComputedRef<string> | ""
}

interface Tabs {
  activeTab: string,
  isCodeMode: boolean
}

// 标记管理器
const manage = useManage();
// 弹出框标题ref
const modalTitleRef = ref<InstanceType<typeof Row> | null>(null);
// 表单ref
const formTabRef = ref<InstanceType<typeof compFormTab> | null>(null);
// 筛选表单ref
const searchFormTabRef = ref<InstanceType<typeof compFormTab> | null>(null);

// 当前加载的url
const currentUrl = ref("");
// 源码内容
const codeContent = ref("");

const modal: Modal = reactive({
  // 是否显示弹出框
  visible: true,
  // 是否收起
  isShrink: false,
  // 弹出框位移样式
  transformStyle: ""
})

const tabs: Tabs = reactive({
  // 当前激活tab
  activeTab: "form",
  // 是否源码模式
  isCodeMode: false,
})

// 拖拽功能
onMounted(() => {
  const { transformX, transformY } = useDraggable(modalTitleRef.value!.$el.parentElement.parentElement);
  modal.transformStyle = computed(() => `transform: translate(${transformX.value}px, ${transformY.value}px)`);
})

// 监听iframe发送的消息, 管理标记操作
useEventListener(window, "message", (ev: MessageEvent) => {
  const { type, value } = <MarkMessage>ev.data;
  switch (type) {
    case "select":
      handleSelect(Number(value));
      break;
  }
})

// 处理标记选中
function handleSelect(index: number) {
  const mark = manage.selectMark(index, true);
  if (mark) {
    formTabRef.value?.addMark(mark);
  }
}

// 加载指定url界面
function loadUrl() {
  const isUrl = /^[http|https]:\/\//.test(currentUrl.value);
  if (isUrl) {
    console.log("加载url:", currentUrl.value)
  } else {
    message.error("url不合法!")
  }
}

// 是否显示源码视图改变
function changeIsCodeMode(is: boolean) {
  if (!is) {
    return
  }
  switch (tabs.activeTab) {
    case "form":
      codeContent.value = formTabRef.value!.getCode();
      break;
    case "searchForm":
      codeContent.value = searchFormTabRef.value!.getCode();
      break;
  }
}
</script>

<template>
  <a-modal :width="650" :keyboard="false" :mask="false" :maskClosable="false" v-model:visible="modal.visible"
    :body-style="{ display: modal.isShrink ? 'none' : 'block', paddingTop: 0 }"
    :wrap-style="{ overflow: 'hidden', pointerEvents: 'none' }" :footer="null" :closable="false">
    <!-- 对话框渲染容器（实现拖拽） -->
    <template #modalRender="{ originVNode }">
      <div :style="(modal.transformStyle as string)">
        <component :is="originVNode" />
      </div>
    </template>
    <!-- 头部 -->
    <template #title>
      <a-row ref="modalTitleRef" align="middle">
        <a-col :span="21" style="display: flex">
          <a-input v-model="currentUrl" placeholder="URL" />
          <a-button type="primary" ghost style="margin-left: 5px" @click="loadUrl">加载</a-button>
        </a-col>
        <a-col offset="2" :span="1">
          <shrink-outlined v-if="!modal.isShrink" @click="modal.isShrink = true" />
          <arrows-alt-outlined v-else @click="modal.isShrink = false" />
        </a-col>
      </a-row>
    </template>
    <a-switch v-model:checked="tabs.isCodeMode" checked-children="源码" un-checked-children="视图"
      style="margin-bottom: 5px;" @change="changeIsCodeMode" />
    <!-- 视图面板 -->
    <a-tabs v-show="!tabs.isCodeMode" v-model:activeKey="tabs.activeTab">
      <a-tab-pane key="form" tab="表单">
        <comp-form-tab ref="formTabRef" />
      </a-tab-pane>
      <a-tab-pane key="searchForm" tab="筛选项">
        <comp-form-tab ref="searchFormTabRef" is-search />
      </a-tab-pane>
      <a-tab-pane key="table" tab="表格">Content of Tab Pane 2</a-tab-pane>
    </a-tabs>
    <!-- 源码面板 -->
    <comp-code-input v-show="tabs.isCodeMode" v-model:codeText="codeContent" />
  </a-modal>
</template>
