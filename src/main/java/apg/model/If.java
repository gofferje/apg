package apg.model;

public class If extends Expression {
    public Expression cond;
    public Expression thenExpr;
    public Expression elseExpr;

    public If(Expression cond, Expression thenExpr, Expression elseExpr) {
        this.cond = cond;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "if " + cond.toString() + " then " + thenExpr.toString() + " else " + elseExpr.toString();
    }
}
