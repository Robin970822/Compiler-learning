package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer extends State {
    public static int currentToken = 0;
    public static boolean save = false;
    public static int state = ST;

    public static List<String> faultList = new ArrayList<String>(); // 错误表
    public static StringBuilder result = new StringBuilder();       // 扫描之后的结果
    public static List<String> tokenList = new ArrayList<String>(); // 字符表
    public static List<String> symbolList = new ArrayList<String>();// 标识符表

    /**
     * 扫描一行
     *
     * @param source  扫描内容
     * @param lineNum 扫描行数
     */
    private static void scan(String source, int lineNum) {
        int endCol = 0;
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
                        state = IL;
                        currentToken = WORD;
                    } else if (isDigit(c)) {
                        state = FY;
                        currentToken = NUM;
                    } else {
                        switch (c) {
                            case '+':
                                currentToken = PLUS;
                                state = PL;
                                break;
                            case '-':
                                currentToken = MINUS;
                                state = MI;
                                break;
                            case '=':
                                currentToken = EQUAL;
                                state = MI;
                                break;
                            case ':':
                                currentToken = COLON;
                                state = TW;
                                break;
                            case '{':
                                currentToken = LBRACE;
                                state = DONE;
                                break;
                            case '}':
                                currentToken = RBRACE;
                                state = DONE;
                                break;
                            case ';':
                                currentToken = SEMI;
                                state = DONE;
                                break;
                            case ' ':
                                currentToken = SPACE;
                                state = ST;
                                break;
                            case '\t':
                                currentToken = TABLE;
                                state = ST;
                                break;
                            case '\n':
                                currentToken = ENTER;
                                state = ST;
                                break;
                            default:
                                state = ST;
                                faultList.add(lineNum + ":" + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'");
                                tokenList.add("<" + c + "," + ERROR + ">");
                        } // End switch c
                    }
                    break;
                case IL:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = IL;
                        currentToken = WORD;
                    } else if (c == '_') {
                        state = UN;
                        currentToken = WORD;
                    } else state = DONE;
                    break;
                case UN:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = IL;
                        currentToken = WORD;
                    } else {
                        state = ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", there should be letter or digit.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case PL:
                    if (c == '=') {
                        state = EQ;
                        currentToken = PLUS;
                    } else if (isDigit(c)) {
                        state = FY;
                        currentToken = NUM;
                    } else {
                        state = ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + source.charAt(endCol - 1) + "'" + ", only approve +=> or +num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case EQ:
                    if (c == '>') {
                        state = DONE;
                        currentToken = PLUSEQUALBIG;
                    } else {
                        state = ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve +=>.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case FY:
                    if (isDigit(c)) {
                        state = FY;
                        currentToken = NUM;
                    } else if (c == '.') {
                        state = DO;
                        currentToken = NUM;
                    } else {
                        state = ST;
                        pointer--;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve . or digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case DO:
                    if (isDigit(c)) {
                        state = AG;
                        currentToken = NUM;
                    } else {
                        state = ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve digit here.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case AG:
                    if (isDigit(c)) {
                        state = AG;
                        currentToken = NUM;
                    } else state = DONE;
                    break;
                case MI:
                    if (isDigit(c)) {
                        state = FY;
                        currentToken = NUM;
                    } else if (c == '>') {
                        state = DONE;
                        currentToken = MINUSBIG;
                    } else {
                        state = ST;
                        faultList.add(lineNum + " : " + source.substring(0, source.length() - 1) + "%" + endCol + "%" + "unrecognized word '" + c + "'" + ", only approve ->, => or -num.");
                        tokenList.add("<" + source.substring(beginCol, endCol) + "," + ERROR + ">");
                    }
                    break;
                case TW:
                    if (c == ':') {
                        state = DONE;
                        currentToken = DOUBLECOLON;
                    } else {
                        state = DONE;
                        currentToken = COLON;
                    }
                    break;
                case DONE:
                    break;
                default:
                    break;
            } // End switch state

            if (state == DONE) {
                if (currentToken == NUM || currentToken == WORD) {
                    String tmp = source.substring(beginCol, endCol);
                    tokenList.add("<" + tmp + "," + (getKeyword(tmp) == -1 ? currentToken : getKeyword(tmp)) + ">");
                    if (getKeyword(tmp) == -1 && currentToken == WORD) {
                        if (!symbolList.contains(tmp)) {
                            symbolList.add(tmp);
                        }
                    }
                    result.append(" ").append(tmp);
                    currentToken = -1;
                } else if (currentToken == COLON) {
                    tokenList.add("<" + ":" + "," + COLON + ">");
                    result.append(":");
                    currentToken = -1;
                } else if (currentToken == DOUBLECOLON) {
                    tokenList.add("<" + "::" + "," + DOUBLECOLON + ">");
                    result.append("::");
                    currentToken = -1;
                    pointer++;
                } else {
                    if (source.substring(beginCol, endCol) != "\n") {
                        // String toAppend = source.substring(beginCol, endCol);
                        tokenList.add("<" + source.substring(beginCol, endCol + 1) + "," + currentToken + ">");
                        result.append(" ").append(source.substring(beginCol, endCol + 1));
                    }
                    pointer++;
                    currentToken = -1;
                }
                // System.out.println(result.toString());
                pointer--;
                state = ST;
            } // state == DONE end
        }

    }

    /**
     * 判断输入字符串是否是关键字
     *
     * @param identifier 输入字符串
     * @return 关键字的状态常量
     */
    private static int getKeyword(String identifier) {
        int keyword = -1;
        switch (identifier) {
            case "thread":
                keyword = THREAD;
                break;
            case "features":
                keyword = FEATURES;
                break;
            case "flows":
                keyword = FLOWS;
                break;
            case "properties":
                keyword = PROPERTIES;
                break;
            case "end":
                keyword = END;
                break;
            case "none":
                keyword = NONE;
                break;
            case "in":
                keyword = IN;
                break;
            case "out":
                keyword = OUT;
                break;
            case "data":
                keyword = DATA;
                break;
            case "port":
                keyword = PORT;
                break;
            case "event":
                keyword = EVENT;
                break;
            case "parameter":
                keyword = PARAMETER;
                break;
            case "flow":
                keyword = FLOW;
                break;
            case "source":
                keyword = SOURCE;
                break;
            case "sink":
                keyword = SINK;
                break;
            case "path":
                keyword = PATH;
                break;
            case "constant":
                keyword = CONSTANT;
                break;
            case "access":
                keyword = ACCESS;
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
     * 判断输入字符是否为+ -
     *
     * @param c 输入字符
     * @return 是否为+ -
     */
    private static boolean isSign(char c) {
        return c == '+' || c == '-';
    }

    /**
     * 读取文件内容返回
     *
     * @param filename 文件读经
     * @return 返回读取的字符串
     */
    private static String readFile(String filename) {
        String source = "";
        try {
            File file = new File(filename);
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(is);
                String line = null;
                while ((line = br.readLine()) != null) {
                    source += line + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
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
        File outputFile = new File(outputFilename);
        if (!outputFile.exists())
            outputFile.createNewFile();
        FileOutputStream out = new FileOutputStream(outputFile, false);
        for (String s : tokenList)
            out.write((s + '\n').getBytes("utf-8"));
        out.close();

        // 输出错误
        if (faultList.size() != 0) {
            File errorFile = new File(errorFilename);
            if (!errorFile.exists())
                errorFile.createNewFile();
            FileOutputStream error = new FileOutputStream(errorFile, false);
            for (String s : faultList) {
                String[] errorInfo = s.split("%");
                error.write((Arrays.toString(errorInfo) + "\n").getBytes("utf-8"));
                error.write(("\t\tError:" + errorInfo[2] + "\n").getBytes("utf-8"));
                error.write('\n');
            }
            error.close();
        }
    }

}
