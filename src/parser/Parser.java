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
        String SyntaxOutFilename = "SyntaxOut.txt";

        switch (args.length) {
            case 2:
                SyntaxOutFilename = args[1];
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
        output(SyntaxOutFilename);
    }
}
