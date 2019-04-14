import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CSEMachine {

    private static int envMarker = 0;
    private Stack stack = new Stack();

    private HashMap environmentHash = new HashMap();
    private ArrayList controlStructuresArray;


    CSEMachine(ArrayList controlStructuresArray) {
        this.controlStructuresArray = controlStructuresArray;
    }

    private ArrayList getControlStructure(int index) {
        return (ArrayList) this.controlStructuresArray.get(index);
    }

    private String getValueOfToken(String token) {
        token = token.trim();
        int beginIndex = token.indexOf(':')+1;
        if (beginIndex <= 0)
            return token;
        return token.substring(beginIndex, token.length()-1);
    }

    @SuppressWarnings("unchecked")
    void runCSEMachine() {
        try {
            int environment = 0;
            ArrayList controlStructure = getControlStructure(environment);
            controlStructure.add(0, "PE_" + environment);       //Making the start of the processing environment as PE_0
            int controlStructureLength;

            stack.push("PE_0");
            // get the last element from the control structure and examine if its jus an identifier or gamma

            while (controlStructure.size() > 0) {     // the last left out value will be the PE_0 env
                controlStructureLength = controlStructure.size();
                String item = (String) controlStructure.get(controlStructureLength-1);
                item = item.trim();
                item = item.replaceAll(";", "");    // hack for tuples
                controlStructure.remove(controlStructureLength-1);

                if (item.equals("")) {
                    continue;
                }
                else if (item.equals("aug")) {
                    String rator = (String) stack.pop();
                    rator = rator.replaceAll("\\(", "");
                    rator = rator.replaceAll("\\)", "");
                    String rand = (String) stack.pop();
                    rand = rand.replaceAll("\\(", "");
                    rand = rand.replaceAll("\\)", "");
                    if (rator.equals("<nil>")) {
                        stack.push(rand);
                        continue;
                    }
                    else {
                        rand = rator + "," + rand;
                    }
                    StringBuilder sb = new StringBuilder();
                    StringTokenizer st = new StringTokenizer (rand, ",");
                    while (st.hasMoreTokens()) {
                        sb.append(getValueOfToken(st.nextToken().trim())).append(",");
                    }
                    sb.deleteCharAt(sb.length()-1);  // deleting the last comma
                    rand = "(" + sb.toString() + ")";
                    stack.push(rand);
                }
                else if (item.equals("neg")) {
                    String rand = (String) stack.pop();
                    rand = getValueOfToken(rand);
                    rand = "-" + rand;
                    stack.push(rand);
                }
                else if (item.equals("eq")) {
                    String rand1 = (String) stack.pop();
                    String rand2 = (String) stack.pop();
                    String i1 = getValueOfToken(rand1);
                    String i2 = getValueOfToken(rand2);
                    i1 = i1.replaceAll("'", "");
                    i2 = i2.replaceAll("'", "");
                    if (i1.equals (i2)) {
                        stack.push("1");
                    }
                    else {
                        stack.push("0");
                    }
                }
                else if (item.equals("&")) {
                    String rand1 = (String) stack.pop();
                    String rand2 = (String) stack.pop();

                    Integer i1 = Integer.valueOf(getValueOfToken(rand1));
                    Integer i2 = Integer.valueOf(getValueOfToken(rand2));

                    String result = "" + (i1 & i2);
                    stack.push(result);
                }
                else if (item.equals ("ne")) {
                    String rand1 = (String) stack.pop();
                    String rand2 = (String) stack.pop();
                    rand1 = getValueOfToken(rand1);
                    rand2 = getValueOfToken(rand2);
                    if (! rand1.equals(rand2)) {
                        stack.push("1");
                    }
                    else
                        stack.push("0");
                }
                else if (item.equals("not")) {
                    String rand1 = (String) stack.pop();
                    if (rand1.equals("0")) {
                        stack.push("1");
                    }
                    else {
                        stack.push("0");
                    }
                }
                else if (item.equals("ls")) {
                    String rator = (String) stack.pop();
                    String rand = (String) stack.pop();
                    Integer i1 = Integer.valueOf(getValueOfToken(rator));
                    Integer i2 = Integer.valueOf(getValueOfToken(rand));
                    if (i1 < i2 ) {
                        stack.push("1");
                    }
                    else {
                        stack.push("0");
                    }
                }
                else if (item.equals ("or")) {
                    String s1 = (String) stack.pop();
                    String s2 = (String) stack.pop();

                    Integer i1 = Integer.valueOf(s1);
                    Integer i2 = Integer.valueOf(s2);

                    int result = i1 | i2;
                    stack.push(""+result);
                }
                else if (item.startsWith("Beta")) {
                    item = item.replaceAll("Beta:", "Beta ");
                    if (item.contains(" ")) {
                        StringTokenizer st = new StringTokenizer(item, " ");
                        while (st.hasMoreTokens()) {
                            controlStructure.add(st.nextToken());
                        }
                    }
                    else {
                        String result = (String) stack.pop();   // at this point, the stack should only have a 1 or 0
                        if (result.equals("1")) {
                            removeDelta(controlStructure, "deltaelse:");
                        }
                        else {
                            removeDelta(controlStructure, "deltathen:");
                        }
                    }
                }
                else if (item.startsWith("deltaelse:")) {
                    item = item.replaceAll("deltaelse:", "");
                    StringTokenizer st = new StringTokenizer (item, " ");
                    while (st.hasMoreTokens()) {
                        controlStructure.add(st.nextToken());
                    }
                }
                else if (item.startsWith("deltathen:")) {

                    item = item.replaceAll("deltathen:", "");
                    if (item.startsWith("<STR:") || isString (item)) {
                        controlStructure.add(item);
                        continue;
                    }

                    StringTokenizer st = new StringTokenizer (item, " ");
                    while (st.hasMoreTokens()) {
                        controlStructure.add(st.nextToken());
                    }
                }
                else if (item.contains(" ") && !isString(getValueOfToken(item)) ) {
                    StringTokenizer st = new StringTokenizer (item, " ");
                    while (st.hasMoreTokens()) {
                        controlStructure.add(st.nextToken());
                    }
                    continue;
                }

                else if (item.startsWith("PE_")) {   // CSE Rule 5
                    String value = (String) stack.pop();
                    String envEnd = (String) stack.pop();
                    // put the value back in the stack
                    if (item.equals("PE_0")) {
                        System.out.println(value);
                    }
                    else {
                        stack.push(value);
                    }
                }
                else if (isOperatorSymbol(item)) {
                    String rator = (String) stack.pop();
                    String rand = (String) stack.pop();
                    String result = apply(item, rator, rand);
                    stack.push(result);
                }
                else if (item.startsWith("tau")) {
                    if (item.indexOf(' ') >= 0) {
                        StringTokenizer st = new StringTokenizer (item, " ");
                        ArrayList temp = new ArrayList ();
                        while (st.hasMoreElements()) {
                            String s = st.nextToken();
                            s= s.replace(';', ',');
                            s=s.replaceAll("\\s", ",");    // in case the tuples are separated by spaces.

                            temp.add(s);
                        }
                        if (temp.get(temp.size()-1) == "")
                            temp.remove(temp.size()-1);
                        if (temp.size() > 0) {
                            controlStructure.addAll(temp);   // add the parsed tau values
                        }
                    }
                    else {    // RULE 9 this means there is only a single tau operator. Evaluate the _ arg and see how many elements are there. pop that many values frm the stack
                        int pos = item.indexOf('_')+1;
                        int numberOfElements = Integer.valueOf(item.substring(pos));
                        StringBuilder tempStr = new StringBuilder("(");
                        while (numberOfElements > 0) {
                            tempStr.append(stack.pop()).append(",");
                            numberOfElements--;
                        }
                        // remove the last comma
                        tempStr.setCharAt(tempStr.length()-1, '\0');

                        tempStr.append(")");
                        stack.push(tempStr.toString());
                    }

                }

                else if (item.startsWith("<ID:")) {
                    item = getValueOfToken(item);
                    HashMap currentEnvMapping = null;
                    switch (item) {
                        case "Print":
                            stack.push("Print");       // continue in case its just a print.
                            continue;
                        case "Conc":
                            stack.push("Conc");
                            continue;
                        case "Stern":
                            stack.push("Stern");
                            continue;
                        case "Stem":
                            stack.push("Stem");
                            continue;
                        case "Order":
                            stack.push("Order");
                            continue;
                        case "Istuple":
                            stack.push("Istuple");
                            continue;
                        case "Isstring":
                            stack.push("Isstring");
                            continue;
                    }
                    // lookup in the current environment, get the value and push it on to the stack.
                    int tempEnvMarker = envMarker;
                    while (tempEnvMarker >=0 ) {
                        currentEnvMapping = (HashMap) environmentHash.get("PE_" + (tempEnvMarker-1));
                        if (currentEnvMapping != null &&  currentEnvMapping.get(item) != null) {
                            break;
                        }
                        tempEnvMarker--;
                    }
                    assert currentEnvMapping != null;
                    String value = (String) currentEnvMapping.get(item);
                    stack.push(value);
                }

                else if (item.startsWith(STTransformer.LAMBDA)) {
                    item = item + ":" + envMarker;
                    stack.push(item);
                }

                else if (item.equals(STTransformer.GAMMA) || item.contains("gamma")) {
                    item = STTransformer.GAMMA; // hack for tuples
                    String itemAtTopOfStack = (String) stack.peek();


                    if (itemAtTopOfStack.equals("<Y*>")) {
                        // get the next top item in the stack
                        String stackTop = (String) stack.pop();
                        String nextStackTop = (String) stack.pop();


                        if (nextStackTop.startsWith(STTransformer.LAMBDA)) {
                            // we should push Eta_i_v:c, Workaround is to rename lambda into eta and push it into the stack.

                            nextStackTop = nextStackTop.replace(STTransformer.LAMBDA, "ETA");
                            stack.push(nextStackTop);

                        }
                    }

                    else if (itemAtTopOfStack.startsWith ("ETA")) {
                        //add 2 gammas
                        controlStructure.add(STTransformer.GAMMA);
                        controlStructure.add(STTransformer.GAMMA);
                        String lambda  = itemAtTopOfStack;
                        lambda = lambda.replace("ETA", STTransformer.LAMBDA);
                        stack.push(lambda);
                    }

                    else if (itemAtTopOfStack.equals ("Print")) {
                        String print = (String) stack.pop();   // this would be the print statement, get rid of it.
                        String result = (String) stack.pop();  // the result
                        //should we get rid of STRs
                        result = result.trim();
                        result = result.replaceAll("<STR:", "");
                        result = result.replaceAll(">", "");
                        result = result.replaceAll("<", "");
                        result = result.replaceAll("INT:", "");
                        stack.push(result);
                    }

                    else if ( ! itemAtTopOfStack.startsWith (STTransformer.LAMBDA) ) {    // if its not lambda, then apply rator to rand and push it back.
                        String rator = (String) stack.pop();
                        String rand = (String) stack.pop();
                        String result = apply(item, rator, rand);
                        stack.push(result);
                    }
                    else {
                        // need to handle tuples
                        String lambdaNode = (String) stack.pop();
                        String rand = (String) stack.pop();
                        String X = getX(lambdaNode);
                        HashMap currEnvMapping = new HashMap ();

                        if (X.indexOf(',') >= 0) {
                            X = X.substring(1, X.length()-1);
                            ArrayList randArray = new ArrayList ();
                            if (rand.startsWith("((")) {
                                rand = rand.substring(1);
                                int randLength = rand.length();
                                int i=0;
                                StringBuffer tempsb = new StringBuffer ();
                                while (i < randLength) {
                                    char a = rand.charAt(i);
                                    if (a == ')') {
                                        if (i != randLength-1 )
                                            tempsb.append (a);      // we do not want the last tuple to have a )
                                        randArray.add(tempsb.toString());
                                        tempsb = new StringBuffer ();
                                    }
                                    else {
                                        tempsb.append(a);
                                    }
                                    i++;
                                }
                                for ( i=0; i < randArray.size(); i++) {
                                    String str = (String) randArray.get(i);
                                    if (str.startsWith(",")) {
                                        str = str.substring (1);
                                        randArray.set(i, str);
                                    }
                                    if (str.endsWith(",")) {
                                        str = str.substring (0, str.length()-1);
                                        randArray.set(i, str);
                                    }
                                }

                            }
                            else {
                                rand = rand.substring(1, rand.length()-1);
                                StringTokenizer randStr = new StringTokenizer (rand, ",");

                                while (randStr.hasMoreElements()) {
                                    String str = randStr.nextToken().trim();
                                    randArray.add(getValueOfToken(str));
                                }
                            }

                            StringTokenizer st = new StringTokenizer(X, ",");
                            int i=0;
                            while (st.hasMoreTokens()) {
                                // add the mapping
                                String tempStr = st.nextToken();
                                currEnvMapping.put(tempStr, randArray.get(i));
                                i++;
                            }
                            environmentHash.put("PE_"+envMarker, currEnvMapping);
                        }

                        else {
                            currEnvMapping.put(X, rand);
                            environmentHash.put("PE_"+envMarker, currEnvMapping);
                        }

                        envMarker++;
                        String newPE = "PE_"+envMarker;

                        int k = getK(lambdaNode);
                        controlStructure.add(newPE);
                        stack.push(newPE);

                        ArrayList deltaK = getControlStructure (k);
                        controlStructure.addAll(deltaK);    // append the ouput of deltaK
                    }
                }
                else {
                    stack.push(item);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDelta(ArrayList controlStructure, String s) {
        for(int i=0; i<controlStructure.size(); i++) {
            String st =(String) controlStructure.get(i);
            if (st.startsWith (s)) {
                controlStructure.remove(i);
            }
        }
    }

    private int getK(String lambdaNode) {
        int startIndex= lambdaNode.indexOf("~")+1;
        int endIndex = lambdaNode.lastIndexOf('~');

        String k = lambdaNode.substring(startIndex, endIndex);
        return Integer.valueOf(k);

    }

    private String getX(String lambdaNode) {
        if (lambdaNode.startsWith("{")) {
            return lambdaNode.substring(1, lambdaNode.length()-1);
        }
        int startIndex = lambdaNode.lastIndexOf('~')+1;
        int endIndex = lambdaNode.indexOf(":");
        return lambdaNode.substring(startIndex, endIndex);

    }

    private boolean isOperatorSymbol(String token) {
        Pattern p = Pattern.compile("^[-+*/<>&.@/:=~|$!#%^\\[\\]{}\"'?]$");
        Matcher match = p.matcher(token);
        return match.find();
    }

    private boolean isString (String token) {
        Pattern p = Pattern.compile("^'[\t\n\\\\();,a-zA-Z0-9-+*/<>&.@/:=~|$!#%^_\\[\\]{}\"'?\\s]*'$");
        Matcher match = p.matcher(token);
        return match.find();
    }

    private String getExprValue (String token) {
        int beginIndex = token.indexOf('(')+1;
        if (beginIndex <= 0)
            return token;
        return token.substring(beginIndex, token.length()-1);
    }

    private String apply(String item, String rator, String rand) {
        if (rator.startsWith("(")) {      // this is for tuples.
            rand = getValueOfToken(rand.trim());
            rator = rator.replaceAll("\\(", "");
            rator = rator.replaceAll("\\)", "");
            int index = Integer.valueOf(rand) -1;
            String[] strArr = rator.split(",");
            return strArr[index];

        }

        if (getValueOfToken(rator).equals("Order")) {
            String[] strArray = rand.split(",");
            return "<INT:"+strArray.length+">";
        }
        else if (getValueOfToken(rator).equals("Istuple")) {
            if (rand.indexOf(',') >=0 )
                return ""+1;
            else
                return ""+0;
        }
        rator = getValueOfToken(rator);
        rand = getValueOfToken(rand);
        if (rator.equals("Print")) {
            return rand;
        }
        else if (rator.equals("Stern")) {
            rand = rand.replaceAll("'", "");   // replace all single quotes.
            rand = rand.substring(1);
            return rand;
        }
        else if (rator.equals("Stem")) {
            rand = rand.replaceAll("'", "");   // replace all single quotes.
            rand = ""+ rand.charAt(0);
            return rand;

        }
        else if (rator.equals ("Isstring")) {
            if (isString  (rand)) {
                return "1";
            }
            else
                return "0";
        }

        else if (rator.equals("Conc")) {
            rand = rand.replaceAll("'", "");
            return "Conc" + rand;
        }
        else if (rator.startsWith("Conc")) {
            rand = rand.replaceAll("'", "");
            rand = rator.replaceAll("Conc", "") + rand;
            return rand;
        }
        else if (isString(rator)) {
            return rator;
        }
        else if (isOperatorSymbol (rator)) {
            return "(" +rator + rand + ")";
        }
        else  if (isOperatorSymbol (item)) {
            char sign=item.charAt(0);
            rator = getExprValue (rator);
            rand = getExprValue (rand);
            switch (sign) {
                case '+':
                    int result = Integer.valueOf(rand) + Integer.valueOf(rator);
                    return ""+result;

                case '-':
                    result = Integer.valueOf(rator) - Integer.valueOf(rand);
                    return ""+result;
                case '*':
                    result = Integer.valueOf(rand) * Integer.valueOf(rator);
                    return ""+result;
                case '/':
                    result = Integer.valueOf(rator) / Integer.valueOf(rand);
                    return ""+result;
                default:
                    return "";
            }
        }
        else
            return "";
    }
}