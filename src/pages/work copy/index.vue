<script setup>
import { ref, reactive, watch, provide, createVNode, render, shallowReactive, readonly, computed } from "vue"
import html from "./origin/Create Authority Record.html?url"
import ControlDialog from "./modules/control-dialog.vue"

const iframe = ref(null);
const markList = reactive([]);
const controlDialogRef = ref(null);

provide("markList", markList);

// 初始化
function init() {
    markIndex = 1;
    markList.splice(0, Infinity);
}

// iframe加载完成
function onIframeLoad() {
    init();
    let iWindow = iframe.value.contentWindow;
    let iDocument = iWindow.document;
    let nodes = iDocument.body.querySelector("#base").querySelectorAll("[id]");
    Array.from(nodes).forEach(n => {
        const findKey = ["_文本", "label", "droplist", "text_field", "text_area", "shape"];
        let findClass = Array.from(n.classList).find(v => findKey.includes(v));
        if(findClass) {
            // 这里找到的只是 findKey 标记过的类型元素
            handlerMark(findClass, n);
        } else {
            // 可能是表格
            let selfChildren = n.children;
            let isTableCell = v => Array.from(v.classList).findIndex(v => v === "table_cell") !== -1;
            let tableCellNodes = Array.from(selfChildren).filter(isTableCell);
            tableCellNodes.forEach(cell => handlerMark("table_cell", cell));
        }
    })
}

// 处理标记点
function handlerMark(type, node) {
    let bundle;
    switch(type) {
        // 表单lebel
        case "_文本":
        case "label":
            let text = node.innerText.trim();
            let required = text.startsWith("* ");
            text = text.replace(/^\*\s|：$/g, "").trim();
            bundle = {
                type: "label",
                required,
                data: text
            }
        break;
        // 下拉框
        case "droplist":
            let options = Array.from(node.querySelector("select").children).map(option => ({
                label: option.innerText,
                value: option.getAttribute("value")
            }))
            bundle = {
                type: "select",
                data: options
            }
        break;
        // 输入框
        case "text_field":
            bundle = {
                type: "input"
            }
        break;
        // 文本域
        case "text_area":
            bundle = {
                type: "textArea"
            }
        break;
        // 表格单元格
        case "table_cell":
            bundle = {
                type: "tableCell",
                data: node.innerText.trim()
            }
        break;
        // 日期
        case "shape":
            bundle = {
                type: "date"
            }
    }
    appendToNode(node, bundle);
}

// 追加标记到指定node
function appendToNode(node, bundle) {
    let markNode = createdMarkNode(bundle);
    markList.push(markNode)
    node.appendChild(markNode);
}

let markIndex = 1;
// 创建一个标记dom
function createdMarkNode(bundle) {
    let container = document.createElement("div");
    container.style.cssText = "width: 20px; height: 20px; cursor: pointer; background-color: black; color: white; text-align: center; line-height: 20px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);";
    container.index = container.innerText = markIndex++;
    container.bundle = bundle;
    container.onclick = function(ev) {
        ev.stopPropagation();
        selectMark(ev.target);
    }
    return container;
}

function selectMark(markNode) {
    controlDialogRef.value.selectMark(markNode);
}
</script>

<template>
    <iframe ref="iframe" src="/api/e-database.html" width="100%" height="540px" frameborder="0" @load="onIframeLoad" />
    <control-dialog ref="controlDialogRef" />
</template>

<style lang="less" scoped>
</style>