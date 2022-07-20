import request from "@/permission/request"

// 获取原型html
export function getHTML() {
    return request({
        url: "work/getHTML",
        method: "get"
    })
}