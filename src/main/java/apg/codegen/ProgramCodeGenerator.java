package apg.codegen;

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

public class ProgramCodeGenerator implements ProgramVisitor<Quadruple> {

    private final List<String> generatedCode = new ArrayList<>();

    private int temporaryIndex;
    private int labelIndex;

    public List<String> generatedCode() {
        return generatedCode;
    }

    @Override
    public Quadruple visit(final Program program) {
        temporaryIndex = 0;
        labelIndex = 0;
        for (final FunctionDefinition functionDefinition : program.functions.values()) {
            functionDefinition.accept(this);
        }
        for (final Declaration declaration : program.variables.values()) {
            declaration.accept(this);
        }
        for (final Expression expression : program.expressions) {
            final Quadruple result = expression.accept(this);
            if (result != null) {
                generatedCode.add(String.format("   show %s", result.result));
            }
        }
        return null;
    }

    @Override
    public Quadruple visit(final Declaration declaration) {
        Quadruple result = new Quadruple(declaration.value.toString(), declaration.id);
        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final FunctionDefinition functionDefinition) {
        final String proceedLabel = generateNewLabel();
        generatedCode.add(String.format("   goto %s", proceedLabel));
        generatedCode.add(String.format("fun: %s", functionDefinition.id));
        // pop params
        for (int paramIndex = functionDefinition.paramOrder.keySet().size() - 1; paramIndex >=0; paramIndex--) {
            final String paramName = functionDefinition.paramOrder.get(paramIndex);
            final Quadruple assignParam = new Quadruple("pop", paramName);
            generatedCode.add(assignParam.toCode());
        }
        final Quadruple bodyResult = functionDefinition.expression.accept(this);
        generatedCode.add(String.format("   return %s", bodyResult.result));
        generatedCode.add(proceedLabel + ":");

        return bodyResult;
    }

    @Override
    public Quadruple visit(final Value value) {
        return new Quadruple(value.toString());
    }

    @Override
    public Quadruple visit(final Number number) {
        return new Quadruple(number.toString());
    }

    @Override
    public Quadruple visit(final BooleanVal booleanVal) {
        return new Quadruple(booleanVal.toString());
    }

    @Override
    public Quadruple visit(final Variable variable) {
        return new Quadruple(variable.id);
    }

    @Override
    public Quadruple visit(final Addition addition) {
        final String left = addition.left.accept(this).result;
        final String right = addition.right.accept(this).result;
        final Quadruple result = new Quadruple("+", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final Minus minus) {
        final String left = minus.left.accept(this).result;
        final String right = minus.right.accept(this).result;
        final Quadruple result = new Quadruple("-", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final Multiplication multiplication) {
        final String left = multiplication.left.accept(this).result;
        final String right = multiplication.right.accept(this).result;
        final Quadruple result = new Quadruple("*", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final And and) {
        final String left = and.left.accept(this).result;
        final String right = and.right.accept(this).result;
        final Quadruple result = new Quadruple("&&", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final Or or) {
        final String left = or.left.accept(this).result;
        final String right = or.right.accept(this).result;
        final Quadruple result = new Quadruple("||", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final Equals equals) {
        final String left = equals.left.accept(this).result;
        final String right = equals.right.accept(this).result;
        final Quadruple result = new Quadruple("==", left, right, generateNewTemporary());

        generatedCode.add(result.toCode());
        return result;
    }

    @Override
    public Quadruple visit(final If ifExpr) {
        Quadruple cond = ifExpr.cond.accept(this);
        final String elseLabel = generateNewLabel();
        generatedCode.add(String.format("   ifFalse %s goto %s",cond.result, elseLabel));
        Quadruple thenCode = ifExpr.thenExpr.accept(this);
        final String resultTemporary = generateNewTemporary();
        Quadruple result = new Quadruple(thenCode.result, resultTemporary);
        generatedCode.add(result.toCode());
        final String proceedLabel = generateNewLabel();
        generatedCode.add(String.format("   goto %s", proceedLabel));
        generatedCode.add(elseLabel + ":");
        Quadruple elseCode = ifExpr.elseExpr.accept(this);
        result = new Quadruple(elseCode.result, resultTemporary);
        generatedCode.add(result.toCode());
        generatedCode.add(proceedLabel + ":");
        return result;
    }

    @Override
    public Quadruple visit(final FunctionCall functionCall) {
        for (final Expression param: functionCall.params) {
            final Quadruple paramResult = param.accept(this);
            generatedCode.add(String.format("   push %s", paramResult.result));
        }
        final String callResult = generateNewTemporary();
        generatedCode.add(String.format("   %s = call %s", callResult, functionCall.id));
        return new Quadruple(callResult);
    }

    private String generateNewTemporary() {
        temporaryIndex++;
        return String.format("t%d", temporaryIndex);
    }

    private String generateNewLabel() {
        labelIndex++;
        return String.format("L%d", labelIndex);
    }
}
