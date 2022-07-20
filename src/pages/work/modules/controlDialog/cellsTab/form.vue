<script setup>
import {ref} from "vue";

// 代码模式
const codeMode = ref(false);
</script>

<template>
    <template v-if="!codeMode">
        <a-row>
            <a-col :span="9">label</a-col>
            <a-col :span="9" :offset="1">input</a-col>
            <a-col :span="5">
                <a-button type="primary" style="margin: 0 5px;" @click="handleReset">
                    <template #icon>
                        <sync-outlined />
                    </template>
                </a-button>
                <a-button type="drange" style="margin: 0 5px;" @click="handleClear">
                    <template #icon>
                        <delete-outlined />
                    </template>
                </a-button>
            </a-col>
        </a-row>
        <div style="max-height: 300px; overflow: auto;">
            <a-row v-for="(item, index) in formMarkList" :key="index" style="margin-top: 5px;">
                <a-col :span="9">
                    <a-select v-model:value="item.label" :class="{
                        active: current === `${index}-label`
                    }" mode="multiple" style="width: 100%;" @click="current = `${index}-label`">
                        <template #tagRender="{ value: val, label, closable, onClose, option }">
                            <a-tag :closable="closable" style="margin-right: 3px" @close="handleRemoveTag(label, onClose)">
                            {{ label }}
                            </a-tag>
                        </template>
                    </a-select>
                </a-col>
                <a-col :span="9" :offset="1">
                    <a-select v-model:value="item.input" :class="{
                        active: current === `${index}-input`
                    }" mode="multiple" style="width: 100%;" @click="current = `${index}-input`">
                        <template #tagRender="{ value: val, label, closable, onClose, option }">
                            <a-tag :closable="closable" style="margin-right: 3px" @close="handleRemoveTag(label, onClose)">
                            {{ label }}
                            </a-tag>
                        </template>
                    </a-select>
                </a-col>
                <a-col :span="5">
                    <a-button type="primary" style="margin: 0 5px;" @click="handleAdd(index)">
                        <template #icon>
                            <plus-outlined />
                        </template>
                    </a-button>
                    <a-button type="danger" v-if="formMarkList.length > 1" @click="handleRemove(index)">
                        <template #icon>
                            <minus-outlined />
                        </template>
                    </a-button>
                </a-col>
            </a-row>
        </div>
    </template>
</template>