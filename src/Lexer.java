import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {

    /*
    * Used to do lexical analysis to determine type
    * */

    boolean isOperatorSymbol(String token) {
        Pattern p = Pattern.compile("^[-+*/<>&.@/:=~|$!#%^\\[\\]{}\"'?]$");  //  ^[-+*/<>&.@/:=~|$!#%^_\\[\\]{}\"'?]$
        Matcher match = p.matcher(token);
        boolean result = match.find();
        return result;
    }

}