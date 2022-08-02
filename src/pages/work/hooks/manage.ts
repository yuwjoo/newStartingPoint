import { ref, computed } from "vue"
import mark from "../modules/mark/index"
import markNode, { MarkNode } from "../modules/mark/index"

export interface MarkInstance {
    // 标记dom
    _node: MarkNode,
    // 标记类型
    type: string,
    // 标记下标
    index: number,
    // 标记数据
    data?: any,
    // 是否选中
    isSelect: boolean,
    // 是否挂载（否：该标记将失去意义，可以直接抛弃）
    isMounted: boolean,
    // 卸载标记
    unmounted: () => void
}

export interface ReadonlyMark {
    // 标记类型
    type: string,
    // 标记下标
    index: number,
    // 标记数据
    data?: any,
    // 是否选中
    isSelect: boolean
}

// 生成标记产生的信息包
export interface Bundle {
    type: Type,
    data?: any
}

// 标记对象
class Mark implements MarkInstance {
    _node: MarkNode;
    type: string;
    data: any;
    _isSelect: boolean;
    isMounted: boolean;

    get index(): number {
        return Number(this._node.innerText)
    }

    get isSelect(): boolean {
        return this._isSelect;
    }

    set isSelect(is) {
        if (is) {
            this._node.classList.add("select");
        } else {
            this._node.classList.remove("select");
        }
        this._isSelect = is;
    }

    constructor(bundle: Bundle, node: MarkNode) {
        this._node = node;
        this.type = bundle.type;
        this.data = bundle.data;
        this._isSelect = false;
        this.isMounted = true;
    }

    /**
     * 卸载标记
     */
    unmounted() {
        this._node.parentElement!.removeChild(this._node);
        this.isMounted = false;
    };
}

// 标记类型
enum Type {
    MARK_OTHER = "MARK_OTHER", // 其他
    MARK_INPUT = "MARK_INPUT", // 输入框
    MARK_SELECT = "MARK_SELECT", // 下拉选择
    MARK_NUMBER = "MARK_NUMBER", // 数字输入
    MARK_LABEL = "MARK_LABEL", // 标签
    MARK_TEXTAREA = "MARK_TEXTAREA", // 文本域
    MARK_RADIO = "MARK_RADIO", // 单选
    MARK_CHECKBOX = "MARK_CHECKBOX", // 多选
    MARK_BUTTON = "MARK_BUTTON", // 按钮
    MARK_DATE = "MARK_DATE", // 日期
    MARK_DATERANGE = "MARK_DATERANGE", // 日期范围
    MARK_TABLECELL = "MARK_TABLECELL" // 表格单元格
}

// 标记下标
let currentIndex = 0;

// 标记列表
const markList = ref<Array<Mark>>([]);

// 只读标记列表
const readonlyMarkList = computed(() => markList.value.map(m => (<ReadonlyMark>{
    type: m.type,
    index: m.index,
    data: m.data,
    isSelect: m.isSelect
})));

/**
 * 生成标记
 * @param nodes 需要添加标记的节点
 */
function generate(nodes: Array<Element & HTMLElement>) {
    const keyText = ["_文本", "label", "droplist", "text_field", "text_area", "table_cell"];
    Array.prototype.forEach.call(nodes, handleNode.bind(null));
    function handleNode(node: Element & HTMLElement) {
        let bundle: Bundle | null = null;
        switch (Array.prototype.find.call(node.classList, v => keyText.includes(v))) {
            // 表单lebel
            case "_文本":
            case "label":
                let text = node.innerText.trim();
                let required = text.startsWith("* ");
                text = text.replace(/^\*\s|：$/g, "").trim();
                bundle = {
                    type: Type.MARK_LABEL,
                    data: {
                        required,
                        text
                    }
                }
                break;
            // 下拉框
            case "droplist":
                let options = Array.from(node.querySelector("select")!.children).map((option: any) => ({
                    label: option.innerText,
                    value: option.getAttribute("value")
                }))
                bundle = {
                    type: Type.MARK_SELECT,
                    data: options
                }
                break;
            // 输入框
            case "text_field":
                bundle = {
                    type: Type.MARK_INPUT
                }
                break;
            // 文本域
            case "text_area":
                bundle = {
                    type: Type.MARK_TEXTAREA
                }
                break;
            // 表格单元格
            case "table_cell":
                bundle = {
                    type: Type.MARK_TABLECELL,
                    data: node.innerText.trim()
                }
                break;
        }
        if (bundle) {
            let MNode = markNode({
                index: ++currentIndex
            })
            node.appendChild(MNode);
            markList.value = markList.value.concat([new Mark(bundle, MNode)]);
        }
    }
}

/**
 * 获取标记对象
 * @param index 标记下标
 */
function findMark(index: number): ReadonlyMark | undefined {
    return readonlyMarkList.value.find(m => m.index === index);
}

/**
 * 删除标记
 * @param index 标记下标
 */
function deleteMark(index: number) {
    const start = markList.value.findIndex(m => m.index === index);
    if (start !== -1) {
        const mark = markList.value.splice(start, 1);
        mark[0].unmounted();
    }
}

/**
 * 改变标记选中状态
 * @param index 标记下标
 * @param is 是否选中
 */
function selectMark(index: number, is: boolean): ReadonlyMark {
    const pos = markList.value.findIndex(m => m.index === index);
    if (pos !== -1) {
        markList.value[pos].isSelect = is;
    }
    return readonlyMarkList.value[pos];
}

/**
 * 批量改变标记选中状态
 * @param index 标记下标集合
 * @param is 是否选中
 */
function selectMarks(indexs: Array<number>, is: boolean): Array<ReadonlyMark> {
    const list: Array<ReadonlyMark> = [];
    const tempIndexs: Array<number> = [...indexs];
    for (let mark of markList.value) {
        let findPos = tempIndexs.indexOf(mark.index);
        if (findPos !== -1) {
            tempIndexs.splice(findPos, 1);
            mark.isSelect = is;
            list.push(mark);
        }
        if (tempIndexs[0] === undefined) {
            break;
        }
    }
    return list;
}

function useManage() {
    return {
        // 标记列表
        markList: readonlyMarkList,
        // 最后的标记下标
        lastIndex: currentIndex,
        // 类型枚举
        typeEnum: Type,
        // 生成标记
        generate,
        // 获取标记对象
        findMark,
        // 删除标记
        deleteMark,
        // 改变标记选中状态
        selectMark,
        // 批量改变标记选中状态
        selectMarks
    }
}

export default useManage