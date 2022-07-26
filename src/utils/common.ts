/**
 * 拷贝引用数据
 * @param data 要拷贝的数据
 * @param deep 拷贝深度，默认深拷贝(Infinity)
 */
export function objectCopy<T extends Record<string, unknown> | Array<any>>(data: T, deep = Infinity): T {
    const temp = Array.isArray(data) ? [] : {};
    const handler = (target: any, source: any, pos = 1) => {
        for (const entity of Object.entries(source)) {
            if (typeof entity[1] === "object" && pos < deep) {
                target[entity[0]] = Array.isArray(entity[1]) ? [] : {};
                taskQueue.push(handler.bind(null, target[entity[0]], entity[1], pos + 1));
            } else {
                target[entity[0]] = entity[1];
            }
        }
    }
    const taskQueue: Array<() => void> = [handler.bind(null, temp, data)];
    for (const task of taskQueue) {
        task();
    }
    return temp as T;
}