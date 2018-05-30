package lexer;

/**
 * 规定状态
 */
public class State {
    // 状态常量
    // 开始状态常量
    public static final int ST = 0;

    // 标识符状态常量
    public static final int UN = 1;
    public static final int IL = 2;

    // 浮点数状态常量
    public static final int PL = 3;
    public static final int EQ = 4;
    public static final int FY = 5;
    public static final int DO = 6;
    public static final int AG = 7;
    public static final int MI = 8;
    public static final int TW = 9;
    public static final int DONE = 10;

    //当前字符属性
    public static final int PLUS = 11;
    public static final int MINUS = 12;
    public static final int EQUAL = 13;
    public static final int LBRACE = 14;
    public static final int RBRACE = 15;
    public static final int COLON = 16;
    public static final int SEMI = 17;
    public static final int SPACE = 18;
    public static final int TABLE = 19;
    public static final int ENTER = 20;

    public static final int DOUBLECOLON = 21;
    public static final int WORD = 22;
    public static final int NUM = 23;
    public static final int PLUSEQUALBIG = 24;
    public static final int MINUSBIG = 25;
    public static final int EQUALBIG = 26;

    //关键字
    public static final int THREAD = 27;
    public static final int FEATURES = 28;
    public static final int FLOWS = 29;
    public static final int PROPERTIES = 30;
    public static final int END = 31;
    public static final int NONE = 32;
    public static final int IN = 33;
    public static final int OUT = 34;
    public static final int DATA = 35;
    public static final int PORT = 36;
    public static final int EVENT = 37;
    public static final int PARAMETER = 38;
    public static final int FLOW = 39;
    public static final int SOURCE = 40;
    public static final int SINK = 41;
    public static final int PATH = 42;
    public static final int CONSTANT = 43;
    public static final int ACCESS = 44;

    public static final int ERROR = -1;
}
