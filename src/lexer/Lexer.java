package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import common.State;
import common.TokenType;

public class Lexer{
    private static TokenType currentToken;
    private static State state = State.ST;

    private static List<String> faultList = new ArrayList<>(); // 错误表
    private static StringBuilder result = new StringBuilder(); // 扫描之后的结果
    private static List<String> tokenList = new ArrayList<>(); // 字符表
    private static List<String> symbolList = new ArrayList<>();// 标识符表

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
                        currentToken = TokenType.IDENTIFIER;
                    } else if (isDigit(c)) {
                        state = State.FY;
                        currentToken = TokenType.DECIMAL;
                    } else {
                        switch (c) {
                            case '+':
                                currentToken = TokenType.PLUS;
                                state = State.PL;
                                break;
                            case '-':
                                currentToken = TokenType.MINUS;
                                state = State.MI;
                                break;
                            case '=':
                                currentToken = TokenType.EQUAL;
                                state = State.ET;
                                break;
                            case ':':
                                currentToken = TokenType.COLON;
                                state = State.TW;
                                break;
                            case '{':
                                currentToken = TokenType.LBRACE;
                                state = State.DONE;
                                break;
                            case '}':
                                currentToken = TokenType.RBRACE;
                                state = State.DONE;
                                break;
                            case ';':
                                currentToken = TokenType.SEMI;
                                state = State.DONE;
                                break;
                            case ' ':
                                currentToken = TokenType.SPACE;
                                state = State.ST;
                                break;
                            case '\t':
                                currentToken = TokenType.TABLE;
                                state = State.ST;
                                break;
                            case '\n':
                                currentToken = TokenType.ENTER;
                                state = State.ST;
                                break;
                            default:
                                state = State.ST;
                                faultList.add(lineNum + ":" + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'");
                                tokenList.add("<" + c + "," + TokenType.ERROR + ">");
                        } // End switch c
                    }
                    break;
                case IL:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = State.IL;
                        currentToken = TokenType.IDENTIFIER;
                    } else if (c == '_') {
                        state = State.UN;
                        currentToken = TokenType.IDENTIFIER;
                    } else state = State.DONE;
                    break;
                case UN:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = State.IL;
                        currentToken = TokenType.IDENTIFIER;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", there should be letter or digit.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case PL:
                    if (c == '=') {
                        state = State.EQ;
                        currentToken = TokenType.PLUS;
                    } else if (isDigit(c)) {
                        state = State.FY;
                        currentToken = TokenType.DECIMAL;
                    } else {
                        state = State.ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + source.charAt(endCol - 1) + "'" + ", only approve +=> or +num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case EQ:
                    if (c == '>') {
                        state = State.DONE;
                        currentToken = TokenType.PLUSEQUALTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve +=>.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case FY:
                    if (isDigit(c)) {
                        state = State.FY;
                        currentToken = TokenType.DECIMAL;
                    } else if (c == '.') {
                        state = State.DO;
                        currentToken = TokenType.DECIMAL;
                    } else {
                        state = State.ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve . or digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case DO:
                    if (isDigit(c)) {
                        state = State.AG;
                        currentToken = TokenType.DECIMAL;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case AG:
                    if (isDigit(c)) {
                        state = State.AG;
                        currentToken = TokenType.DECIMAL;
                    } else state = State.DONE;
                    break;
                case MI:
                    if (isDigit(c)) {
                        state = State.FY;
                        currentToken = TokenType.DECIMAL;
                    } else if (c == '>') {
                        state = State.DONE;
                        currentToken = TokenType.MINUSTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve -> or -num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case ET:
                    if (c == '>') {
                        state = State.DONE;
                        currentToken = TokenType.EQUALTO;
                    } else {
                        state = State.ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve =>");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + TokenType.ERROR + ">");
                    }
                    break;
                case TW:
                    if (c == ':') {
                        state = State.DONE;
                        currentToken = TokenType.DOUBLECOLON;
                    } else {
                        state = State.DONE;
                        currentToken = TokenType.COLON;
                    }
                    break;
                case DONE:
                    break;
                default:
                    break;
            } // End switch state

            if (state == State.DONE) {
                // 实数或者标识符
                if (currentToken == TokenType.DECIMAL || currentToken == TokenType.IDENTIFIER) {
                    String tmp = source.substring(beginCol, endCol);
                    // 判断是否为关键字
                    tokenList.add("<" + tmp + "," + (getKeyword(tmp) == TokenType.NULL ? currentToken : getKeyword(tmp)) + ">");
                    if (getKeyword(tmp) == TokenType.NULL && currentToken == TokenType.IDENTIFIER) {
                        if (!symbolList.contains(tmp)) {
                            symbolList.add(tmp);
                        }
                    }
                    result.append(" ").append(tmp);
                    currentToken = TokenType.NULL;
                } else if (currentToken == TokenType.COLON) { // 是否为 ":"
                    tokenList.add("<" + ":" + "," + TokenType.COLON + ">");
                    result.append(":");
                    currentToken = TokenType.NULL;
                } else if (currentToken == TokenType.DOUBLECOLON) { // 是否为 "::"
                    tokenList.add("<" + "::" + "," + TokenType.DOUBLECOLON + ">");
                    result.append("::");
                    currentToken = TokenType.NULL;
                    pointer++;
                } else {
                    if (!Objects.equals(source.substring(beginCol, endCol), "\n")) { // 其他情况
                        // String toAppend = source.substring(beginCol, endCol);
                        tokenList.add("<" + source.substring(beginCol, endCol + 1) + "," + currentToken + ">");
                        result.append(" ").append(source.substring(beginCol, endCol + 1));
                    }
                    pointer++; //指针前移
                    currentToken = TokenType.NULL;
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
    private static TokenType getKeyword(String identifier) {
        TokenType keyword = TokenType.NULL;
        switch (identifier) {
            case "thread":
                keyword = TokenType.THREAD;
                break;
            case "features":
                keyword = TokenType.FEATURES;
                break;
            case "flows":
                keyword = TokenType.FLOWS;
                break;
            case "properties":
                keyword = TokenType.PROPERTIES;
                break;
            case "end":
                keyword = TokenType.END;
                break;
            case "none":
                keyword = TokenType.NONE;
                break;
            case "in":
                keyword = TokenType.IN;
                break;
            case "out":
                keyword = TokenType.OUT;
                break;
            case "data":
                keyword = TokenType.DATA;
                break;
            case "port":
                keyword = TokenType.PORT;
                break;
            case "event":
                keyword = TokenType.EVENT;
                break;
            case "parameter":
                keyword = TokenType.PARAMETER;
                break;
            case "flow":
                keyword = TokenType.FLOW;
                break;
            case "source":
                keyword = TokenType.SOURCE;
                break;
            case "sink":
                keyword = TokenType.SINK;
                break;
            case "path":
                keyword = TokenType.PATH;
                break;
            case "constant":
                keyword = TokenType.CONSTANT;
                break;
            case "access":
                keyword = TokenType.ACCESS;
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
     * 读取文件内容返回
     *
     * @param filename 文件读经
     * @return 返回读取的字符串
     */
    private static String readFile(String filename) {
        StringBuilder source = new StringBuilder();
        try {
            File file = new File(filename);
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(is);
                String line;
                while ((line = br.readLine()) != null) {
                    source.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source.toString();
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
        if (!outputFile.exists())
            outputFile.createNewFile();
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
        if (!errorFile.exists())
            errorFile.createNewFile();
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

        String sourceFilename = "test2.txt";
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

        String source = readFile(sourceFilename);
        int length = source.length();
        // 扫描开始列号
        int beginColumn = 0;
        // 扫描开始行号
        int line = 1;
        // 开始扫描
        for (int i = 0; i < length; i++) {
            if (source.charAt(i) == '\n') {
                scan(source.substring(beginColumn, i) + '\n', line);
                result.append('\n');
                line++;
                beginColumn = i + 1;
            }
        }

        // 输出结果
        output(outputFilename);

        // 输出错误
        if (faultList.size() != 0) {
            errorOutput(errorFilename);
        }
    }

}
