package apg.model;

public class Variable extends Expression {
    public String id;

    public Variable(String id) {
        this.id = id;
    }

    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String toString() {
        return id;
    }
}
