<template>
    <template v-for="menu in list" :key="menu.name">
        <a-sub-menu v-if="menu.children" :key="menu.name">
            <template #title>
                <pie-chart-outlined />
                <span>{{ menu.name }}</span>
            </template>
            <sider-menu :list="menu.children" />
        </a-sub-menu>
        <template v-else>
            <a-menu-item :key="menu.name">
                <pie-chart-outlined />
                <span>{{ menu.name }}</span>
            </a-menu-item>
        </template>
    </template>
</template>

<script lang="ts" setup name="SiderMenu">
import { PieChartOutlined } from "@ant-design/icons-vue";
import type { Route } from "@/types/store/user"

defineProps({
    list: {
        type: Array<Route>,
        default: () => []
    }
})
</script>