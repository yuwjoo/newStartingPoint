import { onUnmounted } from "vue";

/**
 * 为dom监听事件
 * @param target dom对象
 * @param eventName 事件名称
 * @param listener 监听函数
 * @param options 额外参数
 */
export function useEventListener(
  target: EventTarget,
  eventName: string,
  listener: any,
  options?: object
): void {
  target.addEventListener(eventName, listener, options);
  onUnmounted(() => {
    target.removeEventListener(eventName, listener);
  });
}