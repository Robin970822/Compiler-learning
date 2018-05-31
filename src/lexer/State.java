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
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int EQUAL = 3;
    public static final int LBRACE = 4;
    public static final int RBRACE = 5;
    public static final int COLON = 6;
    public static final int SEMI = 7;
    public static final int SPACE = 8;
    public static final int TABLE = 9;
    public static final int ENTER = 10;

    public static final int DOUBLECOLON = 11;
    public static final int WORD = 12;
    public static final int NUM = 13;
    public static final int PLUSEQUALBIG = 14;
    public static final int MINUSBIG = 15;
    public static final int EQUALBIG = 16;

    //关键字
    public static final int THREAD = 17;
    public static final int FEATURES = 18;
    public static final int FLOWS = 19;
    public static final int PROPERTIES = 20;
    public static final int END = 21;
    public static final int NONE = 22;
    public static final int IN = 23;
    public static final int OUT = 24;
    public static final int DATA = 25;
    public static final int PORT = 26;
    public static final int EVENT = 27;
    public static final int PARAMETER = 28;
    public static final int FLOW = 29;
    public static final int SOURCE = 30;
    public static final int SINK = 31;
    public static final int PATH = 32;
    public static final int CONSTANT = 33;
    public static final int ACCESS = 34;

    public static final int ERROR = -1;
}
