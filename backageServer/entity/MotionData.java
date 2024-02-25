package backageServer.entity;

/**
 * 触摸手势数据实体
 */
public class MotionData {
    public final int action; // 触摸动作
    public final int actionIndex; // 当前激活的指针下标
    public final int pointerCount; // 指针数量
    public final Pointer[] pointers; // 指针集合

    public MotionData(int action, int actionIndex, int pointerCount, Pointer[] pointers) {
        this.action = action;
        this.actionIndex = actionIndex;
        this.pointerCount = pointerCount;
        this.pointers = pointers;
    }
}
