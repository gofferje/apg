package apg.evaluating;

import apg.model.Addition;
import apg.model.And;
import apg.model.BooleanVal;
import apg.model.Declaration;
import apg.model.Equals;
import apg.model.Expression;
import apg.model.If;
import apg.model.Minus;
import apg.model.ProgramVisitor;
import apg.model.FunctionCall;
import apg.model.FunctionDefinition;
import apg.model.Multiplication;
import apg.model.Number;
import apg.model.Or;
import apg.model.Program;
import apg.model.Value;
import apg.model.Variable;

public class ProgramEvaluator implements ProgramVisitor<Value> {

    private final ProgramContext context;

    public ProgramEvaluator(final Program program) {
        context = new ProgramContext(program);
    }

    @Override
    public Value visit(final Program program) {
        return null;
    }

    @Override
    public Value visit(final Declaration declaration) {
        return null;
    }

    @Override
    public Value visit(final FunctionDefinition functionDefinition) {
        return null;
    }

    @Override
    public Value visit(final Value value) {
        return null;
    }

    @Override
    public Value visit(final Number number) {
        return number;
    }

    @Override
    public Value visit(final BooleanVal booleanVal) {
        return booleanVal;
    }

    @Override
    public Value visit(final Variable variable) {
        final Value paramValue = context.params.get(variable.id);
        if (paramValue == null) {
            return context.variables.get(variable.id).value;
        } else {
            return paramValue;
        }
    }

    @Override
    public Value visit(final Addition addition) {
        final Value left = addition.left.accept(this);
        final Value right = addition.right.accept(this);
        final int leftAsInt = ((Number)left).num;
        final int rightAsInt = ((Number)right).num;
        return new Number(leftAsInt + rightAsInt);
    }

    @Override
    public Value visit(final Minus minus) {
        final Value left = minus.left.accept(this);
        final Value right = minus.right.accept(this);
        final int leftAsInt = ((Number)left).num;
        final int rightAsInt = ((Number)right).num;
        return new Number(leftAsInt - rightAsInt);
    }

    @Override
    public Value visit(final Multiplication multiplication) {
        final Value left = multiplication.left.accept(this);
        final Value right = multiplication.right.accept(this);
        final int leftAsInt = ((Number)left).num;
        final int rightAsInt = ((Number)right).num;
        return new Number(leftAsInt * rightAsInt);
    }

    @Override
    public Value visit(final And and) {
        final Value left = and.left.accept(this);
        final Value right = and.right.accept(this);
        final boolean leftAsBoolean = ((BooleanVal)left).bool;
        final boolean rightAsBoolean = ((BooleanVal)right).bool;
        return new BooleanVal(leftAsBoolean && rightAsBoolean);
    }

    @Override
    public Value visit(final Or or) {
        final Value left = or.left.accept(this);
        final Value right = or.right.accept(this);
        final boolean leftAsBoolean = ((BooleanVal)left).bool;
        final boolean rightAsBoolean = ((BooleanVal)right).bool;
        return new BooleanVal(leftAsBoolean || rightAsBoolean);
    }

    @Override
    public Value visit(final Equals equals) {
        final Value left = equals.left.accept(this);
        final Value right = equals.right.accept(this);
        return new BooleanVal(left.equals(right));
    }

    @Override
    public Value visit(final If ifExpr) {
        final BooleanVal cond = (BooleanVal) ifExpr.cond.accept(this);
        if (cond.bool) {
            return ifExpr.thenExpr.accept(this);
        } else {
            return ifExpr.elseExpr.accept(this);
        }
    }

    @Override
    public Value visit(final FunctionCall functionCall) {
        final FunctionDefinition funDef = context.functions.get(functionCall.id);
        int paramIndex= 0;
        for (final Expression expression : functionCall.params) {
            final Value paramValue = expression.accept(this);
            context.params.put(funDef.paramOrder.get(paramIndex), paramValue);
            paramIndex++;
        }
        return funDef.expression.accept(this);
    }

    public void reset() {
        context.clearParams();
    }
}
