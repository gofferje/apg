package apg.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import apg.ApgBaseVisitor;
import apg.ApgParser;
import apg.model.Expression;
import apg.model.FunctionDefinition;

public class AntlrToFunctionDefinition extends ApgBaseVisitor<FunctionDefinition> {
    private final Map<String, FunctionDefinition> functions;

    private final AntlrToExpression exprVisitor;
    private final List<String> semanticErrors;

    public AntlrToFunctionDefinition(final Map<String, FunctionDefinition> functions, final AntlrToExpression exprVisitor,
            final List<String> semanticErrors) {
        this.functions = functions;
        this.exprVisitor = exprVisitor;
        this.semanticErrors = semanticErrors;
    }

    @Override
    public FunctionDefinition visitFunctiondefinition(final ApgParser.FunctiondefinitionContext ctx) {
        final String id = ctx.ID().getText();
        final Map<String, String> params = new HashMap<>();
        final Map<Integer, String> paramOrder = new HashMap<>();
        final ApgParser.ParamdefsContext paramdefs = ctx.paramdefs();
        if (paramdefs != null) {
            for (int i=0; i < paramdefs.getChildCount(); i+=4) {
                final String paramName = paramdefs.getChild(i).getText();
                final String paramType = paramdefs.getChild(i+2).getText();
                params.put(paramName, paramType);
                paramOrder.put(i / 4, paramName);
            }
        }
        exprVisitor.params = params;
        final String returnType = ctx.TYPE().getText();

        if (functions.containsKey(id)) {
            final Token idToken = ctx.ID().getSymbol();
            final int line = idToken.getLine();
            final int column = idToken.getCharPositionInLine() + 1;
            semanticErrors.add(String.format("Error: function %s already defined (%d, %d)", id, line, column));
        } else {
            functions.put(id, new FunctionDefinition(id, new HashMap<>(params), new HashMap<>(paramOrder), returnType, null));
        }

        final Expression expression = exprVisitor.visit(ctx.expr());

        final FunctionDefinition functionDefinition = new FunctionDefinition(id, new HashMap<>(params), new HashMap<>(paramOrder),
                returnType, expression);
        functions.put(id, functionDefinition);
        exprVisitor.params.clear();

        return functionDefinition;
    }
}
