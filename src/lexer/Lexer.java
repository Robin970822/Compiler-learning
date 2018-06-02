package lexer;

import util.State;
import util.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static util.FileReader.readFile;

public class Lexer {
    private static Token currentToken;
    private static State state = State.ST;

    private static StringBuilder result = new StringBuilder(); // 扫描之后的结果

    private static List<String> sourceList = new ArrayList<>(); // 原文表
    private static List<String> faultList = new ArrayList<>();  // 错误表
    private static List<String> tokenList = new ArrayList<>();  // 字符表
    private static List<String> symbolList = new ArrayList<>(); // 标识符表

    /**
     * 扫描一行
     *
     * @param source  扫描内容
     * @param lineNum 扫描行数
     */
    private static void scan(String source, int lineNum) {
        int endCol;
        int beginCol = 0;
        int length = source.length();       // 行长度
        int pointer = 0;                    // 当前扫描列号

        // 开始扫描
        while (pointer < length) {
            // 暂存列号
            endCol = pointer;
            pointer++;
            char c = source.charAt(endCol);
            switch (state) {
                case ST:
                    beginCol = endCol;
                    if (isIdentifierLetter(c)) {
                        state = State.IL;
                        currentToken = Token.IDENTIFIER;
                    } else if (isDigit(c)) {
                        state = State.FY;
                        currentToken = Token.DECIMAL;
                    } else {
                        switch (c) {
                            case '+':
                                currentToken = Token.PLUS;
                                state = State.PL;
                                break;
                            case '-':
                                currentToken = Token.MINUS;
                                state = State.MI;
                                break;
                            case '=':
                                currentToken = Token.EQUAL;
                                state = State.ET;
                                break;
                            case ':':
                                currentToken = Token.COLON;
                                state = State.TW;
                                break;
                            case '{':
                                currentToken = Token.LBRACE;
                                state = State.DONE;
                                break;
                            case '}':
                                currentToken = Token.RBRACE;
                                state = State.DONE;
                                break;
                            case ';':
                                currentToken = Token.SEMI;
                                state = State.DONE;
                                break;
                            case ' ':
                                currentToken = Token.SPACE;
                                state = State.ST;
                                break;
                            case '\t':
                                currentToken = Token.TABLE;
                                state = State.ST;
                                break;
                            case '\n':
                                currentToken = Token.ENTER;
                                state = State.ST;
                                break;
                            default:
                                state = State.ST;
                                faultList.add(lineNum + ":" + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'");
                                tokenList.add("<" + c + "," + Token.ERROR + ">");
                        } // End switch c
                    }
                    break;
                case IL:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = State.IL;
                        currentToken = Token.IDENTIFIER;
                    } else if (c == '_') {
                        state = State.UN;
                        currentToken = Token.IDENTIFIER;
                    } else state = State.DONE;
                    break;
                case UN:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = State.IL;
                        currentToken = Token.IDENTIFIER;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", there should be letter or digit.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case PL:
                    if (c == '=') {
                        state = State.EQ;
                        currentToken = Token.PLUS;
                    } else if (isDigit(c)) {
                        state = State.FY;
                        currentToken = Token.DECIMAL;
                    } else {
                        state = State.ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + source.charAt(endCol - 1) + "'" + ", only approve +=> or +num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case EQ:
                    if (c == '>') {
                        state = State.DONE;
                        currentToken = Token.PLUSEQUALTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve +=>.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case FY:
                    if (isDigit(c)) {
                        state = State.FY;
                        currentToken = Token.DECIMAL;
                    } else if (c == '.') {
                        state = State.DO;
                        currentToken = Token.DECIMAL;
                    } else {
                        state = State.ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve . or digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case DO:
                    if (isDigit(c)) {
                        state = State.AG;
                        currentToken = Token.DECIMAL;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case AG:
                    if (isDigit(c)) {
                        state = State.AG;
                        currentToken = Token.DECIMAL;
                    } else state = State.DONE;
                    break;
                case MI:
                    if (isDigit(c)) {
                        state = State.FY;
                        currentToken = Token.DECIMAL;
                    } else if (c == '>') {
                        state = State.DONE;
                        currentToken = Token.MINUSTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve -> or -num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case ET:
                    if (c == '>') {
                        state = State.DONE;
                        currentToken = Token.EQUALTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve =>");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + Token.ERROR + ">");
                    }
                    break;
                case TW:
                    if (c == ':') {
                        state = State.DONE;
                        currentToken = Token.DOUBLECOLON;
                    } else {
                        state = State.DONE;
                        currentToken = Token.COLON;
                    }
                    break;
                case DONE:
                    break;
                default:
                    break;
            } // End switch state

            if (state == State.DONE) {
                // 实数或者标识符
                if (currentToken == Token.DECIMAL || currentToken == Token.IDENTIFIER) {
                    String tmp = source.substring(beginCol, endCol);
                    // 判断是否为关键字
                    tokenList.add("<" + tmp + "," + (getKeyword(tmp) == Token.NULL ? currentToken : getKeyword(tmp)) + ">");
                    if (getKeyword(tmp) == Token.NULL && currentToken == Token.IDENTIFIER) {
                        if (!symbolList.contains(tmp)) {
                            symbolList.add(tmp);
                        }
                    }
                    result.append(" ").append(tmp);
                    currentToken = Token.NULL;
                } else if (currentToken == Token.COLON) { // 是否为 ":"
                    tokenList.add("<" + ":" + "," + Token.COLON + ">");
                    result.append(":");
                    currentToken = Token.NULL;
                } else if (currentToken == Token.DOUBLECOLON) { // 是否为 "::"
                    tokenList.add("<" + "::" + "," + Token.DOUBLECOLON + ">");
                    result.append("::");
                    currentToken = Token.NULL;
                    pointer++;
                } else {
                    if (!Objects.equals(source.substring(beginCol, endCol), "\n")) { // 其他情况
                        // String toAppend = source.substring(beginCol, endCol);
                        tokenList.add("<" + source.substring(beginCol, endCol + 1) + "," + currentToken + ">");
                        result.append(" ").append(source.substring(beginCol, endCol + 1));
                    }
                    pointer++; //指针前移
                    currentToken = Token.NULL;
                }
                pointer--; // 指针后移
                state = State.ST;
            } // state == DONE end
        }

    }

    /**
     * 判断输入字符串是否是关键字
     *
     * @param identifier 输入字符串
     * @return 关键字的状态常量
     */
    private static Token getKeyword(String identifier) {
        Token keyword = Token.NULL;
        switch (identifier) {
            case "thread":
                keyword = Token.THREAD;
                break;
            case "features":
                keyword = Token.FEATURES;
                break;
            case "flows":
                keyword = Token.FLOWS;
                break;
            case "properties":
                keyword = Token.PROPERTIES;
                break;
            case "end":
                keyword = Token.END;
                break;
            case "none":
                keyword = Token.NONE;
                break;
            case "in":
                keyword = Token.IN;
                break;
            case "out":
                keyword = Token.OUT;
                break;
            case "data":
                keyword = Token.DATA;
                break;
            case "port":
                keyword = Token.PORT;
                break;
            case "event":
                keyword = Token.EVENT;
                break;
            case "parameter":
                keyword = Token.PARAMETER;
                break;
            case "flow":
                keyword = Token.FLOW;
                break;
            case "source":
                keyword = Token.SOURCE;
                break;
            case "sink":
                keyword = Token.SINK;
                break;
            case "path":
                keyword = Token.PATH;
                break;
            case "constant":
                keyword = Token.CONSTANT;
                break;
            case "access":
                keyword = Token.ACCESS;
                break;
        }
        return keyword;
    }

    /**
     * 判断输入字符是否为数字
     *
     * @param c 输入字符
     * @return 是否为数字
     */
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 判断输入字符是否为字母
     *
     * @param c 输入字符
     * @return 是否为字母
     */
    private static boolean isIdentifierLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * 输出结果
     *
     * @param outputFilename 结果输出文件
     * @throws IOException 文件读写异常
     */
    private static void output(String outputFilename) throws IOException {
        // 输出结果
        File outputFile = new File(outputFilename);
        FileOutputStream out = new FileOutputStream(outputFile, false);
        for (String s : tokenList)
            out.write((s + '\n').getBytes("utf-8"));
        out.close();
    }

    /**
     * 输出错误信息
     *
     * @param errorFilename 错误信息输出文件
     * @throws IOException 文件读写异常
     */
    private static void errorOutput(String errorFilename) throws IOException {
        File errorFile = new File(errorFilename);
        FileOutputStream error = new FileOutputStream(errorFile, false);
        for (String s : faultList) {
            String[] errorInfo = s.split("%");
            error.write((errorInfo[0] + "\n").getBytes("utf-8"));
            error.write(("\t\tError:" + errorInfo[2] + "\n").getBytes("utf-8"));
            error.write('\n');
        }
        error.close();
    }

    public static void main(String[] args) throws IOException {

        String sourceFilename = "test.txt";
        String outputFilename = "tokenOut.txt";
        String errorFilename = "errorOut.txt";

        switch (args.length) {
            case 3:
                errorFilename = args[2];
            case 2:
                outputFilename = args[1];
            case 1:
                sourceFilename = args[0];
            case 0:
                break;
            default:
                System.err.println("Please input at most 3 args!");
                return;
        }

        sourceList = readFile(sourceFilename);
        // 扫描开始行号
        int line = 1;
        // 开始扫描
        for (String s : sourceList) {
            scan(s, line);
            line++;
        }
        // 输出结果
        output(outputFilename);

        // 输出错误
        if (faultList.size() != 0) {
            errorOutput(errorFilename);
        }
    }

}
