package apg.typechecking;

public class TypeResult {
    private final String type;

    private final boolean correct;
    public TypeResult(final String type, final boolean correct) {
        this.type = type;
        this.correct = correct;
    }

    public TypeResult(final String type) {
        this(type, true);
    }

    public String type() {
        return type;
    }

    public boolean isInt() {
        return "INT".equals(type);
    }

    public boolean isBool() {
        return "BOOL".equals(type);
    }

    public boolean sameTypeAs(final TypeResult other) {
        return this.isInt() && other.isInt() || this.isBool() && other.isBool();
    }

    public boolean isCorrect() {
        return correct;
    }
}
