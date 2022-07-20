<script setup>
import { ref, reactive, toRefs, watchEffect, inject, watch } from "vue";
import { PlusOutlined, MinusOutlined, SyncOutlined, DeleteOutlined } from "@ant-design/icons-vue"

const props = defineProps({
    // 源码模式
    codeMode: {
        type: Boolean
    },
    // 是筛选表单
    isSearch: {
        type: Boolean
    }
})
const codeContent = ref("");
const codeInputRef = ref(null);

watchEffect(() => {
    if(props.codeMode) {
        computeCode();
    }
})

const formMarkList = reactive([formMarkInitItem()]);
const current = ref("0-label");

function init() {
    formMarkList.splice(0, Infinity, formMarkInitItem());
}

function formMarkInitItem () {
    return {
        label: [],
        input: []
    }
}

function add(mark) {
    if(props.codeMode) return;
    mark.style.backgroundColor = "#1890ff";
    let pos = current.value.split("-");
    formMarkList[pos[0]][pos[1]].push(mark.index);
    if(formMarkList[pos[0]][pos[1]].length >= 1) {
        if(pos[1] === "label") {
            current.value = `${pos[0]}-input`;
        } else {
            if(formMarkList.length - 1 <= pos[0]);
            formMarkList.splice(pos[0] + 1, 0, formMarkInitItem());
            current.value = `${Number(pos[0]) + 1}-label`;
        }
    }
}

function computeCode() {
    const importPacks = {};
    const vars = {};
    const formSetting = [];

    const handleLabel = (labelMark, inputMark) => {
        if(labelMark.length === 1) {
            let bundle = labelMark[0].bundle || {};
            return `'${bundle.data}'`;
        }
        return `''`;
    }
    const handleProp = (labelMark, inputMark) => {
        if(labelMark.length !== 1) return '';
        let bundle = labelMark[0].bundle || {};
        let text = (bundle.data || "").replace(/\W/g, '');
        text = text.charAt(0).toLocaleLowerCase() + text.slice(1);
        return text
    }
    const handleDefault = (labelMark, inputMark) => {
        if(inputMark.length !== 1) return `''`;
        switch(inputMark[0].bundle.type) {
            default:
                return `''`;
        }
    }
    const handleRule = (labelMark, inputMark) => {
        if(props.isSearch || labelMark.length !== 1) {
            return "";
        }
        let labelBundle = labelMark[0].bundle || {};
        let inputBundle = inputMark[0].bundle || {};
        let required, limitLength;
        if(labelBundle.required) {
            required = `{ required: true, message: 'Please ${inputBundle.type === 'select' ? 'select' : 'input'} your ${labelBundle.data}!' }`
        }
        if(inputBundle.type === "input") {
            const importValue = importPacks["@/utils/helper"] || [];
            if(importValue.indexOf("isLegalName100Validator") === -1) {
                importValue.push("isLegalName100Validator");
                importPacks["@/utils/helper"] = importValue;
            }
            limitLength = `{ validator: isLegalName100Validator, trigger: 'blur' }`
        }
        if(inputBundle.type === "textArea") {
            const importValue = importPacks["@/utils/helper"] || [];
            if(importValue.indexOf("isLegalName500Validator") === -1) {
                importValue.push("isLegalName500Validator");
                importPacks["@/utils/helper"] = importValue;
            }
            limitLength = `{ validator: isLegalName500Validator, trigger: 'blur' }`
        }
        if(!required && !limitLength) return "";
        return `
          rule: [
              ${[required, limitLength].filter(v=>v).join(",\n")}
          ],`
    }
    const handleInput = (labelMark, inputMark) => {
        const propText = handleProp(labelMark, inputMark);
        const preVModel = props.isSearch ? "this.search.form.model." : "this.form.model.";
        let createTag = inputBundle => {
            switch(inputBundle.type) {
                case "select":
                    const listText = `${propText}List`;
                    vars[listText] = JSON.stringify(inputBundle.data.map(v=>({
                        id: v.value,
                        itemName: v.label
                    })));
                    const style = props.isSearch ? `style="width: 180px"` : "";
                    return `<a-select vModel={ ${preVModel}${propText} } ${style} placeholder="" allowClear show-search option-filter-prop="children">
                            {this.${listText}.map(v => <a-select-option value={v.id}>{v.itemName}</a-select-option>)}
                        </a-select>`
                case "input":
                    return `<a-input vModel={ ${preVModel}${propText} } />`
                case "textArea":
                    return `<a-textarea vModel={ ${preVModel}${propText} } autoSize={{ minRows: 4 }} style="resize: none;" />`
                case "date":
                    return `<a-date-picker vModel={ ${preVModel}${propText} } />`
                default:
                    return `<div>{ ${preVModel}${propText} }</div>`;
            }
        }
        if(inputMark.length === 1) {
            const inputBundle = inputMark[0].bundle || {};
            const tag = createTag(inputBundle);
            if(inputBundle.type === "select") {
                return `(
                    ${tag}
                )`
            } else {
                return tag;
            }
        } else {
            let tags = inputMark.map(mark => createTag(mark.bundle || {}));
            return `(
            <div style="display: flex;">
                ${tags.join("\n")}
            </div>
          )`
        }
        return ''
    }
        
    formMarkList.forEach(({label, input}) => {
        let labelMark = label.map(i => markList.find(v=>v.index === i) || {});
        let inputMark = input.map(i => markList.find(v=>v.index === i) || {});
        console.log(inputMark)
        formSetting.push(`{
          label: ${handleLabel(labelMark, inputMark)},
          prop: '${handleProp(labelMark, inputMark)}',
          default: ${handleDefault(labelMark, inputMark)},${handleRule(labelMark, inputMark)}
          render: () => ${handleInput(labelMark, inputMark)}
        }`)
    })
    const importText = Object.keys(importPacks).map(p => `import { ${importPacks[p].join(", ")} } from '${p}'`).join("\n");
    const varText = Object.keys(vars).map(v => `this.${v} = ${vars[v]}`).join("\n");
    const settingText = formSetting.join(", ");
    codeContent.value = `${importText}\n\n${varText}\n\n${settingText}`;
}

function handleRemoveTag(index, next) {
    const mark = markList.find(n => n.index === index) || {};
    mark.style.backgroundColor = "black";
    next();
}

function handleAdd(index) {
    formMarkList.push(formMarkInitItem());
}

function handleRemove(index) {
    formMarkList.splice(index, 1);
    let pos = current.value.split("-");
    let i = Number(pos[0]);
    if(formMarkList.length - 1 < i) {
        current.value = `${formMarkList.length - 1}-input`;
    }
}

function handleReset() {
    if(formMarkList.length <= 1) return;
    let pos = formMarkList.length - 1;
    while(pos >= 0 && formMarkList.length > 1) {
        let cur = formMarkList[pos];
        if(!cur.label.length && !cur.input.length) {
            formMarkList.splice(pos, 1);
        }
        pos--;
    }
}

function handleClear() {
    // formMarkList.splice(0, Infinity, formMarkInitItem());
}

function handleCopy() {
    codeInputRef.value.select();
    document.execCommand('copy');
}

defineExpose({
    add,
    computeCode
})
</script>

<template>
    <a-row v-show="!codeMode">
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
    <div v-show="!codeMode" style="max-height: 300px; overflow: auto;">
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
    <div v-show="codeMode">
        <a-button type="primary" size="small" @click="handleCopy">复制</a-button>
        <textarea ref="codeInputRef" :value="codeContent" rows="10" style="border: none; width: 100%;" />
    </div>
</template>

<style lang="less" scoped>
.active {
    :deep(.ant-select-selector) {
        border-color: #40a9ff;
        box-shadow: 0 0 0 2px rgb(24 144 255 / 20%);
        border-right-width: 1px !important;
        outline: 0;
    }
}
</style>