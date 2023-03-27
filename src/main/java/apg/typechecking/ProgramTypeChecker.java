package apg.typechecking;

import java.util.ArrayList;
import java.util.List;

import apg.evaluating.ProgramContext;
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

public class ProgramTypeChecker implements ProgramVisitor<TypeResult> {

    private final ProgramContext context;

    private final List<String> typeErrors;

    public ProgramTypeChecker(final Program program) {
        context = new ProgramContext(program);
        typeErrors = new ArrayList<>();
    }

    public List<String> typeErrors() {
        return typeErrors;
    }

    @Override
    public TypeResult visit(final Program program) {
        program.variables.values().forEach(declaration -> declaration.accept(this));

        program.functions.values().forEach(functionDefinition -> functionDefinition.accept(this));

        program.expressions.forEach(expression -> expression.accept(this));
        return new TypeResult("BOOL", true);
    }

    @Override
    public TypeResult visit(final Declaration declaration) {
        final TypeResult valueType = declaration.value.accept(this);
        if (!declaration.type.equals(valueType.type())) {
            // report type error
            final String typeError =
                    String.format("Declaration of variable '%s': Type of actual value (%s) does not match declared type %s",
                                  declaration.id, valueType.type(), declaration.type);
            typeErrors.add(typeError);
        }
        return valueType;
    }

    @Override
    public TypeResult visit(final FunctionDefinition functionDefinition) {
        context.paramTypes.putAll(functionDefinition.params);
        //
        final TypeResult functionBodyResult = functionDefinition.expression.accept(this);
        if (functionBodyResult.isCorrect() && !(functionBodyResult.type().equals(functionDefinition.type))) {
            // report type error
            final String typeError =
                    String.format("Function '%s': Function body type (%s) does not match declared result type %s",
                            functionDefinition.id, functionBodyResult.type(), functionDefinition.type);
            typeErrors.add(typeError);
        }
        context.clearParams();
        return functionBodyResult;
    }

    @Override
    public TypeResult visit(final Value value) {
        return new TypeResult(value.typeOf());
    }

    @Override
    public TypeResult visit(final Number number) {
        return new TypeResult(number.typeOf());
    }

    @Override
    public TypeResult visit(final BooleanVal booleanVal) {
        return new TypeResult(booleanVal.typeOf());
    }

    @Override
    public TypeResult visit(final Variable variable) {
        final String paramType = context.paramTypes.get(variable.id);
        if (paramType == null) {
            final String varType = context.variables.get(variable.id).value.typeOf();
            return new TypeResult(varType);
        } else {
            return new TypeResult(paramType);
        }
    }

    @Override
    public TypeResult visit(final Addition addition) {
        final TypeResult leftResult = addition.left.accept(this);
        final TypeResult rightResult = addition.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("INT", false);
        }
        if (leftResult.isInt() && rightResult.isInt()) {
            return new TypeResult("INT");
        } else {
            // report type error
            final String typeError;
            if (leftResult.isInt()) {
                typeError = String.format("Right operand of + should be of INT type (%s)", addition.right);
            } else {
                typeError = String.format("Left operand of + should be of INT type (%s)", addition.left);
            }
            typeErrors.add(typeError);
            return new TypeResult("INT", false);
        }
    }

    @Override
    public TypeResult visit(final Minus minus) {
        final TypeResult leftResult = minus.left.accept(this);
        final TypeResult rightResult = minus.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("INT", false);
        }
        if (leftResult.isInt() && rightResult.isInt()) {
            return new TypeResult("INT");
        } else {
            // report type error
            final String typeError;
            if (leftResult.isInt()) {
                typeError = String.format("Right operand of - should be of INT type (%s)", minus.right);
            } else {
                typeError = String.format("Left operand of - should be of INT type (%s)", minus.left);
            }
            typeErrors.add(typeError);
            return new TypeResult("INT", false);
        }
    }

    @Override
    public TypeResult visit(final Multiplication multiplication) {
        final TypeResult leftResult = multiplication.left.accept(this);
        final TypeResult rightResult = multiplication.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("INT", false);
        }
        if (leftResult.isInt() && rightResult.isInt()) {
            return new TypeResult("INT");
        } else {
            // report type error
            final String typeError;
            if (leftResult.isInt()) {
                typeError = String.format("Right operand of * should be of INT type (%s)", multiplication.right);
            } else {
                typeError = String.format("Left operand of * should be of INT type (%s)", multiplication.left);
            }
            typeErrors.add(typeError);
            return new TypeResult("INT", false);
        }
    }

    @Override
    public TypeResult visit(final And and) {
        final TypeResult leftResult = and.left.accept(this);
        final TypeResult rightResult = and.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("BOOL", false);
        }
        if (leftResult.isBool() && rightResult.isBool()) {
            return new TypeResult("BOOL");
        } else {
            // report type error
            final String typeError;
            if (leftResult.isBool()) {
                typeError = String.format("Right operand of && should be of BOOL type (%s)", and.right);
            } else {
                typeError = String.format("Left operand of && should be of BOOL type (%s)", and.left);
            }
            typeErrors.add(typeError);
            return new TypeResult("BOOL", false);
        }
    }

    @Override
    public TypeResult visit(final Or or) {
        final TypeResult leftResult = or.left.accept(this);
        final TypeResult rightResult = or.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("BOOL", false);
        }
        if (leftResult.isBool() && rightResult.isBool()) {
            return new TypeResult("BOOL");
        } else {
            // report type error
            final String typeError;
            if (leftResult.isBool()) {
                typeError = String.format("Right operand of && should be of BOOL type (%s)", or.right);
            } else {
                typeError = String.format("Left operand of && should be of BOOL type (%s)", or.left);
            }
            typeErrors.add(typeError);
            return new TypeResult("BOOL", false);
        }
    }

    @Override
    public TypeResult visit(final Equals equals) {
        final TypeResult leftResult = equals.left.accept(this);
        final TypeResult rightResult = equals.right.accept(this);
        if (!leftResult.isCorrect() || !rightResult.isCorrect()) {
            return new TypeResult("BOOL", false);
        }
        if (leftResult.sameTypeAs(rightResult)) {
            return new TypeResult("BOOL");
        } else {
            // report type error
            final String typeError = String.format("Left operand (%s) and right operand (%s) of == are not the same type",
                    leftResult.type(), rightResult.type());
            typeErrors.add(typeError);
            return new TypeResult("BOOL", false);
        }
    }

    @Override
    public TypeResult visit(final If ifExpr) {
        final TypeResult condResult = ifExpr.cond.accept(this);
        if (!condResult.isBool()) {
            // report type error
            final String typeError = String.format("'if' condition must be BOOL but is of type %s", condResult.type());
            typeErrors.add(typeError);
            return new TypeResult("BOOL", false);
        }
        final TypeResult thenResult = ifExpr.thenExpr.accept(this);
        final TypeResult elseResult = ifExpr.elseExpr.accept(this);
        if (!thenResult.isCorrect() || !elseResult.isCorrect()) {
            return new TypeResult("BOOL", false);
        }
        if (thenResult.sameTypeAs(elseResult)) {
            return new TypeResult(thenResult.type());
        } else {
            // report type error
            final String typeError = String.format("'then' expression (%s) and 'else' expression (%s) are not the same type",
                    thenResult.type(), elseResult.type());
            typeErrors.add(typeError);
            return new TypeResult("BOOL", false);
        }
    }

    @Override
    public TypeResult visit(final FunctionCall functionCall) {
        final FunctionDefinition funDef = context.functions.get(functionCall.id);
        int paramIndex= 0;
        boolean isCorrect = true;
        for (final Expression expression : functionCall.params) {
            final String paramName = funDef.paramOrder.get(paramIndex);
            final TypeResult paramTypeResult = expression.accept(this);
            if (!paramTypeResult.type().equals(funDef.params.get(paramName))) {
                // report type error
                final String typeError =
                        String.format("Call to function '%s': Parameter '%s': Type of actual parameter (%s) does not match type of formal "
                                + "parameter (%s)", funDef.id, paramName, paramTypeResult.type(), funDef.params.get(paramName));
                typeErrors.add(typeError);
                isCorrect = false;
            }
            context.paramTypes.put(paramName, paramTypeResult.type());
            isCorrect = isCorrect && paramTypeResult.isCorrect();
            paramIndex++;
        }

        return new TypeResult(funDef.type, isCorrect);
    }
}
