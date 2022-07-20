export default function (option) {
    let markNode = document.createElement("div");
    markNode.className = "mark-box";
    markNode.innerText = option.index;
    markNode.markManage = option.markManage;
    markNode.onclick = function(ev) {
        ev.stopPropagation();
        let { markManage, innerText } = ev.target;
        markManage.selectMark(innerText);
    }
    return markNode;
}