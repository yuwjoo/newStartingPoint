<template>
    <!-- 头部 -->
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
    <!-- 主体 -->
    <div style="max-height: 300px; overflow: auto;">
        <a-row v-for="(bundle, index) in markBundleList" :key="index" style="margin-top: 5px;">
            <!-- 标记选择区 -->
            <a-col :span="9" :offset="itemIndex" v-for="(item, itemIndex) in ['labels', 'inputs']" :key="itemIndex">
                <a-select :value="(bundle[item] as Array<ReadonlyMark>).map(m => ({ value: m }))" :class="{
                    active: focusItem === bundle[item]
                }" mode="multiple" style="width: 100%;" @click="focusItem = (bundle[item] as Array<ReadonlyMark>)">
                    <template #tagRender="{ value, closable, onClose }">
                        <a-tag :closable="closable" style="margin-right: 3px"
                            @close="handleRemoveTag(index, item, value, onClose)">
                            {{ value.index }}
                        </a-tag>
                    </template>
                </a-select>
            </a-col>
            <!-- 操作区 -->
            <a-col :span="5">
                <a-button type="primary" style="margin: 0 5px;" @click="handleAdd(index)">
                    <template #icon>
                        <plus-outlined />
                    </template>
                </a-button>
                <a-button type="danger" @click="handleRemove(index)">
                    <template #icon>
                        <minus-outlined />
                    </template>
                </a-button>
            </a-col>
        </a-row>
    </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue';
import useManage from "@/pages/work/hooks/manage"
import { PlusOutlined, MinusOutlined, SyncOutlined, DeleteOutlined } from "@ant-design/icons-vue"
import type { ReadonlyMark } from "@/pages/work/hooks/manage"

interface MarkBundle {
    [key: string]: unknown,
    labels: Array<ReadonlyMark>,
    inputs: Array<ReadonlyMark>
}

const props = defineProps({
    // 是筛选条件
    isSearch: {
        type: Boolean
    }
})

const manage = useManage();
// 标记包列表
const markBundleList = ref<Array<MarkBundle>>([createMarkBundle()]);
// 当前焦点项
const focusItem = ref<Array<ReadonlyMark> | null>(markBundleList.value[0].labels);
// 选择的标记列表
const selectMarkList = computed(() => markBundleList.value.reduce((pre, cur) => [...pre, ...cur.labels, ...cur.inputs], [] as Array<ReadonlyMark>))

// 生成空的markBundle
function createMarkBundle() {
    return {
        labels: [],
        inputs: []
    }
}

// 动态计算焦点项
function autoFocus() {
    if (markBundleList.value.length) {
        if (!markBundleList.value.some(b => b.labels === focusItem.value || b.inputs === focusItem.value)) {
            const lastItem = markBundleList.value.slice(-1)[0];
            focusItem.value = lastItem.labels.length ? lastItem.inputs : lastItem.labels;
        }
    } else {
        focusItem.value = null;
    }
}

// 添加标记
function addMark(mark: ReadonlyMark) {
    if (selectMarkList.value.some(m => m.index === mark.index)) {
        return;
    }
    if (!focusItem.value) {
        return;
    }
    focusItem.value!.push(mark);
    markBundleList.value = [...markBundleList.value];
    let key = '';
    const pos = markBundleList.value.findIndex(b => {
        if (b.labels === focusItem.value) {
            key = 'labels';
        } else if (b.inputs === focusItem.value) {
            key = 'inputs';
        }
        return !!key;
    });
    if (key === "labels") {
        focusItem.value = markBundleList.value[pos]['inputs'];
    } else if (pos < markBundleList.value.length - 1) {
        focusItem.value = markBundleList.value[pos + 1]['labels'];
    } else {
        markBundleList.value.push(createMarkBundle());
        focusItem.value = markBundleList.value.slice(-1)[0]['labels'];
    }
}
// 重置标记
function handleReset() {
    manage.selectMarks(selectMarkList.value.map(m => m.index), false);
    markBundleList.value = [createMarkBundle()];
    autoFocus();
}
// 清除标记
function handleClear() {
    manage.selectMarks(selectMarkList.value.map(m => m.index), false);
    markBundleList.value = [createMarkBundle()];
    autoFocus();
}
// 删除指定标记
function handleRemoveTag(index: number, key: string, mark: ReadonlyMark, next: any) {
    markBundleList.value.splice(index, 1, {
        ...markBundleList.value[index],
        [key]: (markBundleList.value[index][key] as Array<ReadonlyMark>).filter(m => m !== mark)
    })
    manage.selectMark(mark.index, false);
    next();
    autoFocus();
}
// 指定位置增加空位
function handleAdd(index: number) {
    markBundleList.value.splice(index + 1, 0, createMarkBundle());
}
// 删除当前位置
function handleRemove(index: number) {
    const bundle = markBundleList.value[index];
    manage.selectMarks([...bundle.labels, ...bundle.inputs].map(m => m.index), false)
    markBundleList.value.splice(index, 1);
    autoFocus();
}

function computeCode(): string {
    const importPacks: Record<string, Array<string>> = {};
    const vars: Record<string, any> = {};
    const formSetting: Array<string> = [];

    const handleLabel = (labelMark: Array<ReadonlyMark>, inputMark: Array<ReadonlyMark>) => {
        if (labelMark.length === 1) {
            return `'${labelMark[0].data.text}'`;
        }
        return `''`;
    }
    const handleProp = (labelMark: Array<ReadonlyMark>, inputMark: Array<ReadonlyMark>) => {
        if (labelMark.length !== 1) return '';
        let text = (labelMark[0].data.text || "").replace(/\W/g, '');
        text = text.charAt(0).toLocaleLowerCase() + text.slice(1);
        return text
    }
    const handleDefault = (labelMark: Array<ReadonlyMark>, inputMark: Array<ReadonlyMark>) => {
        if (inputMark.length !== 1) return `''`;
        switch (inputMark[0].type) {
            default:
                return `''`;
        }
    }
    const handleRule = (labelMark: Array<ReadonlyMark>, inputMark: Array<ReadonlyMark>) => {
        if (props.isSearch || labelMark.length !== 1) {
            return "";
        }
        let labelData = labelMark[0].data || {};
        let inputData = inputMark[0].data || {};
        let required, limitLength;
        if (labelData.required) {
            required = `{ required: true, message: 'Please ${inputData.type === manage.typeEnum.MARK_SELECT ? 'select' : 'input'} your ${labelData.text}!' }`
        }
        if (inputData.type === manage.typeEnum.MARK_INPUT) {
            const importValue = importPacks["@/utils/helper"] || [];
            if (importValue.indexOf("isLegalName100Validator") === -1) {
                importValue.push("isLegalName100Validator");
                importPacks["@/utils/helper"] = importValue;
            }
            limitLength = `{ validator: isLegalName100Validator, trigger: 'blur' }`
        }
        if (inputData.type === manage.typeEnum.MARK_TEXTAREA) {
            const importValue = importPacks["@/utils/helper"] || [];
            if (importValue.indexOf("isLegalName500Validator") === -1) {
                importValue.push("isLegalName500Validator");
                importPacks["@/utils/helper"] = importValue;
            }
            limitLength = `{ validator: isLegalName500Validator, trigger: 'blur' }`
        }
        if (!required && !limitLength) return "";
        return `
          rule: [
              ${[required, limitLength].filter(v => v).join(",\n")}
          ],`
    }
    const handleInput = (labelMark: Array<ReadonlyMark>, inputMark: Array<ReadonlyMark>) => {
        const propText = handleProp(labelMark, inputMark);
        const preVModel = props.isSearch ? "this.search.form.model." : "this.form.model.";
        let createTag = (mark: ReadonlyMark) => {
            switch (mark.type) {
                case manage.typeEnum.MARK_SELECT:
                    const listText = `${propText}List`;
                    vars[listText] = JSON.stringify(mark.data.map((v: Record<string, unknown>) => ({
                        id: v.value,
                        itemName: v.label
                    })));
                    const style = props.isSearch ? `style="width: 180px"` : "";
                    return `<a-select vModel={ ${preVModel}${propText} } ${style} placeholder="" allowClear show-search option-filter-prop="children">
                            {this.${listText}.map(v => <a-select-option value={v.id}>{v.itemName}</a-select-option>)}
                        </a-select>`
                case manage.typeEnum.MARK_INPUT:
                    return `<a-input vModel={ ${preVModel}${propText} } />`
                case manage.typeEnum.MARK_TEXTAREA:
                    return `<a-textarea vModel={ ${preVModel}${propText} } autoSize={{ minRows: 4 }} style="resize: none;" />`
                case manage.typeEnum.MARK_DATE:
                    return `<a-date-picker vModel={ ${preVModel}${propText} } />`
                default:
                    return `<div>{ ${preVModel}${propText} }</div>`;
            }
        }
        if (inputMark.length === 1) {
            const tag = createTag(inputMark[0]);
            if (inputMark[0].type === manage.typeEnum.MARK_SELECT) {
                return `(
                    ${tag}
                )`
            } else {
                return tag;
            }
        } else {
            let tags = inputMark.map(mark => createTag(mark));
            return `(
            <div style="display: flex;">
                ${tags.join("\n")}
            </div>
          )`
        }
    }

    markBundleList.value.forEach(({ labels, inputs }) => {
        let labelMark = labels;
        let inputMark = inputs;
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
    return `${importText}\n\n${varText}\n\n${settingText}`;
}

defineExpose({
    addMark,
    getCode: computeCode
})
</script>

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