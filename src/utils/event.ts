import { onUnmounted } from "vue";

/**
 * 为dom监听事件
 * @param target dom对象
 * @param eventName 事件名称
 * @param callback 回调函数
 * @param options 额外参数
 */
export function useEventListener(
  target: EventTarget,
  eventName: string,
  callback: any,
  options?: object
): void {
  target.addEventListener(eventName, callback, options);
  onUnmounted(() => {
    target.removeEventListener(eventName, callback);
  });
}