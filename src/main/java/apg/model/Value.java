package apg.model;

public abstract class Value extends Expression {


    public abstract String typeOf();
    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
