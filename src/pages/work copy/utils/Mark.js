import { warn } from "./utils"

// 标记类
export default class Mark {
    static MARK_OTHER = -1; // 其他
    static MARK_INPUT = 0; // 输入框
    static MARK_SELECT = 1; // 下拉选择
    static MARK_NUMBER = 2; // 数字输入
    static MARK_LABEL = 3; // 标签
    static MARK_TEXTAREA = 4; // 文本域
    static MARK_RADIO = 5; // 单选
    static MARK_CHECKBOX = 6; // 多选
    static MARK_BUTTON = 7; // 按钮
    static MARK_DATE = 8; // 日期
    static MARK_DATERANGE = 9; // 日期范围
    static MARK_TABLECELL = 10; // 表格单元格

    constructor(option) {
        // 标记序号
        this.index = option.index;
        // 标记类型
        this.type = option.type || Mark.MARK_OTHER;
        // 是否被选择
        this.isSelect = option.isSelect || false;
        // 所属分组列表
        this.grounps = new Set(option.grounps || []);
        // 标记node节点
        this.node = option.node || null;
        // 标记数据
        this.data = option.data;
        // 是否已删除
        this.isDelete = false;
    }

    get isSelect() {
        return this._isSelect;
    }
    set isSelect(val) {
        if(!this.node) return;
        if(val) {
            this.node.classList.add("select");
        } else {
            this.node.classList.remove("select");
        }
        this._isSelect = val;
    }

    get grounpList() {
        return Array.from(this.grounps);
    }

    // 追加分组
    appendGrounp(grounpName) {
        if(!grounpName) {
            warn("require grounpName!");
            return;
        }
        let grounpList = Array.isArray(grounpName) ? grounpName : [grounpName];
        grounpList.forEach(this.grounps.add.bind(this.grounps));
    }

    // 移除分组
    removeGrounp(grounpName) {
        if(!grounpName) {
            warn("require grounpName");
            return;
        }
        let grounpList = Array.isArray(grounpName) ? grounpName : [grounpName];
        grounpList.forEach(this.grounps.remove.bind(this.grounps));
    }

    // 清除分组
    clearGrounp() {
        this.grounps.clear();
    }

    // 卸载标记
    unmounted() {
        if(!this.node) return;
        this.node.parentElement.removeChild(this.node);
        this.node = null;
        this.isDelete = true;
    }
}