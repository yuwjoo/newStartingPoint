<script setup>
import { reactive } from "vue"

const props = defineProps({
})

const form = reactive({
    formCodeMode: false
})
</script>

<template>
    <a-form class="form" layout="vertical" :mode="form">
        <a-form-item>
            <template #label>
                <a-row type="flex">
                    <a-col flex="auto">
                        表单
                    </a-col>
                    <a-col>
                        <a-switch v-model:checked="form.formCodeMode" checked-children="源码" un-checked-children="视图" />
                    </a-col>
                </a-row>
            </template>
            
        </a-form-item>
    </a-form>
</template>

<style lang="less" scoped>
.form {
    :deep(.ant-form-item-label>label) {
        width: 100%;
        display: block;
    }
}
</style>