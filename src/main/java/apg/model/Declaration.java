package apg.model;

public class Declaration {
    public String id;
    public String type;
    public Value value;

    public Declaration(String id, String type, Value value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Declaration{" +
               "id='" + id + '\'' +
               ", type='" + type + '\'' +
               ", value=" + value +
               '}';
    }
}
