package util;

/**
 * 字符类型
 */
public enum Token {
    // 专用符号
    PLUS,           //+
    MINUS,          //-
    EQUAL,          //=
    LBRACE,         //{
    RBRACE,         //}
    COLON,          //:
    SEMI,           //;
    SPACE,          //
    TABLE,          //
    ENTER,          //
    DOUBLECOLON,    //::
    PLUSEQUALTO,    //+=>
    MINUSTO,        //->
    EQUALTO,        //=>

    ERROR,
    ENDFILE,
    NULL,

    //
    IDENTIFIER,     //标识符
    DECIMAL,        //实数

    // 关键字
    THREAD,
    FEATURES,
    FLOWS,
    PROPERTIES,
    END,
    NONE,
    DATA,
    PORT,
    EVENT,
    PARAMETER,
    FLOW,
    SOURCE,
    SINK,
    PATH,
    CONSTANT,
    ACCESS,

    // IOtype
    IN,
    OUT,
    INOUT,

    // portType
    DATAPORT,
    EVENTDATAPORT,
    EVENTPORT
}