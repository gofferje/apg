package apg.optimizing;

import java.util.ArrayList;
import java.util.List;

import apg.model.Addition;
import apg.model.And;
import apg.model.BooleanVal;
import apg.model.Declaration;
import apg.model.Equals;
import apg.model.Expression;
import apg.model.FunctionCall;
import apg.model.FunctionDefinition;
import apg.model.If;
import apg.model.Minus;
import apg.model.Multiplication;
import apg.model.Number;
import apg.model.Or;
import apg.model.Program;
import apg.model.ProgramVisitor;
import apg.model.Value;
import apg.model.Variable;

public class ProgramOptimizer implements ProgramVisitor<Expression> {

    @Override
    public Expression visit(final Program program) {
        program.functions.values().forEach(functionDefinition -> functionDefinition.accept(this));

        final List<Expression> optimizedExpressions = new ArrayList<>();
        for (final Expression expression: program.expressions) {
            final Expression optimizedExpr = expression.accept(this);
            optimizedExpressions.add(optimizedExpr);
        }
        program.expressions = optimizedExpressions;
        return null;
    }

    @Override
    public Expression visit(final Declaration declaration) {
        return null;
    }

    @Override
    public Expression visit(final FunctionDefinition functionDefinition) {
        functionDefinition.expression = functionDefinition.expression.accept(this);
        return null;
    }

    @Override
    public Expression visit(final Value value) {
        return value;
    }

    @Override
    public Expression visit(final Number number) {
        return number;
    }

    @Override
    public Expression visit(final BooleanVal booleanVal) {
        return booleanVal;
    }

    @Override
    public Expression visit(final Variable variable) {
        return variable;
    }

    @Override
    public Expression visit(final Addition addition) {
        addition.left = addition.left.accept(this);
        addition.right = addition.right.accept(this);
        if (addition.left instanceof Number && addition.right instanceof Number) {
            return new Number(((Number) addition.left).num + ((Number) addition.right).num);
        } else {
            return addition;
        }
    }

    @Override
    public Expression visit(final Minus minus) {
        minus.left = minus.left.accept(this);
        minus.right = minus.right.accept(this);
        if (minus.left instanceof Number && minus.right instanceof Number) {
            return new Number(((Number) minus.left).num - ((Number) minus.right).num);
        } else {
            return minus;
        }
    }

    @Override
    public Expression visit(final Multiplication multiplication) {
        multiplication.left = multiplication.left.accept(this);
        multiplication.right = multiplication.right.accept(this);
        if (multiplication.left instanceof Number && multiplication.right instanceof Number) {
            return new Number(((Number) multiplication.left).num * ((Number) multiplication.right).num);
        } else {
            return multiplication;
        }
    }

    @Override
    public Expression visit(final And and) {
        and.left = and.left.accept(this);
        and.right = and.right.accept(this);
        return and;
    }

    @Override
    public Expression visit(final Or or) {
        or.left = or.left.accept(this);
        or.right = or.right.accept(this);
        return or;
    }

    @Override
    public Expression visit(final Equals equals) {
        equals.left = equals.left.accept(this);
        equals.right = equals.right.accept(this);
        return equals;
    }

    @Override
    public Expression visit(final If ifExpr) {
        return ifExpr;
    }

    @Override
    public Expression visit(final FunctionCall functionCall) {
        final List<Expression> optimizedParams = new ArrayList<>();
        for (final Expression param: functionCall.params) {
            final Expression optimizedParam = param.accept(this);
            optimizedParams.add(optimizedParam);
        }
        functionCall.params = optimizedParams;
        return functionCall;
    }
}
