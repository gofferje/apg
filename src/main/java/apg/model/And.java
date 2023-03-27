package apg.model;

public class And extends Expression {
    public Expression left;
    public Expression right;

    public And(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return left.toString() + " && " + right.toString();
    }
}
