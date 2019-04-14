public class myrpal {
    public static void main(String[] args) {
        String rpalFileName = args[0];
        STTransformer subtreeTransformer = new STTransformer();
        subtreeTransformer.constructAST(rpalFileName);
        subtreeTransformer.constructST();
    }
}
