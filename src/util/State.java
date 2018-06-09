package util;

/**
 * 状态常量
 * 用于词法分析有限状态机
 */
public enum State {
    // 开始状态常量
    ST,

    // 标识符状态常量
    UN,
    IL,

    // 浮点数状态常量
    PL,
    EQ,
    FY,
    DO,
    AG,
    MI,
    ET,
    TW,
    DONE
}
