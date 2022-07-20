import { ref, computed } from "vue";
import { warn } from "./utils"
import Mark from "./Mark"
import markNode from "../modules/mark/index.js"

// 标记的搜集，和node形成关联
export default class MarkManage {
    // 标记列表
    #markList = ref([]);
    // 当前分组
    #currentGrounp = ref("default");
    // 当前下标
    #currentIndex = 0;
    // 所有分组
    #markGrounp = computed(() => {
        let grounp = {};
        for(let mark of this.#markList.value) {
            for(let name of mark.grounps) {
                let values = grounp[name] || [];
                grounp[name] = values.concat([mark]);
            }
        }
        return grounp;
    });

    constructor(nodes) {
        this.appendMark(nodes);
    }

    // 获取标记列表
    get markList() {
        return this.#markList;
    }

    // 获取标记分组
    get markGrounp() {
        return this.#markGrounp;
    }

    // 获取当前分组
    get currentGrounp() {
        return this.#currentGrounp;
    }

    // 当前分组的标记
    currentGrounpMarks = computed(() => this.#markGrounp[this.#currentGrounp.value])

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
                this.#markList.value.push(new Mark({
                    ...bundle,
                    index: MIndex,
                    node: MNode
                }));
            }
        }
    }

    // 删除标记
    deleteMark(indexOption) {
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        for(let i of indexs) {
            let mark = this.#markList.value.splice(this.#markList.value.findIndex(m => m.index == i), 1);
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

    // 设置当前分组
    setCurrentGrounp(grounpName) {
        if(!grounpName) {
            warn("require grounpName");
            return;
        }
        for(let mark of this.currentGrounpMarks || []) {
            mark.isSelect = false;
        }
        for(let mark of this.#markGrounp[grounpName] || []) {
            mark.isSelect = true;
        }
        this.#markList.value = [...this.#markList.value];
        this.#currentGrounp.value = grounpName;
    }

    // 提交标记到分组
    commitGrounp(indexOption, grounpName = this.#currentGrounp.value) {
        if(!indexOption) {
            warn("require indexOption");
            return;
        }
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        let markList = this.#markList.value;
        for(let i of indexs) {
            let pos = markList.findIndex(m => m.index == i);
            if(pos !== -1) {
                markList[pos].isSelect = true;
                markList[pos].appendGrounp(grounpName);
            }
        }
    }

    // 移除分组中的标记
    removeGrounp(indexOption, grounpName = this.#currentGrounp.value) {
        if(!indexOption) {
            warn("require indexOption");
            return;
        }
        let indexs = Array.isArray(indexOption) ? indexOption : [indexOption];
        let markList = this.#markList.value;
        for(let i of indexs) {
            let pos = markList.findIndex(m => m.index === i);
            if(pos !== -1) {
                markList[pos].isSelect = false;
                markList[pos].removeGrounp(grounpName);
            }
        }
        this.#markList.value = [...markList];
    }

    // 清除分组中的标记
    clearGrounp(grounpName = this.#currentGrounp.value) {
        let grounps = this.#markGrounp[grounpName];
        if(!grounps) {
            warn("require grounps");
            return;
        }
        for(let mark of grounps) {
            mark.isSelect = false;
            mark.removeGrounp(grounpName);
        }
        this.#markList.value = [...this.#markList.value];
    }

    // 销毁
    destroyed() { }
}