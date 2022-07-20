<script setup>

const props = defineProps({
    // 代码文本
    codeText: [String, Number],
})
const emit = defineEmits(["update:codeText"]);

// 复制code
function handleCopy() {
    let inputNode = document.createElement("input");
    inputNode.value = props.codeText;
    document.body.appendChild(inputNode);
    inputNode.value.select();
    document.execCommand('copy');
    document.body.removeChild(inputNode);
}
</script>

<template>
    <div class="codeinput-container">
        <a-button type="primary" size="small" @click="handleCopy">复制</a-button>
        <textarea :value="props.codeText" rows="10" style="border: none; width: 100%;"
            @input="ev => $emit('update:codeText', ev.target.value)" />
    </div>
</template>