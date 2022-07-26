<template>
    <a-menu theme="dark" mode="inline" @select="onMenuSelect">
        <comp-menu :list="menuList" />
    </a-menu>
</template>

<script lang="ts" setup>
import { computed } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/store"
import compMenu from "./menu.vue"
import type { Route } from "@/types/store/user"

const router = useRouter();
const userStore = useUserStore();

// 菜单列表
const menuList = computed(() => [
    <Route>{
        path: "/home",
        name: "Home",
    },
    ...userStore.menuList
])

function onMenuSelect({ key }: { key: string }) {
    router.push({
        name: key
    })
}
</script>
