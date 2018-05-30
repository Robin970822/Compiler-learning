package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer extends State{
    public static int currentToken = 0;
    public static boolean save = false;
    public static int state = ST;

    public static List<String> faultList = new ArrayList<String>(); // 错误表
    public static StringBuilder result = new StringBuilder();       // 扫描之后的结果
    public static List<String> tokenList = new ArrayList<String>(); // 字符表
    public static List<String> symbolList = new ArrayList<String>();// 符号表

    /**
     * 扫描一行
     *
     * @param source 扫描内容
     * @param lineNum 扫描行数
     */
    private static void scan(String source, int lineNum){
        int i = 0;
        int length = source.length();       // 行长度
        int flag = 0;
        int pointer = 0;                    // 当前扫描列号

        // 开始扫描
        while (pointer < length) {
            // 暂存列号
            i = pointer;
            pointer++;
            char c = source.charAt(i);
            switch (state) {
                case ST:
                    flag = i;
                    if (isIdentifierLetter(c)) {
                        state = IL;
                        currentToken = WORD;
                    } else if (isDigit(c)) {
                        state = FY;
                        currentToken = NUM;
                    } else switch (c) {
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
                            faultList.add(lineNum + ":" + source.substring(0, source.length() - 1) + "%" +
                                    i + "%" + "unrecognized word '" + c + "'");
                            tokenList.add("<" + c + "," + ERROR + ">");
                    } // End switch c
                    break;
                case IL:
                    if (isIdentifierLetter(c) || isDigit(c)) {
                        state = IL;
                        currentToken = WORD;
                    }else if (c == '_') {
                        state = UN;
                        currentToken = WORD;
                    }else state = DONE;
                    break;
                case UN:
                    break;
                case PL:
                    break;
                case EQ:
                    break;
                case FY:
                    break;
                case DO:
                    break;
                case AG:
                    break;
                case MI:
                    break;
                case TW:
                    break;
                case DONE:
                    break;
                default:
                    break;
            } // End switch state
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
        if(identifier.equals("thread")) keyword = THREAD;
        else if(identifier.equals("features")) keyword = FEATURES;
        else if(identifier.equals("flows")) keyword = FLOWS;
        else if(identifier.equals("properties")) keyword = PROPERTIES;
        else if(identifier.equals("end")) keyword = END;
        else if(identifier.equals("none")) keyword = NONE;
        else if(identifier.equals("in")) keyword = IN;
        else if(identifier.equals("out")) keyword = OUT;
        else if(identifier.equals("data")) keyword = DATA;
        else if(identifier.equals("port")) keyword = PORT;
        else if(identifier.equals("event")) keyword = EVENT;
        else if(identifier.equals("parameter")) keyword = PARAMETER;
        else if(identifier.equals("flow")) keyword = FLOW;
        else if(identifier.equals("source")) keyword = SOURCE;
        else if(identifier.equals("sink")) keyword = SINK;
        else if(identifier.equals("path")) keyword = PATH;
        else if(identifier.equals("constant")) keyword = CONSTANT;
        else if(identifier.equals("access")) keyword = ACCESS;
        return keyword;
    }

    /**
     * 判断输入字符是否为数字
     *
     * @param c 输入字符
     * @return 是否为数字
     */
    private static boolean isDigit(char c) {
        if (c >= '0' && c <= '9')
            return true;
        else return false;
    }

    /**
     * 判断输入字符是否为字母
     *
     * @param c 输入字符
     * @return 是否为字母
     */
    private static boolean isIdentifierLetter(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            return true;
        else return false;
    }

    /**
     * 判断输入字符是否为+ -
     *
     * @param c 输入字符
     * @return 是否为+ -
     */
    private static boolean isSign(char c) {
        if (c == '+' || c == '-')
            return true;
        else return false;
    }

    /**
     * 读取文件内容返回
     *
     * @param filename 文件读经
     * @return 返回读取的字符串
     */
    private static String readFile(String filename){
        String source = "";
        try {
            File file = new File(filename);
            if(file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(is);
                String line = null;
                while ((line = br.readLine()) != null){
                    source += line + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }

    public static void main(String[] args) throws IOException {

        String sourceFilename = "test1.txt";
        String outputFilename = "tokenOut.txt";
        String errorFilename = "errorTokenOut.txt";

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
                beginColumn++;
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
        if (faultList.size() != 0){
            File errorFile = new File(errorFilename);
            if (!errorFile.exists())
                errorFile.createNewFile();
            FileOutputStream error = new FileOutputStream(errorFile, false);
            for (String s : faultList) {
                String[] errorInfo = s.split("%");
                error.write((errorInfo + "\n").getBytes("utf-8"));
                error.write(("\t\tError:" + errorInfo[2] + "\n").getBytes("utf-8"));
                error.write('\n');
            }
            error.close();
        }
    }

}
