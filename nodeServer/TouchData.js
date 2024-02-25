class TouchData {
  dataView = null;

  /**
   * @description: 构造函数
   * @param {number} id 触摸点id
   * @param {number} action 触摸动作：0：按下, 1：抬起，2：移动
   * @param {number} x 触摸x轴
   * @param {number} y 触摸y轴
   * @param {number} pressure 触摸压力
   */
  constructor(id, action, x, y, pressure) {
    this.dataView = new DataView(new ArrayBuffer(24));
    this.dataView.setInt32(0, 200); // 触摸起始标识
    this.dataView.setInt32(4, id);
    this.dataView.setInt32(8, action);
    this.dataView.setFloat32(12, x);
    this.dataView.setFloat32(16, y);
    this.dataView.setFloat32(20, pressure);
  }

  /**
   * @description: 获取 uint8Array 数据
   * @return {uint8Array} uint8Array 数据
   */
  getUint8Array() {
    return new Uint8Array(this.dataView.buffer);
  }
}

module.exports = TouchData;
