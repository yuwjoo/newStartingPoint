<script setup>
import { ref, toRefs } from "vue";
import FormTab from "./tabs/form-tab.vue"

const props = defineProps({
  codeMode: {
    type: Boolean
  }
})

const activeTabs = ref("form");
const formTabRef = ref(null);
const searchTabRef = ref(null);

function selectMark(markNode) {
  switch(activeTabs.value) {
    case "form":
      formTabRef.value.add(markNode);
    break;
    case "search":
      searchTabRef.value.add(markNode);
    break;
  }
}

defineExpose({
  selectMark
})
</script>

<template>
    <a-tabs v-model:activeKey="activeTabs" tab-position="left">
      <template #renderTabBar="{ DefaultTabBar, ...props }">
        <component class="tab_head" :is="DefaultTabBar" v-bind="props" />
      </template>
      <a-tab-pane key="form" tab="表单"><form-tab ref="formTabRef" :code-mode="props.codeMode" /></a-tab-pane>
      <a-tab-pane key="search" tab="筛选项"><form-tab ref="searchTabRef" isSearch :code-mode="props.codeMode" /></a-tab-pane>
      <a-tab-pane key="table" tab="表格">Content of Tab Pane 3</a-tab-pane>
    </a-tabs>
</template>

<style lang="less" scoped>
.tab_head {
    :deep(.ant-tabs-tab) {
        padding: 8px 0;
    }
}
</style>