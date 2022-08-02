// 创建标记时需要的参数
interface MarkOption {
    index: number | string
}

export interface MarkMessage {
    type: string,
    value: any
}
// 返回的标记类型
export type MarkNode = HTMLDivElement & { targetWindow?: Window };

export default function (option: MarkOption): MarkNode {
    let markNode: MarkNode = document.createElement("div");
    markNode.className = "mark-box";
    markNode.innerText = option.index + "";
    markNode.targetWindow = window;
    markNode.onclick = function (ev) {
        ev.stopPropagation();
        let { targetWindow, innerText } = <MarkNode>ev.target;
        targetWindow!.postMessage(<MarkMessage>{
            type: "select",
            value: innerText
        }, "*")
    }
    return markNode;
}