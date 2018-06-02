package parser;

import util.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.FileReader.readFile;

public class Parser {
    private static Token currentToken;
    private static String currentVal = "";

    private static List<String> sourceList = new ArrayList<>(); // 原文表
    private static StringBuilder result = new StringBuilder();  // 结果表

    private static int pointer = 0;

    /**
     * 得到下一个字符值与类型
     *
     * @return 字符类型
     */
    private static Token nextToken(){
        if (pointer < sourceList.size()){
            String s = sourceList.get(pointer++);
            int index = s.indexOf(',');
            Token token = Token.valueOf(s.substring(index + 1, s.length() - 2));
            currentVal = s.substring(1, index);
            return token;
        }else return null;
    }

    /**
     * 匹配字符类型
     *
     * @param expected 期望字符类型
     * @return 是否匹配
     */
    private static boolean match(Token expected){
        if (currentToken == expected){
            return true;
        }else {
            System.out.println("Unexpected token: " + currentVal);
            return false;
        }
    }

    private static void output(String outputFilename) throws IOException {
        System.out.println(result);
        File file = new File(outputFilename);
        FileOutputStream out = new FileOutputStream(file);
        byte o[] = result.toString().getBytes();
        out.write(o, 0, o.length);
        out.close();
    }

    public static void main(String[] args) throws IOException {
        String tokenOutFilename = "tokenOut.txt";
        String syntaxOutFilename = "SyntaxOut.txt";

        switch (args.length) {
            case 2:
                syntaxOutFilename = args[1];
            case 1:
                tokenOutFilename = args[0];
            case 0:
                break;
            default:
                System.err.println("Please input at most 2 args!");
                return;
        }

        sourceList = readFile(tokenOutFilename);

        // 输出结果
        output(syntaxOutFilename);
    }
}
