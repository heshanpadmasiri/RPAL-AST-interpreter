import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class TempTreeNode extends TreeNode {

    private int depth;
    private TempTreeNode parent;

    TempTreeNode(String token, int depth) {
        super(token);
        this.depth = depth;
        this.parent = null;
    }

    public int getDepth() {
        return depth;
    }

    public TempTreeNode getParent() {
        return parent;
    }

    public void setParent(TempTreeNode parent) {
        this.parent = parent;
    }


}

public class ASTParser extends Parser {

    private String FileName;
    private List<String> tokenStrings;

    public ASTParser(String fileName) {
        super(fileName);
        FileName = fileName;
        getTokens();
        createTree();
    }

    private void getTokens(){
        try {
             this.tokenStrings = Files.readAllLines(Paths.get(this.FileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTree(){
        ArrayDeque<TempTreeNode> stack = new ArrayDeque<>();
        for (String each:this.tokenStrings) {
            String tokenValue = each.replaceAll("\\.","");
            int depth = each.length() - tokenValue.length();
            TempTreeNode node = new TempTreeNode(tokenValue,depth );
            stack.addLast(node);
        }
        var root = stack.pop();
        var previous = root;
        while (stack.size() != 0){
            var next = stack.pop();
            insertNode(previous, next);
            previous = next;
        }
        this.stack.push(root);
    }



    private void insertNode(TempTreeNode previous, TempTreeNode next){
        if (previous.getDepth() == next.getDepth()){
            // siblings insert right
            if (previous.getRightChild() == null){
                next.setParent(previous);
                previous.setRightChild(next);
            } else {
                insertNode((TempTreeNode) previous.getRightChild(), next);
            }
        } else if (previous.getDepth() < next.getDepth()){
            // children insert left
            if (previous.getLeftChild() == null){
                next.setParent(previous);
                previous.setLeftChild(next);
            } else {
                insertNode((TempTreeNode) previous.getLeftChild(), next);
            }
        } else {
            // move up
            insertNode(previous.getParent(), next);
        }
    }

    public static void main(String[] args) {
        ASTParser parser = new ASTParser("ast.txt");

    }
}

