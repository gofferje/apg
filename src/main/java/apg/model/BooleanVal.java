package apg.model;

public class BooleanVal extends Value {
    public boolean bool;

    public BooleanVal(boolean bool) {
        this.bool = bool;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this==obj) return true;
        if (obj==null) return false;
        if (obj instanceof BooleanVal) {
            return bool == ((BooleanVal)obj).bool;
        }
        return false;
    }

    @Override
    public String toString() {
        return Boolean.toString(bool);
    }

    @Override
    public String typeOf() {
        return "BOOL";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
