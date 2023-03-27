package apg.parsing;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class SyntaxErrorListener extends BaseErrorListener {
    public static boolean hasError = false;
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine,
            final String msg,
            final RecognitionException e) {
        hasError = true;

        List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        System.err.println("Syntax Error!");
        System.err.println(String.format("Token \"%s\" (line %d, column %d): %s", ((Token)offendingSymbol).getText(),
                line, charPositionInLine + 1, msg));
        System.err.println("Rule Stack: " + stack);
    }
}
