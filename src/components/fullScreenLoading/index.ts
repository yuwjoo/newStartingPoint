import { createApp } from "vue"
import loading from "./loading.vue"

const instance = createApp(loading);
const vm: any = instance.mount(document.createElement("div"));

const manage = {
    // 最小持续时间，防止一闪而过
    minDurationTime: 300,
    // 开始显示时间
    startShowTime: 0,
    // 中止上一次还未执行隐藏动作
    abortHideFun: <(() => void) | null>null,
    show() {
        this.abort();
        this.startShowTime = Date.now();
        document.body.appendChild(vm.$el);
    },
    hide() {
        if (!vm.$el.parentElement) {
            return Promise.resolve();
        }
        const duration = Date.now() - this.startShowTime;
        const handler = (resolve: (value: void | PromiseLike<void>) => void) => {
            document.body.removeChild(vm.$el);
            resolve();
        }
        return new Promise<void>((resolve, reject) => {
            if (duration >= this.minDurationTime) {
                handler(resolve);
            } else {
                // 持续时间小于设置的时间，用计时器延迟关闭
                const timear = setTimeout(() => {
                    handler(resolve);
                    // 计时器已经执行完成，中止方法没有存在的必要
                    this.abortHideFun = null;
                }, this.minDurationTime - duration)
                // 对外抛出中止方法
                this.abortHideFun = () => {
                    clearTimeout(timear);
                    resolve();
                }
            }
        })
    },
    abort() {
        if (this.abortHideFun) {
            this.abortHideFun();
            this.abortHideFun = null;
        }
    }
};

export const useLoading = () => manage