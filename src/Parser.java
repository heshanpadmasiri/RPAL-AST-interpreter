import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

class TreeNode {

    String token;
    private  TreeNode leftChild = null;
    private TreeNode rightChild = null;

    TreeNode (String token) {
        this.token = token;
    }

    TreeNode (TreeNode t) {
        this.token = t.token;
    }

    public void setLeftChild (TreeNode t) {
        this.leftChild = t;
    }

    public void setRightChild (TreeNode t) {
        this.rightChild = t;
    }

    public TreeNode getLeftChild () {
        return this.leftChild;
    }

    public TreeNode getRightChild () {
        return this.rightChild;
    }

    public String getTokenValue () {
        return this.token;
    }

    public void setTokenValue (String token) {
        this.token = token;
    }

}

public class Parser {
    Lexer lexer = new Lexer ();
    // HashMap lexTable;
    public static String nextToken;
    private static int index = -1;

    private static final String ID = "ID:";
    private static final String STR = "STR:";
    private static final String INT = "INT:";

    Stack stack = new Stack ();
    ArrayList tokenList;
    static String [] reserved = {"let", "in", "fn", "where", "aug", "within", "and", "eq"};
    static ArrayList reservedTokens = new ArrayList ();

    private TreeNode rootTreeNode;


    static {
        for (String s: reserved ) {
            reservedTokens.add(s);
        }
    }

    public TreeNode getRootTreeNode () {
        return this.rootTreeNode;
    }

    public void setRootTreeNode (TreeNode rootTreeNode) {
        this.rootTreeNode = rootTreeNode;
    }
    /**
     * Constructor for the Parser
     *
     * @param fileName
     */
    public Parser (String fileName) {
        lexer.readFile (fileName);
        lexer.constructTokens();
        tokenList = lexer.getTokens();
        // lexTable = lexer.getLexTable();
        nextToken = getNextToken();
    }

    Parser() {
    }


    /**
     * Utility to read the tokens and to identify if it's the correct one.
     *
     * @param token
     * @throws Exception
     */
    public void readToken (String token) throws Exception {
        if ( ! token.equalsIgnoreCase (nextToken)) {
            throw new Exception ("Error: Expected "+ token + " but found: "+ nextToken);
        }
        String type = lexer.getTypeOfToken(token);
        if ((type.equalsIgnoreCase("Identifier") ||  type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("String")) && !token.equals("in") &&!token.equals("eq")
                && !token.equals("rec") && !token.equals ("where") && !token.equals ("let") && !token.equals("within") &&!token.equals("and") &&!token.equals("fn") &&!token.equals (",")
                &&!token.equals (".") && !token.equals ("le") && !token.equals ("gr") && !token.equals ("ge") && !token.equals ("ls") && !token.equals("or") && !token.equals("not")
                && !token.equals("aug") && !token.equals ("nil")  && !token.equals ("ne") && !token.equalsIgnoreCase ("true") && !token.equalsIgnoreCase ("false") && !token.equalsIgnoreCase ("dummy"))
        {
            if (token.equals ("nil")) {
                token = "<nil>";
            }
            else if (lexer.getTypeOfToken(token).equals("String")) {
                if (token.equals("' ) '")) {
                    token = "')'";
                }
                if (token.equals("' ( '")) {
                    token = "'('";
                }
                if (token.equals("' , '")) {
                    token = "','";
                }
                token = "<"+STR+token+">";
            }
            else if (lexer.getTypeOfToken(token).equals("Integer")) {
                token = "<"+INT+token+">";
            }
            else if (lexer.getTypeOfToken(token).equals("Identifier")) {
                token = "<"+ID+token+">";
            }
            else {
                token = token;
            }
            Build_tree (token, 0);
        }
        nextToken = getNextToken ();
    }

    /**
     * Utility to build the tree. It uses a first child - next sibling approach
     *
     * @param token
     * @param n
     */
    private void Build_tree(String token, int n) {
        TreeNode treeNode = new TreeNode (token);
        ArrayList <TreeNode>treeNodesList = new ArrayList<TreeNode>  ();
        TreeNode tempNode, lastNode, lastButOneNode;
        if (n == 0) {  // Just push the tree to the stack
            stack.push(treeNode);
        }
        else {   // We will pop 'i' trees from the stack, connect it to-gether like a first child next sibling and then push the resulting one to the stack again...
            for (int i=0; i<n ; i++) {
                treeNodesList .add((TreeNode) stack.pop());
            }
            Collections.reverse(treeNodesList);

            for (int i=0; i<treeNodesList.size()-1; i++) {
                treeNodesList.get(i).setRightChild(treeNodesList.get(i+1)); // ? shouldnt we update the tree node ??

            }
            treeNode.setLeftChild(treeNodesList.get(0));



            stack.push(treeNode);


        }

    }

    /**\
     * Tester for Build tree
     *
     */
    public void testBuild_tree () {
        Build_tree ("let", 0);
        Build_tree("where", 0);
        Build_tree("then", 2);
        preOrderTraversal();
    }

    public void preOrderTraversal () {
        int depth =0 ;
        int noLeft = 0;

        if (stack.empty()) {
            throw new RuntimeException("Stack is empty");
        }
        TreeNode root = (TreeNode) stack.pop();
        TreeNode temp = root;

        this.setRootTreeNode(temp);
        preOrder (root, depth);
    }

    protected ArrayList<String> preOrder (TreeNode t, int depth) {
        ArrayList <String> elements = new ArrayList <String> ();

        elements.add(t.getTokenValue());
        depth++;
        if  (t.getLeftChild() != null ) {
            preOrder (t.getLeftChild(), depth);
        }
        if (t.getRightChild() != null) {
            preOrder (t.getRightChild(),depth-1);
        }
        return elements;
    }
	
	/*private int height(TreeNode root)
	{
        int ldepth=0;
        int rdepth=0;
		if(root.getTokenValue() == this.rootTreeNode.getTokenValue())
	        return 0;
	    ldepth = height(root.getLeftChild()) + 1;
	    rdepth = height(root.getRightChild()) + 1;

	    if(ldepth > rdepth)
	        return ldepth;
	    else
	        return rdepth;
	}*/

    /**
     *
     * Utility to get the next token from the input. Or rather the lexer table. This updates the nextToken, which is always one token ahead
     * @return
     */
    private String getNextToken() {
        index++;
        if (index == tokenList.size())  {
            return "PARSE_COMPLETE";
        }
        return (String) tokenList.get(index);
    }

    private boolean isReserved (String token) {
        if (reservedTokens.contains(token)) {
            return true;
        }
        else {
            return false;
        }
    }

    /************************************************************
     * Procedures for the various Non terminals...
     *
     ************************************************************/

    public void fn_E () throws Exception {
        if (nextToken.equalsIgnoreCase("let")) {

            readToken("let");
            fn_D ();
            readToken("in");
            fn_E ();
            Build_tree("let", 2);

        }
        else if (nextToken.equalsIgnoreCase("fn")) {

            readToken ("fn");
            int n = 0;
            do {
                fn_Vb();
                n++;
            } while (lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
            readToken (".");
            fn_E ();
            Build_tree ("lambda", n+1);


        }
        else {
            fn_Ew ();
        }
    }


    private void fn_Ew() throws Exception {
        fn_T ();
        if (nextToken.equalsIgnoreCase ("where")) {
            readToken ("where");
            fn_Dr();
            Build_tree("where", 2);
        }

    }


    private void fn_Dr() throws Exception {
        if (nextToken.equalsIgnoreCase ("rec")) {
            readToken ("rec");
            fn_Db();
            Build_tree("rec", 1);
        }
        else {
            fn_Db();
        }

    }


    private void fn_Db() throws Exception {

        if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("(")) {

            readToken ("(");
            fn_D ();
            readToken (")");


        }

        else {
            if ( lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier")) {
                int n = 1;
                fn_V1();
                if (nextToken.equals ("=")) {
                    readToken ("=");
                    fn_E ();
                    Build_tree("=", 2);
                }
                else {

                    do {
                        fn_Vb();
                        n++;
                    } while ( (!nextToken.equals("=")) && lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
                    readToken ("=");
                    fn_E ();
                    Build_tree("function_form", (n+1));
                }
            }
        }

    }


    private void fn_T() throws Exception {
        fn_Ta ();
        if (lexer.getTypeOfToken(nextToken).equals (",")) {
            int n = 0;
            do {
                readToken (",") ;
                fn_Ta();
                n++;
            }
            while (lexer.getTypeOfToken(nextToken).equals (",") );
            Build_tree("tau", n+1);
        }
    }


    private void fn_Ta() throws Exception {
        fn_Tc ();

        if (nextToken.equalsIgnoreCase ("aug")) {
            while (nextToken.equalsIgnoreCase ("aug")) {
                readToken ("aug");
                fn_Tc();
                Build_tree ("aug", 2);

            }
        }
    }


    private void fn_Tc() throws Exception {
        fn_B ();
        if (nextToken.equalsIgnoreCase("->")) {
            readToken ("->");
            fn_Tc();
            readToken ("|");
            fn_Tc();
            Build_tree ("->", 3);
        }
    }


    private void fn_B() throws Exception {
        fn_Bt ();
        if (nextToken.equalsIgnoreCase("or")) {
            while(nextToken.equalsIgnoreCase("or")) {
                readToken ("or");
                fn_Bt ();
                Build_tree ("or", 2);
            }
        }
    }


    private void fn_Bt() throws Exception {
        fn_Bs ();
        if (nextToken.equalsIgnoreCase("&")) {
            while(nextToken.equalsIgnoreCase("&")) {
                readToken ("&");
                fn_Bs ();
                Build_tree ("&", 2);
            }
        }
    }


    private void fn_Bs() throws Exception {
        if (nextToken.equalsIgnoreCase("not")) {
            readToken ("not");
            fn_Bp ();
            Build_tree ("not", 1);
        }
        else {
            fn_Bp();
        }
    }


    private void fn_Bp() throws Exception {

        fn_A ();
        if (nextToken.equalsIgnoreCase("eq")) {
            readToken ("eq");
            fn_A ();
            Build_tree ("eq", 2);
        }
        else if (nextToken.equalsIgnoreCase("ne")) {
            readToken ("ne");
            fn_A ();
            Build_tree ("ne", 2);
        }

        else  {

            String temp = nextToken;
            if (temp.equalsIgnoreCase("gr") || temp.equalsIgnoreCase(">")) {
                readToken (temp);
                fn_A ();
                Build_tree("gr", 2);
            }
            else if (temp.equalsIgnoreCase("ge") || temp.equalsIgnoreCase(">=")) {
                readToken (temp);
                fn_A ();
                Build_tree("ge", 2);
            }
            else if (temp.equalsIgnoreCase("ls") || temp.equalsIgnoreCase("<")) {
                readToken (temp);
                fn_A ();
                Build_tree("ls", 2);
            }
            else if (temp.equalsIgnoreCase("le") || temp.equalsIgnoreCase(">")) {
                readToken (temp);
                fn_A ();
                Build_tree("le", 2);
            }
        }
    }


    private void fn_A() throws Exception {
        if (nextToken.equalsIgnoreCase("+")) {
            readToken ("+");
            fn_At ();
        }
        else if (nextToken.equalsIgnoreCase("-")) {
            readToken ("-");
            fn_At ();
            Build_tree ("neg", 1);
        }
        else {
            fn_At ();
        }
        while (nextToken.equalsIgnoreCase("+") || nextToken.equalsIgnoreCase("-")  ) {
            if (nextToken.equalsIgnoreCase("+")) {
                readToken ("+");
                fn_At ();
                Build_tree ("+", 2);
            }
            else {
                readToken ("-");
                fn_At ();
                Build_tree ("-", 2);
            }
        }
    }





    private void fn_At() throws Exception {
        fn_Af();
        while (nextToken.equalsIgnoreCase("*") || nextToken.equalsIgnoreCase("/")  ) {
            if (nextToken.equalsIgnoreCase("*")) {
                readToken ("*");
                fn_Af();
                Build_tree ("*", 2);
            }
            else {
                readToken ("/");
                fn_Af ();
                Build_tree ("/", 2);
            }

        }


    }

    private void fn_Af() throws Exception {
        fn_Ap();
        if (nextToken.equalsIgnoreCase("**")) {
            readToken ("**");
            fn_Af();
            Build_tree ("**", 2);
        }

    }

    private void fn_Ap() throws Exception {
        fn_R();
        if (nextToken.equalsIgnoreCase("@")) {
            while (nextToken.equalsIgnoreCase("@")) {
                readToken ("@");
                readToken (nextToken);
                fn_R();
                Build_tree ("@", 3);
            }
        }
    }
    private void fn_R() throws Exception {
        fn_Rn ();
        if (nextToken.equalsIgnoreCase("PARSE_COMPLETE")) {
            return;
        }
        while ( (!isReserved(nextToken) && !lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Arrow")
                && !nextToken.equals("|") && !lexer.getTypeOfToken (nextToken).equalsIgnoreCase("Operator_symbol")
                && !nextToken.equals("gr") && !nextToken.equals("ge") && !nextToken.equals("ls") && !nextToken.equals("le") && !nextToken.equals("or")
                && !nextToken.equals("ne")
                && ! lexer.getTypeOfToken (nextToken).equalsIgnoreCase(")") && !nextToken.equals(",") && !nextToken.equals("aug"))  &&
                (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Integer") ||
                        lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("Identifier") || lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("true")
                        || lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("false")  ||
                        lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("(") || lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("dummy")) ||
                lexer.getTypeOfToken(nextToken).equalsIgnoreCase ("String") )
        {


            fn_Rn ();
            Build_tree ("gamma", 2);
            if (nextToken.equalsIgnoreCase("PARSE_COMPLETE")) {
                return;
            }
        }

    }


    private void fn_Rn() throws Exception {

        if (nextToken.equalsIgnoreCase("True")) {
            readToken ("True");
            Build_tree ("<true>", 0);
        }
        else if (nextToken.equalsIgnoreCase("False")) {
            readToken ("False");
            Build_tree ("<false>", 0);
        }
        else if (nextToken.equalsIgnoreCase("nil")) {
            readToken ("nil");
            Build_tree ("<nil>", 0);
        }
        else if (nextToken.equalsIgnoreCase("(")) {
            readToken ("(");
            fn_E();
            readToken (")");
        }
        else if (nextToken.equalsIgnoreCase("dummy")){
            readToken ("dummy");
            Build_tree ("<dummy>",0);
        }
        else {
            if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier") || lexer.getTypeOfToken(nextToken).equalsIgnoreCase("String")|| lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Integer")) {
                readToken (nextToken);
            }
        }
    }



    private void fn_Vb() throws Exception {

        if (nextToken.equalsIgnoreCase("(")) {
            readToken ("(");
            if (nextToken.equalsIgnoreCase(")")) {
                Build_tree ("()", 2);
            }
            else {
                fn_V1 ();
                readToken (")");
            }
        }

        else {
            readToken (nextToken);
        }

    }


    private void fn_V1() throws Exception {
        int n=0;
        if (lexer.getTypeOfToken(nextToken).equals("Identifier")) {
            readToken (nextToken);
        }
        //while (lexer.getTypeOfToken(nextToken).equals("Identifier") || nextToken.equals(",")) {
        if (nextToken.equals(",")) {
            while (nextToken.equals(",")) {
                readToken (",");
                readToken (nextToken);
                n++;
            }
            Build_tree (",",n+1);
        }
    }

    private void fn_D() throws Exception {
        fn_Da();
        if (nextToken.equalsIgnoreCase("within")) {
            readToken ("within");
            fn_D();
            Build_tree ("within", 2);
        }

    }

    private void fn_Da() throws Exception {
        fn_Dr ();
        if (nextToken.equalsIgnoreCase("and")) {
            int n=0;
            while ( nextToken.equalsIgnoreCase ("and")) {
                readToken ("and");
                fn_Dr ();
                n++;
            }
            Build_tree ("and", n+1);
        }
    }

    public static void main (String args[]) {
        // create Options object
        Options options = new Options();
        // add t option
        options.addOption("ast", true, "rpal test file name");
        options.addOption("noout", false, "No output computation");

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse( options, args);
            if (cmd.hasOption("l")) {
                return;
            }
            if (cmd.hasOption("noout")) {

            }
            if (cmd.hasOption("ast")) {
                String rpalFileName = cmd.getOptionValue("ast");
                Parser p = new Parser (rpalFileName);
                p.fn_E();
                if (nextToken.equalsIgnoreCase("PARSE_COMPLETE")) {
                    p.preOrderTraversal ();
                    System.exit(0);
                }
            }
        } catch (Exception e1) {
            System.out.println (e1.getMessage());
        }
    }


}