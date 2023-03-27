package apg.model;

public class Number extends Value {
    public int num;

    public Number(int num) {
        this.num = num;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this==obj) return true;
        if (obj==null) return false;
        if (obj instanceof Number) {
            return num == ((Number)obj).num;
        }
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(num);
    }

    @Override
    public String typeOf() {
        return "INT";
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
