package apg.parsing;

import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import apg.ApgBaseVisitor;
import apg.ApgParser;
import apg.model.BooleanVal;
import apg.model.Declaration;
import apg.model.Expression;
import apg.model.Number;
import apg.model.Value;

public class AntlrToDeclaration extends ApgBaseVisitor<Declaration> {

    private final Set<String> vars;
    private final List<String> semanticErrors;

    public AntlrToDeclaration(final Set<String> vars, final List<String> semanticErrors) {
        this.vars = vars;
        this.semanticErrors = semanticErrors;
    }

    @Override
    public Declaration visitDeclaration(final ApgParser.DeclarationContext ctx) {
        final String id = ctx.ID().getText();
        if (vars.contains(id)) {
            final Token idToken = ctx.ID().getSymbol();
            final int line = idToken.getLine();
            final int column = idToken.getCharPositionInLine() + 1;
            semanticErrors.add(String.format("Error: variable %s already declared (%d, %d)", id, line, column));
        }
        final String type = ctx.TYPE().getText();

        final Value value;
        if (ctx.NUM() != null) {
            value = new Number(Integer.parseInt(ctx.NUM().getText()));
        } else {
            value = new BooleanVal(Boolean.parseBoolean(ctx.BOOL().getText()));
        }
        return new Declaration(id, type, value);
    }
}
