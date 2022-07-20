<template>
    <a-menu theme="dark" mode="inline" @select="onMenuSelect">
        <a-menu-item key="Home">
            <pie-chart-outlined />
            <span>Home</span>
        </a-menu-item>
        <a-menu-item key="2">
            <desktop-outlined />
            <span>Option 2</span>
        </a-menu-item>
        <a-sub-menu key="sub1">
            <template #title>
                <span>
                    <user-outlined />
                    <span>User</span>
                </span>
            </template>
            <a-menu-item key="3">Tom</a-menu-item>
            <a-menu-item key="4">Bill</a-menu-item>
            <a-menu-item key="5">Alex</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub2">
            <template #title>
                <span>
                    <team-outlined />
                    <span>Team</span>
                </span>
            </template>
            <a-menu-item key="6">Team 1</a-menu-item>
            <a-menu-item key="8">Team 2</a-menu-item>
        </a-sub-menu>
        <a-menu-item key="9">
            <file-outlined />
            <span>File</span>
        </a-menu-item>
        <a-menu-item key="Work">
            <file-outlined />
            <span>Work</span>
        </a-menu-item>
    </a-menu>
</template>

<script lang="ts" setup>
import { PieChartOutlined, DesktopOutlined, UserOutlined, TeamOutlined, FileOutlined } from "@ant-design/icons-vue";
import { useRouter } from "vue-router"
const router = useRouter();

function onMenuSelect({ key }: { key: string }) {
    router.push({
        name: key
    })
}
</script>
