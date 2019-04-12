import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import java.util.ArrayList;
import java.util.Stack;

public class STTransformer extends Parser {

    private String rpalFileName = "";
    static final String GAMMA = "gamma";
    static final String LAMBDA = "lambda";

    Parser p = null;
    ArrayList<String> controlStructure =  new ArrayList ();
    private static int counter = 1;
    //Stack stack = new Stack ();
    ArrayList stack = new ArrayList();
    private ArrayList  controlStructureArray = new ArrayList  ();

    public ArrayList getControlStructureArray () {
        return this.controlStructureArray;
    }

    public ArrayList getControlStructure(int index) {
        try {
            if (index < 0 ) {
                throw new ArrayIndexOutOfBoundsException();
            }
            return (ArrayList) this.controlStructureArray.get(index);
        } catch (ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
        }
        return null;
    }

    public void convertLet (TreeNode node) {
        TreeNode X = null;
        TreeNode E = null;
        TreeNode P = null;

        //TreeNode gammaNode = new TreeNode(GAMMA);
        TreeNode lambdaNode = new TreeNode (LAMBDA);

        System.out.println ("\n\nIncoming node to convertLet " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("let") ) {
            System.out.println ("Expected Let statement");
            return;
        }
        try {
            node.setTokenValue(GAMMA);

            if ( node.getLeftChild().getTokenValue().equals("=")) {

                P = node.getLeftChild().getRightChild();
                //System.out.println ("P Value "+ P.getTokenValue());
                X = node.getLeftChild().getLeftChild();
                //System.out.println ("X Value "+ X.getTokenValue());
                E = node.getLeftChild().getLeftChild().getRightChild();
                //System.out.println ("E Value "+ E.getTokenValue());
            }
            node.setLeftChild(lambdaNode);
            lambdaNode.setRightChild(E);   // This would be the transformed lambda node.
            lambdaNode.setLeftChild(X);
            X.setRightChild(P);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //preOrder(gammaNode, 0);
    }

    public void convertWhere (TreeNode node) {
        TreeNode X = null;
        TreeNode E = null;
        TreeNode P = null;

        //TreeNode gammaNode = new TreeNode(GAMMA);  //Transforming the input Where node to gamma node.
        TreeNode lambdaNode = new TreeNode (LAMBDA);
        System.out.println ("\n\nIncoming node to convertWhere " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("where") ) {
            System.out.println ("Expected where statement");
            return;
        }
        node.setTokenValue(GAMMA);


        if ( node.getLeftChild().getRightChild().getTokenValue().equals("=")) {
            //System.out.println ("= node in where detected");
            //System.out.println ("Setting P node to be " + node.getLeftChild().getTokenValue());
            P = node.getLeftChild();
            //System.out.println ("setting X to be "  + node.getLeftChild().getRightChild().getLeftChild().getTokenValue());
            X = node.getLeftChild().getRightChild().getLeftChild();
            //System.out.println ("setting E to be "  + node.getLeftChild().getRightChild().getLeftChild().getRightChild().getTokenValue());
            E =  node.getLeftChild().getRightChild().getLeftChild().getRightChild();





            node.setLeftChild(lambdaNode);

            node.setRightChild  (null);  // null the right child of the incoming where node

            //P.setLeftChild(null);
            P.setRightChild(null);
            //X.setLeftChild(null);
            X.setRightChild(P);

            lambdaNode.setLeftChild(X);

            lambdaNode.setRightChild(E);

            System.out.println ("----------------\n");
            System.out.println ("----where------\n");
            preOrder(node, 0);
        }

    }

    public void convertWithin (TreeNode node) {
        TreeNode X1 = null;
        TreeNode E1 = null;
        TreeNode X2 = null;
        TreeNode E2 = null;
        //TreeNode equalsNode = new TreeNode("=");

        TreeNode gammaNode = new TreeNode(GAMMA);
        TreeNode lambdaNode = new TreeNode (LAMBDA);




        System.out.println ("\n\nIncoming node to convertWithin  " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("within") ) {
            System.out.println ("Expected within statement");
            return;
        }

        node.setTokenValue("=");
        //node.setRightChild(null);   // This might be a blunder
        //System.out.println ("Setting P Node to be " + node.getRightChild().getTokenValue());
        System.out.println ("Left child " + node.getLeftChild().getTokenValue() + "\n");
        System.out.println ("Right child " + node.getLeftChild().getRightChild().getTokenValue());

        if ( node.getLeftChild().getTokenValue().equals("=") && node.getLeftChild().getRightChild().getTokenValue().equals("=")) {
            System.out.println ("came here" );
            System.out.println ("Setting X1 node to be " + node.getLeftChild().getLeftChild().getTokenValue());
            X1 = node.getLeftChild().getLeftChild();

            System.out.println ("Setting E1 node to be " + node.getLeftChild().getLeftChild().getRightChild().getTokenValue());
            E1 = node.getLeftChild().getLeftChild().getRightChild();

            System.out.println ("Setting X2 node to be " + node.getLeftChild().getRightChild().getLeftChild().getTokenValue());
            X2 = node.getLeftChild().getRightChild().getLeftChild();

            System.out.println ("Setting E2 node to be " + node.getLeftChild().getRightChild().getLeftChild().getRightChild().getTokenValue());
            E2 =  node.getLeftChild().getRightChild().getLeftChild().getRightChild();


            node.setLeftChild(X2);
            X2.setRightChild(gammaNode);
            X2.setLeftChild(null);
            gammaNode.setLeftChild(lambdaNode);
            gammaNode.setRightChild(null);
            lambdaNode.setRightChild(E1);
            lambdaNode.setLeftChild(X1);
            X1.setRightChild(E2);
            X1.setLeftChild(null);

            // preOrder(node, 0);
        }
    }

    public void convertRec (TreeNode node) {
        TreeNode X = null;
        TreeNode E = null;

        //TreeNode equalsNode = new TreeNode("=");
        TreeNode ystar = new TreeNode("<Y*>");


        TreeNode gammaNode = new TreeNode(GAMMA);
        TreeNode lambdaNode = new TreeNode (LAMBDA);


        System.out.println ("\n\nIncoming node to convertRec  " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("rec") ) {
            System.out.println ("Expected rec statement");
            return;
        }
        node.setTokenValue("=");
        System.out.println ("Node rec" + node.getLeftChild().getTokenValue());
        if ( node.getLeftChild().getTokenValue().equals("=")) {

            System.out.println ("Setting X node to be " + node.getLeftChild().getLeftChild().getTokenValue());
            X = node.getLeftChild().getLeftChild();
            TreeNode newX = new TreeNode(X);

            System.out.println ("Setting E node to be " + node.getLeftChild().getLeftChild().getRightChild().getTokenValue());
            E = node.getLeftChild().getLeftChild().getRightChild();

            node.setLeftChild(X);
            X.setRightChild(gammaNode);
            X.setLeftChild(null);
            gammaNode.setLeftChild(ystar);

            ystar.setRightChild(lambdaNode);
            lambdaNode.setLeftChild(newX);
            newX.setRightChild(E);

        }
    }

    public void convertFcnForm (TreeNode node) {

        TreeNode P = null;
        ArrayList<TreeNode> V = new ArrayList<TreeNode> ();
        TreeNode E = null;

        TreeNode equalsNode = new TreeNode("=");
        TreeNode gammaNode = new TreeNode(GAMMA);
        TreeNode lambdaNode = new TreeNode (LAMBDA) ;
        try {
            System.out.println ("\n\nIncoming node to convertFcn_Form  " + node.getTokenValue());
            if ( ! node.getTokenValue().equals("function_form") ) {
                System.out.println ("Expected function_form statement");
                return;
            }
            node.setTokenValue("=");   //setting function_form to be '='

            System.out.println ("Setting P node to be " + node.getLeftChild().getTokenValue());
            P = node.getLeftChild();
            TreeNode PCopy = P;

            while (! (PCopy.getRightChild().getTokenValue().equals("->") ||
                    PCopy.getRightChild().getTokenValue().equals(GAMMA) ||
                    PCopy.getRightChild().getTokenValue().startsWith("<INT:") ||
                    lexer.getTypeOfToken(PCopy.getRightChild().getTokenValue()).equalsIgnoreCase("Operator_symbol") ||
                    PCopy.getRightChild().getTokenValue().equalsIgnoreCase("aug")
            )
            ) {    // first i thought only ->
                V.add(PCopy.getRightChild());
                PCopy = PCopy.getRightChild();
            }

            E = PCopy.getRightChild(); // The last node should be E
            //System.out.println ("Last E node " + E.getTokenValue());
            P.setRightChild(lambdaNode);



            for (int i=0; i < V.size(); i++) {
                //lambdaNode = new TreeNode (LAMBDA);
                System.out.println ("Setting " +lambdaNode.getTokenValue() + " to " + V.get(i).getTokenValue() );
                lambdaNode.setLeftChild(V.get(i));         // I belive we only set up to the number of V's. However lets double check.
                if (V.size() > 1) {
                    lambdaNode = new TreeNode (LAMBDA);
                    V.get(i).setRightChild(lambdaNode);
                }
            }


            System.out.println ("Setting  " + V.get(V.size()-1).getTokenValue() + " to be " + E.getTokenValue());

            V.get(V.size()-1).setRightChild(E);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convertAnd (TreeNode node) {


        ArrayList <TreeNode> X = new ArrayList<TreeNode> ();
        ArrayList <TreeNode> E = new ArrayList<TreeNode> ();

        //TreeNode equalsNode = new TreeNode("=");

        TreeNode gammaNode = new TreeNode(GAMMA);
        TreeNode lambdaNode = new TreeNode (LAMBDA);
        TreeNode commaNode = new TreeNode (",");
        TreeNode tauNode = new TreeNode("tau");

        System.out.println ("\n\nIncoming node to convertAnd  " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("and") ) {
            System.out.println ("Expected and statement");
            return;
        }

        node.setTokenValue("="); // Converting and to '='

        if ( node.getLeftChild().getTokenValue().equals("=")) {

            TreeNode equalNode = node.getLeftChild();
            TreeNode equalNodeCopy = equalNode;
            while (equalNodeCopy != null) {

                X.add(equalNodeCopy.getLeftChild());
                E.add(equalNodeCopy.getLeftChild().getRightChild());
                equalNodeCopy = equalNodeCopy.getRightChild();
            }

            // Disconnect the last X node's right child. This infact worked.
            X.get(X.size()-1).setRightChild(null);


            node.setLeftChild(commaNode);
            commaNode.setRightChild(tauNode);

            System.out.println ("CommaNode left child " + X.get(0).getTokenValue());
            commaNode.setLeftChild(X.get(0));


            System.out.println ("TauNode left child " + E.get(0).getTokenValue());
            tauNode.setLeftChild(E.get(0));

            for (int i=0; i<X.size()-1; i++) {
                X.get(i).setRightChild(X.get(i+1));
            }

            for (int i=0; i<E.size()-1; i++) {
                E.get(i).setRightChild(E.get(i+1));
            }

        }
    }

    public void convertAtTheRateOf (TreeNode node) {
        TreeNode E1 = null;
        TreeNode N = null;
        TreeNode E2 = null;

        //TreeNode gammaNode1 = new TreeNode(GAMMA);
        TreeNode gammaNode2 = new TreeNode(GAMMA);

        System.out.println ("\n\nIncoming node to convertAtTheRateOf " + node.getTokenValue());
        if ( ! node.getTokenValue().equals("@") ) {
            System.out.println ("Expected @ statement");
            return;
        }

        node.setTokenValue(GAMMA);

        E1 = node.getLeftChild();

        N = E1.getRightChild();
        E2 = N.getRightChild();

        node.setLeftChild(gammaNode2);
        gammaNode2.setRightChild(E2);
        gammaNode2.setLeftChild(N);

        N.setRightChild(E1);

        // E1.setLeftChild(null); fix for bug on pairs1
        E1.setRightChild(null);



    }

    private void postOrder (TreeNode t) {
        if  (t.getLeftChild() != null ) {
            postOrder (t.getLeftChild());
        }
        if (t.getRightChild() != null) {
            postOrder (t.getRightChild());
        }
        //System.out.println(t.getTokenValue());
        reduceConstruct (t);
    }

    private void reduceConstruct (TreeNode t) {
        String nodeValue = t.getTokenValue();

        if (nodeValue.equals("let")) {
            convertLet(t);
        }
        else if (nodeValue.equals("where")) {
            convertWhere (t);
        }
        else if (nodeValue.equals("within")) {
            convertWithin (t);
        }
        else if (nodeValue.equals("rec")) {
            convertRec(t);
        }
        else if (nodeValue.equals("function_form")) {
            convertFcnForm(t);
        }
        else if (nodeValue.equals ("and")) {
            convertAnd(t);
        }
        else if (nodeValue.equals ("@")) {
            convertAtTheRateOf(t);
        }
        else
            return;
    }

    public void constructAST (String rpalFileName) {
        this.rpalFileName = rpalFileName;
        p = new Parser (rpalFileName);
        try {
            p.fn_E();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nextToken.equalsIgnoreCase("PARSE_COMPLETE")) {
            System.out.println ("*******PARSING COMPLETE*****");
            System.out.println ("-------TREE VALUES----------");
            p.preOrderTraversal ();
        }

    }

    public void constructST () {
        System.out.println ("------POST ORDER TRAVERSAL-------\n");

        /* IMP. We will need to invoke getRootTreeNode using the instance of the parent class.
         * The below call is going to traverse the tree recursively in a post order fashion and reduce it to a
         * partial standard tree.*/

        this.postOrder(p.getRootTreeNode());

        //Print it out in a pre-order way
        p.preOrder(p.getRootTreeNode(), 0);

        this.generateControlStructures();
    }

    public void generateControlStructures () {

        /* Logic would be to travers through the control Elemets in a pre-order way
         * when we encounter a lambda switch to a new context.
         */
        System.out.println ("Generating Control Structures");

        //stack.push(p.getRootTreeNode());
        stack.add(p.getRootTreeNode());
        //preOrderTraverse (p.getRootTreeNode());

        //while (!stack.empty()) {
        while (stack.size() != 0) {
            //TreeNode elem = (TreeNode) stack.pop();
            TreeNode elem = (TreeNode) stack.get(0);     // not a real stack, but arraylist based. so extra step will be to remove the first element.
            stack.remove(0);

            controlStructure = preOrderTraverse (elem);
            System.out.println ("CONTROL STRUCTURE : " + controlStructure);
            this.controlStructureArray.add(controlStructure);          // We will have the final control structure (flattened here)
            controlStructure = new ArrayList (); // We need to do this to clear out the arrayList

        }

        // Lets pass the generated control structures to the CSE machine class.
        CSEMachine cseMachine = new CSEMachine(controlStructureArray);
        cseMachine.runCSEMachine();
    }

    public ArrayList preOrderTraverse (TreeNode t) {


        TreeNode temp = null;

        if (t.getTokenValue().equals(LAMBDA)) {
            if (t.getLeftChild().getTokenValue().equals(",")) {
                TreeNode node = t.getLeftChild().getLeftChild();
                StringBuffer sb = new StringBuffer ();
                while (node != null) {
                    sb.append (getValueofToken(node.getTokenValue())+ ",");
                    node = node.getRightChild();
                }
                sb.deleteCharAt(sb.length()-1);
                // System.out.println (t.getTokenValue() + "_" + (counter) + "_" + "{" + sb.toString() + "}");
                controlStructure.add (t.getTokenValue() + "~" + (counter) + "~" + "{" + sb.toString() + "}");
                counter++;

            }

            else {
                //  System.out.println (t.getTokenValue() + "_" + (counter) + "_" + getValueofToken(t.getLeftChild().getTokenValue()));
                controlStructure.add (t.getTokenValue() + "~" + (counter) + "~" + getValueofToken(t.getLeftChild().getTokenValue()));
                counter++;

            }
            //stack.push(t.getLeftChild().getRightChild());
            stack.add(t.getLeftChild().getRightChild());
            if (t.getRightChild() != null) {
                preOrderTraverse (t.getRightChild());
            }

            return controlStructure; // will this backtrace ?
        }
        else if (t.getTokenValue().equals("->")) {
            StringBuffer deltaThen = new StringBuffer ();
            StringBuffer deltaElse = new StringBuffer ();
            StringBuffer B = new StringBuffer ();


            litePreOrderTraverse(t.getLeftChild().getRightChild().getRightChild(), deltaElse, "");
            controlStructure.add ("deltaelse:" + deltaElse);

            //disconnect the else part
            t.getLeftChild().getRightChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild().getRightChild(), deltaThen, "then");
            controlStructure.add ("deltathen:" + deltaThen);
            //disconnect the then part
            t.getLeftChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild(), B, "B");
            controlStructure.add("Beta:" + B);

            if (t.getRightChild() != null) {
                preOrderTraverse (t.getRightChild());
            }

            return controlStructure; // will this backtrace ?

        }
        else if (t.getTokenValue().equals("tau")) {
            StringBuffer sb = new StringBuffer ();
            litePreOrderTraverse (t, sb, "");
            controlStructure.add(sb.toString());
            return controlStructure;
        }
        else {
            //System.out.println (getValueofToken(t.getTokenValue()));
            controlStructure.add(t.getTokenValue());
        }
        if  (t.getLeftChild() != null ) {
            preOrderTraverse (t.getLeftChild());
        }
        if (t.getRightChild() != null) {
            preOrderTraverse (t.getRightChild());
        }

        return controlStructure;
    }

    private StringBuffer litePreOrderTraverse (TreeNode t, StringBuffer sb, String delta) {

        if (t.getTokenValue().equals(LAMBDA)) {
            if (t.getLeftChild().getTokenValue().equals(",")) {
                TreeNode node = t.getLeftChild().getLeftChild();
                StringBuffer sb1 = new StringBuffer ();
                while (node != null) {
                    sb1.append (getValueofToken(node.getTokenValue())+ ",");
                    node = node.getRightChild();
                }
                sb1.deleteCharAt(sb1.length()-1);
                // System.out.println (t.getTokenValue() + "_" + (counter) + "_" + "{" + sb.toString() + "}");
                // controlStructure.add (t.getTokenValue() + "_" + (counter) + "_" + "{" + sb1.toString() + "}");
                sb.append (t.getTokenValue() + "~" + (counter) + "~" + "{" + sb1.toString() + "}");
                counter++;

            }

            else {
                // System.out.println (t.getTokenValue() + "_" + (counter) + "_" + getValueofToken(t.getLeftChild().getTokenValue()));
                // controlStructure.add (t.getTokenValue() + "_" + (counter) + "_" + getValueofToken(t.getLeftChild().getTokenValue()));
                sb.append(t.getTokenValue() + "~" + (counter) + "~" + getValueofToken(t.getLeftChild().getTokenValue()) +" ");
                counter++;

            }
            //stack.push(t.getLeftChild().getRightChild());
            stack.add(t.getLeftChild().getRightChild());
            t.setLeftChild(null);   // hack to prevent the below getLeft child from running.
        }
        else if (t.getTokenValue().equals("->")) {
            StringBuffer deltaThen = new StringBuffer ();
            StringBuffer deltaElse = new StringBuffer ();
            StringBuffer B = new StringBuffer ();
            StringBuffer result = new StringBuffer ();

            litePreOrderTraverse(t.getLeftChild().getRightChild().getRightChild(), deltaElse, "");
            //controlStructure.add ("deltaelse:" + deltaElse);
            result.append("deltaelse:" + deltaElse);

            //disconnect the else part
            t.getLeftChild().getRightChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild().getRightChild(), deltaThen, "then");
            //controlStructure.add ("deltathen:" + deltaThen);
            result.append("deltathen:" + deltaThen);
            //disconnect the then part
            t.getLeftChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild(), B, "B");
            //controlStructure.add("Beta:" + B);
            result.append("Beta:" + B);

            // add this to the end  ?
            sb.append(result);
        }

        else {
            if (t.getTokenValue().equals("tau")) {
                int tempCount = 0;
                TreeNode node = t.getLeftChild();
                //System.out.println ("Processing "  + t.getTokenValue() + " left node " + t.getLeftChild().getTokenValue());
                if (node != null) {
                    if (node.getTokenValue().equals("tau")) {
                        processTauNode(node);
                    }
                    while (node != null) {
                        tempCount++;
                        node.setTokenValue(node.getTokenValue()+";");   // a differentiator for the tuple elements.
                        node = node.getRightChild();

                        // check if the internal node is again a tau.
                        if (node != null && node.getTokenValue().equals("tau") ) {
                            processTauNode(node);
                        }
                    }
                }
                t.setTokenValue("tau_"+tempCount+";");
            }
            sb.append(t.getTokenValue() + " " );
        }


        if  (t.getLeftChild() != null ) {
            litePreOrderTraverse (t.getLeftChild(), sb, delta);
        }
        //if (! (delta.equals("then") || delta.equals("B") )) {
        if (t.getRightChild() != null) {
            litePreOrderTraverse (t.getRightChild(), sb, delta);
        }
        //}
        //sb.deleteCharAt(sb.length()-1);
        return sb;
    }

    private void processTauNode (TreeNode t) {
        if (t.getTokenValue().equals("tau")) {
            int tempCount = 0;
            TreeNode node = t.getLeftChild();
            if (node != null) {
                while (node != null) {
                    tempCount++;
                    node.setTokenValue(node.getTokenValue()+";");   // a differentiator for the tuple elements.
                    node = node.getRightChild();
                }
            }
            t.setTokenValue("tau_"+tempCount);
        }
    }


    private String getValueofToken (String token) {
        int beginIndex = token.indexOf(':')+1;
        if (beginIndex <= 0)
            return token;
        return token.substring(beginIndex, token.length()-1);
    }
    public static void main (String args[]) {
        // create Options object
        Options options = new Options();
        // add t option
        options.addOption("ast", true, "rpal test file name");
        options.addOption("noout", false, "No output computation");

        CommandLineParser parser = new PosixParser();
        STTransformer subtreeTransformer = null;
        try {
            CommandLine cmd = parser.parse( options, args);
            if (cmd.hasOption("l")) {
                System.out.println ("USAGE: ./p1 [-ast][-noout] <testfile>");
                return;
            }

            if (cmd.hasOption("ast")) {
                String rpalFileName = cmd.getOptionValue("ast");
                subtreeTransformer = new STTransformer();
                subtreeTransformer.constructAST(rpalFileName);

            }
            if (! cmd.hasOption("noout")) {

                subtreeTransformer.constructST();
            }
        } catch (Exception e1) {
            System.out.println (e1.getMessage());
        }
    }

}