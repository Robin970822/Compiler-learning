package lexer;

/**
 * 规定状态
 */
public class State {
    // 状态常量
    // 开始状态常量
    static final int ST = 0;

    // 标识符状态常量
    static final int UN = 1;
    static final int IL = 2;

    // 浮点数状态常量
    static final int PL = 3;
    static final int EQ = 4;
    static final int FY = 5;
    static final int DO = 6;
    static final int AG = 7;
    static final int MI = 8;
    static final int ET = 9;
    static final int TW = 10;
    static final int DONE = 11;

    //当前字符属性
    static final int PLUS = 1;
    static final int MINUS = 2;
    static final int EQUAL = 3;
    static final int LBRACE = 4;
    static final int RBRACE = 5;
    static final int COLON = 6;
    static final int SEMI = 7;
    static final int SPACE = 8;
    static final int TABLE = 9;
    static final int ENTER = 10;

    static final int DOUBLECOLON = 11;
    static final int WORD = 12;
    static final int NUM = 13;
    static final int PLUSEQUALTO = 14;
    static final int MINUSTO = 15;
    static final int EQUALTO = 16;

    //关键字
    static final int THREAD = 17;
    static final int FEATURES = 18;
    static final int FLOWS = 19;
    static final int PROPERTIES = 20;
    static final int END = 21;
    static final int NONE = 22;
    static final int IN = 23;
    static final int OUT = 24;
    static final int DATA = 25;
    static final int PORT = 26;
    static final int EVENT = 27;
    static final int PARAMETER = 28;
    static final int FLOW = 29;
    static final int SOURCE = 30;
    static final int SINK = 31;
    static final int PATH = 32;
    static final int CONSTANT = 33;
    static final int ACCESS = 34;

    static final int ERROR = -1;
}
