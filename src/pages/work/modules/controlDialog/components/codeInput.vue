<script lang="ts" setup>

const props = defineProps({
    // 代码文本
    codeText: [String, Number],
})
defineEmits(["update:codeText"]);

// 复制code
function handleCopy() {
    let inputNode = document.createElement("input");
    inputNode.value = <string>props.codeText;
    document.body.appendChild(inputNode);
    inputNode.select();
    document.execCommand('copy');
    document.body.removeChild(inputNode);
}

defineExpose({
    copy: handleCopy
})
</script>

<template>
    <div class="codeinput-container">
        <a-button type="primary" size="small" @click="handleCopy">复制</a-button>
        <textarea :value="props.codeText" rows="10" style="border: none; width: 100%;"
            @input="ev => $emit('update:codeText', (ev?.target as HTMLTextAreaElement)?.value)" />
    </div>
</template>