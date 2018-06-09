package parser;

import util.Statement;
import util.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析书节点
 */
public class TreeNode {
    TreeNode[] child = new TreeNode[3]; // 最多有三个孩子
    Statement statement;                // 语句类型
    boolean isNone = false;

    List<String> identifierList = new ArrayList<>();
    List<TreeNode> nodeList = new ArrayList<>();

    private Token op;

    public TreeNode(Statement statement) {
        this.statement = statement;
    }

    public Token getOp() {
        return op;
    }

    public void setOp(Token op) {
        this.op = op;
    }
}
