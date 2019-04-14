import java.util.ArrayList;
import java.util.Stack;

class TreeNode {

    private String token;
    private  TreeNode leftChild = null;
    private TreeNode rightChild = null;

    TreeNode (String token) {
        this.token = token;
    }

    TreeNode (TreeNode t) {
        this.token = t.token;
    }

    void setLeftChild(TreeNode t) {
        this.leftChild = t;
    }

    void setRightChild(TreeNode t) {
        this.rightChild = t;
    }

    TreeNode getLeftChild() {
        return this.leftChild;
    }

    TreeNode getRightChild() {
        return this.rightChild;
    }

    String getTokenValue() {
        return this.token;
    }

    void setTokenValue(String token) {
        this.token = token;
    }

}

class Parser {
    Lexer lexer = new Lexer ();


    Stack<TreeNode> stack = new Stack<>();

    private TreeNode rootTreeNode;

    TreeNode getRootTreeNode() {
        return this.rootTreeNode;
    }

    private void setRootTreeNode(TreeNode rootTreeNode) {
        this.rootTreeNode = rootTreeNode;
    }


    void preOrderTraversal() {
        int depth =0 ;

        if (stack.empty()) {
            throw new RuntimeException("Stack is empty");
        }
        TreeNode root = stack.pop();

        this.setRootTreeNode(root);
        preOrder (root, depth);
    }

    void preOrder(TreeNode t, int depth) {
        ArrayList <String> elements = new ArrayList<>();

        elements.add(t.getTokenValue());
        depth++;
        if  (t.getLeftChild() != null ) {
            preOrder (t.getLeftChild(), depth);
        }
        if (t.getRightChild() != null) {
            preOrder (t.getRightChild(),depth-1);
        }
    }


}