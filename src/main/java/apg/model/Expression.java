package apg.model;

public abstract class Expression {
    public abstract <T> T accept(ProgramVisitor<T> visitor);

    public boolean isTerminal() {
        return false;
    }
}
