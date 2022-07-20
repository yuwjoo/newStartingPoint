import { reactive, computed } from "vue";
import { warn } from "./utils"
import Mark from "./Mark"
import markNode from "../modules/mark/index.js"
import mark from "../modules/mark/index.js";

// 标记的搜集，和node形成关联
export default class MarkManage {
    // 标记列表
    #markList = reactive([]);
    // 监听的事件集合
    #events = {}
    // 当前下标
    #currentIndex = 0;

    constructor(nodes) {
        this.appendMark(nodes);
    }

    // 获取标记列表
    get markList() {
        return this.#markList;
    }

    // 查询指定标记
    selectMark(indexOption) {
        if(!indexOption) {
            warn("require indexOption");
            return;
        }
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        let marks = {};
        for(let i of indexs) {
            marks[i] = this.#markList.find(m => m.index == i);
        }
        return marks;
    }

    // 追加标记
    appendMark(nodes) {
        const keyText = ["_文本", "label", "droplist", "text_field", "text_area", "table_cell"];
        Array.prototype.forEach.call(nodes, handleNode.bind(this));
        function handleNode(node) {
            let bundle = null;
            switch(Array.prototype.find.call(node.classList, v => keyText.includes(v))) {
                // 表单lebel
                case "_文本":
                case "label":
                    let text = node.innerText.trim();
                    let required = text.startsWith("* ");
                    text = text.replace(/^\*\s|：$/g, "").trim();
                    bundle = {
                        type: Mark.MARK_LABEL,
                        data: {
                            required,
                            text
                        }
                    }
                break;
                // 下拉框
                case "droplist":
                    let options = Array.from(node.querySelector("select").children).map(option => ({
                        label: option.innerText,
                        value: option.getAttribute("value")
                    }))
                    bundle = {
                        type: Mark.MARK_SELECT,
                        data: options
                    }
                break;
                // 输入框
                case "text_field":
                    bundle = {
                        type: Mark.MARK_INPUT
                    }
                break;
                // 文本域
                case "text_area":
                    bundle = {
                        type: Mark.MARK_TEXTAREA
                    }
                break;
                // 表格单元格
                case "table_cell":
                    bundle = {
                        type: Mark.MARK_TABLECELL,
                        data: node.innerText.trim()
                    }
                break;
            }
            if(bundle) {
                let MIndex = ++this.#currentIndex;
                let MNode = markNode({
                    index: MIndex,
                    markManage: this
                })
                node.appendChild(MNode);
                this.#markList.push(new Mark({
                    ...bundle,
                    index: MIndex,
                    node: MNode
                }));
            }
        }
    }

    // 删除标记
    deleteMark(indexOption) {
        if(!indexOption) {
            warn("require indexOption");
            return;
        }
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        for(let i of indexs) {
            let mark = this.#markList.splice(this.#markList.findIndex(m => m.index == i), 1);
            if(mark) {
                mark.unmounted();
            }
        }
    }

    // 清除标记
    clearMark() {
        let mark = this.#markList.value.pop();
        while(mark) {
            mark.unmounted();
            mark = this.#markList.value.pop();
        }
        this.#currentIndex = 0;
    }

    // 选择标记
    selectMark(indexOption) {
        if(!indexOption) {
            warn("require indexOption");
            return;
        }
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        let marks = {};
        for(let i of indexs) {
            let pos = this.#markList.findIndex(m => m.index == i);
            if(pos !== -1) {
                this.#markList[pos].isSelect = true;
                marks[i] = this.#markList[pos];
            } else {
                marks[i] = null;
            }
        }
        this.emit("select-mark", marks);
    }

    // 销毁
    destroyed() {
        this.#markList = null;
        this.#events = null;
    }

    // 监听事件
    on(eventName, callback) {
        if(!eventName || !callback) {
            warn("eventName and callback are required");
            return;
        }
        this.#events[eventName] = [
            ...(this.#events[eventName] || []),
            callback
        ]
    }

    // 发送事件
    emit(eventName, ...arg) {
        if(!eventName) {
            warn("require eventName");
            return;
        }
        for(let cb of this.#events[eventName] || []) {
            cb(...arg);
        }
    }

    // 停止监听事件(不传callback清空所有回调)
    off(eventName, callback) {
        if(!eventName) {
            warn("require eventName");
            return;
        }
        let queue = this.#events[eventName];
        if(!queue) {
            return;
        }
        if(typeof callback === "function") {
            queue.splice(queue.indexOf(callback), 1);
        } else {
            queue = [];
        }
        this.#events[eventName] = queue;
    }
}