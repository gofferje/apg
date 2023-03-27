package apg.model;

public interface ProgramVisitor<T> {
    T visit(final Program program);
    T visit(final Declaration declaration);
    T visit(final FunctionDefinition functionDefinition);
    T visit(final Value value);
    T visit(final Number number);
    T visit(final BooleanVal booleanVal);
    T visit(final Variable variable);
    T visit(final Addition addition);
    T visit(final Minus minus);
    T visit(final Multiplication multiplication);
    T visit(final And and);
    T visit(final Or or);
    T visit(final Equals equals);
    T visit (final If ifExpr);
    T visit(final FunctionCall functionCall);
}
