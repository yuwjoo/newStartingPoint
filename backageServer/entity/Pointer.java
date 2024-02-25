package backageServer.entity;

/**
 * 单个指针实体
 */
public class Pointer {
    public final int id; // 触摸点id
    public final float x; // x位置
    public final float y; // y位置
    public final float pressure; // 按压力度

    public Pointer(int id, float x, float y, float pressure) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
}
