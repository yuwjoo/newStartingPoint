/**
 * 拷贝引用数据
 * @param data 要拷贝的数据
 * @param deep 拷贝深度，默认深拷贝(Infinity)
 */
export function objectCopy<T extends Record<string, unknown> | Array<any>>(data: T, deep = Infinity): T {
    // 保存引用对象，解决循环引用问题
    const objMap = new Map();
    const taskQueue: Array<() => void> = [];
    function handler(target: any, source: any, pos: number) {
        // Object.entries只会获取属于自身对象的属性
        for (const [key, val] of Object.entries(source)) {
            if (typeof val === "object") {
                if (objMap.has(val)) {
                    // 如果发现有循环引用的对象，直接取map里的值
                    target[key] = objMap.get(val);
                } else if (pos < deep) {
                    target[key] = addTask(val, pos);
                } else {
                    target[key] = val;
                }
            } else {
                target[key] = val;
            }
        }
    }
    function addTask(oldVal: any, curPos: number) {
        const newVal = Array.isArray(oldVal) ? [] : {};
        objMap.set(oldVal, newVal);
        taskQueue.push(handler.bind(null, newVal, oldVal, curPos + 1));
        return newVal;
    }
    const temp = addTask(data, 1);
    for (const task of taskQueue) {
        task();
    }
    objMap.clear();
    return temp as T;
}