<template>
    <template v-if="state === '404'">
        <a-result status="404" title="404" sub-title="抱歉，此页面未找到！">
            <template #extra>
                <a-button type="primary" @click="router.push({ name: 'Home' })">回到首页</a-button>
            </template>
        </a-result>
    </template>
    <template v-else-if="state === '403'">
        <a-result status="403" title="403" sub-title="抱歉，你没有此页面的访问权限！">
            <template #extra>
                <a-button type="primary" @click="router.push({ name: 'Home' })">回到首页</a-button>
            </template>
        </a-result>
    </template>
    <template v-else-if="state === '500'">
        <a-result status="500" title="500" sub-title="抱歉，服务器发生了错误！">
            <template #extra>
                <a-button type="primary" @click="router.replace(redirect)">刷新页面</a-button>
                <a-button type="danger" @click="router.push({ name: 'Login' })">重新登录</a-button>
            </template>
        </a-result>
    </template>
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router"

const { state = "404", redirect = "/home" } = defineProps<{
    // 异常状态
    state?: string,
    // 重定向路径
    redirect?: string
}>()

const router = useRouter();
</script>