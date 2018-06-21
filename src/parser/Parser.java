package parser;

import util.Statement;
import util.Token;

import java.io.File;
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
    private static List<String> faultList = new ArrayList<>();  // 错误表

    private static int pointer = 0;

    /**
     * 得到下一个字符值与类型
     *
     * @return 字符类型
     */
    private static Token nextToken() {
        if (pointer < sourceList.size()) {
            String s = sourceList.get(pointer++);
            int index = s.indexOf(',');
            Token token = Token.valueOf(s.substring(index + 1, s.length() - 2));
            currentVal = s.substring(1, index);
            return token;
        } else return null;
    }

    /**
     * 匹配字符类型
     *
     * @param expected 期望字符类型
     * @return 是否匹配
     */
    private static boolean match(Token expected) {
        if (currentToken == expected) {
            currentToken = nextToken();
            return true;
        } else {
            String fault = "Unexpected token: " + currentVal + ". We expected a token of \" " + expected + "\" type.";
            System.err.println(fault);
            faultList.add(fault);
            // 如果下一个字符类型能够匹配当前期望字符，那么跳过当前字符继续匹配
            currentToken = nextToken();
            if (currentToken == expected) {
                currentToken = nextToken();
            } else {
                // 否则依然回退回当前字符
                pointer -= 1;
                currentToken = nextToken();
            }
            return false;
        }
    }

    /**
     * ThreadSpec --> thread identifier [ features featureSpec ] [ flows flowSpec ] [ properties association; ] end identifier ;
     *
     * @return TreadSpec 语法树
     */
    private static TreeNode ThreadSpec() {
        TreeNode t = new TreeNode(Statement.THREAD_SPEC);
        match(Token.THREAD);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        if (currentToken == Token.FEATURES) {
            match(Token.FEATURES);
            t.child[0] = featureSpec();
        }
        if (currentToken == Token.FLOWS) {
            match(Token.FLOWS);
            t.child[1] = flowSpec();
        }
        if (currentToken == Token.PROPERTIES) {
            match(Token.PROPERTIES);
            t.child[2] = association();
            match(Token.SEMI);
        }
        match(Token.END);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        match(Token.SEMI);
        return t;
    }

    /**
     * featureSpec --> portSpec|ParameterSpec| none ;
     * <p>
     * 等价转换为
     * <p>
     * featureSpec --> identifier : IOtype (port | Parameter) | none;
     *
     * @return featureSpec 语法树
     */
    private static TreeNode featureSpec() {
        TreeNode t = new TreeNode(Statement.FEATURE_SPEC);
        if (currentToken == Token.NONE) {
            match(Token.NONE);
            t.setOp(Token.NONE);
            t.isNone = true;
            match(Token.SEMI);
            return t;
        } else {
            if (currentToken == Token.IDENTIFIER) {
                t.identifierList.add(currentVal);
            }
            match(Token.IDENTIFIER);
            match(Token.COLON);
            t.child[0] = IOtype();
            if (currentToken == Token.PARAMETER) {
                t.child[1] = Parameter();
            } else {
                t.child[1] = portSpec();
            }
        }
        return t;
    }

    /**
     * portSpec --> identifier : IOtype portType [ { { association } } ] ;
     * <p>
     * 等价转换为
     * <p>
     * portSpec --> identifier : IOtype port
     * port --> portType [ { { association } } ];
     *
     * @return portSpec 语法树
     */
    private static TreeNode portSpec() {
        TreeNode t = new TreeNode(Statement.PORT_SPEC);
        t.child[0] = portType();
        if (currentToken == Token.LBRACE) {
            match(Token.LBRACE);
            while (currentToken == Token.IDENTIFIER) {
                t.nodeList.add(association());
            }
            match(Token.RBRACE);
        }
        match(Token.SEMI);
        return t;
    }

    /**
     * portType -->data port [ reference ] | event data port [ reference ]| event port
     *
     * @return portType 语法树
     */
    private static TreeNode portType() {
        TreeNode t = new TreeNode(Statement.PORT_TYPE);
        if (currentToken == Token.DATA) {
            match(Token.DATA);
            match(Token.PORT);
            if (currentToken == Token.IDENTIFIER) {
                t.child[0] = reference();
            }
            t.setOp(Token.DATAPORT);
        } else if (currentToken == Token.EVENT) {
            match(Token.EVENT);
            if (currentToken == Token.DATA) {
                match(Token.DATA);
                match(Token.PORT);
                if (currentToken == Token.IDENTIFIER) {
                    t.child[0] = reference();
                }
                t.setOp(Token.EVENTDATAPORT);
            } else {
                match(Token.PORT);
                t.setOp(Token.EVENTPORT);
            }
        }
        return t;
    }

    /**
     * ParameterSpec --> identifier : IOtype parameter [ reference ][ { { association } } ] ;
     * <p>
     * 等价转换为
     * <p>
     * ParameterSpec --> identifier : IOtype Parameter
     * Parameter --> parameter [ reference ][ { { association } } ] ;
     *
     * @return Parameter 语法树
     */
    private static TreeNode Parameter() {
        TreeNode t = new TreeNode(Statement.PARAMETER);
        match(Token.PARAMETER);
        if (currentToken == Token.IDENTIFIER) {
            t.child[0] = reference();
        }
        if (currentToken == Token.LBRACE) {
            match(Token.LBRACE);
            while (currentToken == Token.IDENTIFIER) {
                t.nodeList.add(association());
            }
            match(Token.RBRACE);
        }
        match(Token.SEMI);
        return t;
    }

    /**
     * IOtype-->in | out | in out
     *
     * @return IOtype 语法树
     */
    private static TreeNode IOtype() {
        TreeNode t = new TreeNode(Statement.IO_TYPE);
        if (currentToken == Token.OUT) {
            match(Token.OUT);
            t.setOp(Token.OUT);
        } else if (currentToken == Token.IN) {
            match(Token.IN);
            if (currentToken == Token.OUT) {
                match(Token.OUT);
                t.setOp(Token.INOUT);
            } else {
                t.setOp(Token.IN);
            }
        }
        return t;
    }

    /**
     * flowSpec --> flowSourceSpec| flowSinkSpec| flowPathSpec| none;
     * flowSourceSpec --> identifier : flow source identifier [ { { association } } ] ;
     * flowSinkSpec --> identifier : flow sink identifier[ { { association } } ] ;
     * flowPathSpec --> identifier : flow path identifier ->identifier;
     * <p>
     * 等价转化为
     * flowSpec --> identifier : flow (SourceSpec | SinkSpec | PathSpec)| none;
     * SourceSpec --> source identifier [ { { association } } ] ;
     * SinkSpec --> sink identifier[ { { association } } ] ;
     * PathSpec --> path identifier ->identifier;
     *
     * @return flowSpec 语法树
     */
    private static TreeNode flowSpec() {
        TreeNode t = new TreeNode(Statement.FLOW_SPEC);
        if (currentToken == Token.NONE) {
            match(Token.NONE);
            t.setOp(Token.NONE);
            t.isNone = true;
            match(Token.SEMI);
        } else if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
            match(Token.IDENTIFIER);
            match(Token.COLON);
            match(Token.FLOW);
            if (currentToken == Token.SOURCE) {
                flowSourceSpec(t);
            } else if (currentToken == Token.SINK) {
                flowSinkSpec(t);
            } else if (currentToken == Token.PATH) {
                flowPathSpec(t);
            }
        }
        return t;
    }

    /**
     * SourceSpec --> source identifier [ { { association } } ] ;
     *
     * @param t flowSpec 父节点
     */
    private static void flowSourceSpec(TreeNode t) {
        t.statement = Statement.FLOW_SOURCE_SPEC;
        match(Token.SOURCE);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        if (currentToken == Token.LBRACE) {
            match(Token.LBRACE);
            while (currentToken == Token.IDENTIFIER) {
                t.nodeList.add(association());
            }
            match(Token.RBRACE);
        }
        match(Token.SEMI);
    }

    /**
     * SinkSpec --> sink identifier[ { { association } } ] ;
     *
     * @param t flowSpec 父节点
     */
    private static void flowSinkSpec(TreeNode t) {
        t.statement = Statement.FLOW_SINK_SPEC;
        match(Token.SINK);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        if (currentToken == Token.LBRACE) {
            match(Token.LBRACE);
            while (currentToken == Token.IDENTIFIER) {
                t.nodeList.add(association());
            }
            match(Token.RBRACE);
        }
        match(Token.SEMI);
    }

    /**
     * PathSpec --> path identifier ->identifier;
     *
     * @param t flowSpec 父节点
     */
    private static void flowPathSpec(TreeNode t) {
        t.statement = Statement.FLOW_PATH_SPEC;
        match(Token.PATH);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        match(Token.MINUSTO);
        if (currentToken == Token.IDENTIFIER) {
            t.identifierList.add(currentVal);
        }
        match(Token.IDENTIFIER);
        match(Token.SEMI);
    }

    /**
     * association -->[ identifier :: ] identifier splitter [ constant ] access decimal | none
     *
     * @return association 语法树
     */
    private static TreeNode association() {
        TreeNode t = new TreeNode(Statement.ASSOCIATION);
        if (currentToken == Token.NONE) {
            match(Token.NONE);
            t.isNone = true;
            t.setOp(Token.NONE);
            return t;
        } else {
            if (currentToken == Token.IDENTIFIER) {
                t.identifierList.add(currentVal);
            }
            match(Token.IDENTIFIER);
            if (currentToken == Token.DOUBLECOLON) {
                match(Token.DOUBLECOLON);
                if (currentToken == Token.IDENTIFIER) {
                    t.identifierList.add(currentVal);
                }
                match(Token.IDENTIFIER);
            }
            t.child[0] = splitter();
            if (currentToken == Token.CONSTANT) {
                match(Token.CONSTANT);
                t.setOp(Token.CONSTANT);
            }
            match(Token.ACCESS);
            if (currentToken == Token.DECIMAL) {
                t.setDecimal(Double.parseDouble(currentVal));
            }
            match(Token.DECIMAL);
        }
        return t;
    }

    /**
     * splitter-->  => | +=>
     *
     * @return splitter 语法树
     */
    private static TreeNode splitter() {
        TreeNode t = new TreeNode(Statement.SPLITTER);
        if (currentToken == Token.EQUALTO) {
            t.setOp(currentToken);
            match(Token.EQUALTO);
        } else if (currentToken == Token.PLUSEQUALTO) {
            t.setOp(currentToken);
            match(Token.PLUSEQUALTO);
        }
        return t;
    }

    /**
     * reference -->[ packageName :: ]  identifier
     * packageName --> { identifier :: }  identifier
     * <p>
     * packageName --> { identifier :: }  identifier 可等价转换为
     * packageName --> identifier { :: identifier }
     *
     * @return reference 语法树
     */
    private static TreeNode reference() {
        // 例如 Compiler::parser::Parser
        // 例如 Parser
        TreeNode t = new TreeNode(Statement.REFERENCE);
        if (currentToken == Token.IDENTIFIER) {
            // Compiler::parser::Parser
            //         ^^
            // 应该判断::

            // Parser
            //        ^^
            // 应该用t.identifierList记录标识符并返回
            match(Token.IDENTIFIER);
        }
        if (currentToken == Token.DOUBLECOLON) {
            // 有 packageName ::
            // Compiler::parser::Parser
            //         ^^
            while (true) { // 循环
                if (currentToken == Token.DOUBLECOLON) {
                    // 如果还有::，那么还在packageName中，用t.idList记录其中的标识符
                    // 回到标识符的位置
                    // Compiler::parser::Parser
                    //   ^^
                    pointer -= 2;
                    currentToken = nextToken();
                    if (currentToken == Token.IDENTIFIER) {
                        t.idList.add(currentVal);
                    }
                    match(Token.IDENTIFIER);
                    // 前进到下一个::可能出现的位置
                    // Compiler::parser::Parser
                    //                 ^^
                    pointer++;
                    currentToken = nextToken();
                } else {
                    // 如果没有::，那么已不再packageName中，用t.identifierList记录最后一个的标识符
                    // Compiler::parser::Parser
                    //                         ^^

                    // Compiler::parser::Parser
                    //                     ^^
                    pointer -= 2;
                    currentToken = nextToken();
                    if (currentToken == Token.IDENTIFIER) {
                        t.identifierList.add(currentVal);
                    }
                    match(Token.IDENTIFIER);
                    return t;
                }
            }
        } else {
            // 没有 packageName ::
            // Parser
            //        ^^

            // Parser
            //  ^^
            pointer--;
            if (currentToken == Token.IDENTIFIER) {
                t.identifierList.add(currentVal);
            }
            match(Token.IDENTIFIER);
        }
        return t;
    }

    /**
     * 输出结果
     *
     * @param outputFilename 结果输出文件
     * @throws IOException 文件读写异常
     */
    private static void output(String outputFilename) throws IOException {
        System.out.println(result);
        File file = new File(outputFilename);
        FileOutputStream out = new FileOutputStream(file);
        byte o[] = result.toString().getBytes();
        out.write(o, 0, o.length);
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
            error.write(s.getBytes("utf-8"));
            error.write('\n');
        }
        error.close();
    }

    /**
     * （先序）遍历语法树，获得语法分析结果
     *
     * @param t       语法树
     * @param level   语法树等级
     * @param builder 保存的结果
     */
    private static void preTree(TreeNode t, int level, StringBuilder builder) {
        for (int i = 0; i < level; i++) {
            builder.append("  ");
        }
        builder.append("--");
        builder.append(t.statement);
        builder.append("(");
        if (t.identifierList != null) {
            builder.append(" ");
            for (String identifier : t.identifierList) {
                builder.append(identifier);
                builder.append(" ");
            }
        }
        if (t.getDecimal() != 0) {
            builder.append(t.getDecimal());
            builder.append(" ");
        }
        if (t.idList != null && !t.idList.isEmpty()) {
            builder.append("list( ");
            for (String id : t.idList) {
                builder.append(id);
                builder.append(" ");
            }
            builder.append(")");
        }
        if (t.getOp() != null) {
            builder.append(t.getOp());
            builder.append(" ");
        }
        builder.append(")\n");
        level++;
        if (t.child[0] != null) {
            preTree(t.child[0], level, builder);
        }
        if (t.child[1] != null) {
            preTree(t.child[1], level, builder);
        }
        if (t.child[2] != null) {
            preTree(t.child[2], level, builder);
        }
        if (t.nodeList != null) {
            for (TreeNode child : t.nodeList) {
                preTree(child, level, builder);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String tokenOutFilename = "tokenOut.txt";
        String syntaxOutFilename = "SyntaxOut.txt";
        String errorFilename = "SyntaxErrorOut.txt";

        switch (args.length) {
            case 3:
                errorFilename = args[2];
            case 2:
                syntaxOutFilename = args[1];
            case 1:
                tokenOutFilename = args[0];
            case 0:
                break;
            default:
                System.err.println("Please input at most 3 args!");
                return;
        }

        sourceList = readFile(tokenOutFilename);
        List<TreeNode> treeNodeList = new ArrayList<>();
        while (pointer < sourceList.size() - 1) {
            currentToken = nextToken();
            treeNodeList.add(ThreadSpec());
            pointer--;
        }

        result.append("TREE \n");
        for (TreeNode t : treeNodeList) {
            preTree(t, 0, result);
        }

        // 输出结果
        output(syntaxOutFilename);

        // 输出错误
        if (faultList.size() != 0) {
            errorOutput(errorFilename);
        }
    }
}
