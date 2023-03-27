package apg.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import apg.ApgBaseVisitor;
import apg.ApgParser;
import apg.model.Addition;
import apg.model.And;
import apg.model.BooleanVal;
import apg.model.Equals;
import apg.model.Expression;
import apg.model.FunctionCall;
import apg.model.FunctionDefinition;
import apg.model.If;
import apg.model.Minus;
import apg.model.Multiplication;
import apg.model.Number;
import apg.model.Or;
import apg.model.Variable;

public class AntlrToExpression extends ApgBaseVisitor<Expression> {

    private final Set<String> vars;

    public Map<String, String> params;

    private final Map<String, FunctionDefinition> functions;
    private final List<String> semanticErrors;

    public AntlrToExpression(Set<String> vars, Map<String, FunctionDefinition> functions, List<String> semanticErrors) {
        this.vars = vars;
        this.params = new HashMap<>();
        this.functions = functions;
        this.semanticErrors = semanticErrors;
    }

    @Override
    public Expression visitUnaryMinus(final ApgParser.UnaryMinusContext ctx) {
        Expression expr = visit(ctx.getChild(1));
        return new Multiplication(new Number(-1), expr);
    }
    @Override
    public Expression visitMultiplication(final ApgParser.MultiplicationContext ctx) {
        Expression left = visit(ctx.getChild(0));
        Expression right = visit(ctx.getChild(2));
        return new Multiplication(left, right);
    }

    @Override
    public Expression visitAddition(final ApgParser.AdditionContext ctx) {
        Expression left = visit(ctx.getChild(0));
        Expression right = visit(ctx.getChild(2));
        String operator = ctx.op.getText();
        return "+".equals(operator) ? new Addition(left, right) : new Minus(left, right);
    }

    @Override
    public Expression visitAnd(final ApgParser.AndContext ctx) {
        Expression left = visit(ctx.getChild(0));
        Expression right = visit(ctx.getChild(2));
        return new And(left, right);
    }

    @Override
    public Expression visitOr(final ApgParser.OrContext ctx) {
        Expression left = visit(ctx.getChild(0));
        Expression right = visit(ctx.getChild(2));
        return new Or(left, right);
    }

    @Override
    public Expression visitEquals(final ApgParser.EqualsContext ctx) {
        Expression left = visit(ctx.getChild(0));
        Expression right = visit(ctx.getChild(2));
        return new Equals(left, right);
    }

    @Override
    public Expression visitIf(final ApgParser.IfContext ctx) {
        Expression cond = visit(ctx.getChild(1));
        Expression thenExpr = visit(ctx.getChild(3));
        Expression elseExpr = visit(ctx.getChild(5));
        return new If(cond, thenExpr, elseExpr);
    }

    @Override
    public Expression visitVariable(final ApgParser.VariableContext ctx) {
        final String id = ctx.ID().getText();
        if (params.containsKey(id)) {
            return new Variable(id);
        }
        if (!vars.contains(id)) {
            final Token idToken = ctx.ID().getSymbol();
            final int line = idToken.getLine();
            final int column = idToken.getCharPositionInLine() + 1;
            semanticErrors.add(String.format("Error: variable %s not declared (%d, %d)", id, line, column));
        }
        return new Variable(id);
    }

    @Override
    public Expression visitFunctionCall(final ApgParser.FunctionCallContext ctx) {
        final String id = ctx.ID().getText();
        final FunctionDefinition fundef = functions.get(id);
        final List<Expression> parameters = new ArrayList<>();
        if (fundef == null) {
            final Token idToken = ctx.ID().getSymbol();
            final int line = idToken.getLine();
            final int column = idToken.getCharPositionInLine() + 1;
            semanticErrors.add(String.format("Error: function %s not defined (%d, %d)", id, line, column));
        }
        final ApgParser.ParamsContext params = ctx.params();
        if (params != null) {
            final int numParamsGiven = params.getChildCount() / 2 + 1;
            final int numParamsDefined = fundef.params.size();
            if (numParamsGiven != numParamsDefined) {
                final Token paramsToken = params.start;
                final int line = paramsToken.getLine();
                final int column = paramsToken.getCharPositionInLine() + 1;
                semanticErrors.add(String.format("Error: number of params given (%d) doesn't match function definition (%d) at (%d, %d)",
                        numParamsGiven, numParamsDefined, line, column));
            }
            for (int i=0; i < params.getChildCount(); i+=2) {
                final Expression param = visit(params.getChild(i));
                parameters.add(param);
            }
        }
        return new FunctionCall(id, parameters);
    }

    @Override
    public Expression visitNumber(final ApgParser.NumberContext ctx) {
        final String numText = ctx.NUM().getText();
        final int num = Integer.parseInt(numText);
        return new Number(num);
    }

    @Override
    public Expression visitBoolean(final ApgParser.BooleanContext ctx) {
        final String boolText = ctx.BOOL().getText();
        final boolean bool = Boolean.parseBoolean(boolText);
        return new BooleanVal(bool);
    }

    @Override
    public Expression visitParExpression(final ApgParser.ParExpressionContext ctx) {
        return visit(ctx.expr());
    }
}
