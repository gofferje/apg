package apg.model;

import java.util.Map;

public class FunctionDefinition {
    public String id;
    public Map<String, String> params;
    public Map<Integer, String> paramOrder;
    public String type;
    public Expression expression;

    public FunctionDefinition(String id, Map<String, String> params, Map<Integer, String> paramOrder, String type, Expression expression) {
        this.id = id;
        this.params = params;
        this.paramOrder = paramOrder;
        this.type = type;
        this.expression = expression;
    }

    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FunctionDefinition {" +
               "id='" + id + '\'' +
               "params=" + params +
               "paramOrder=" + paramOrder +
               ", type='" + type + '\'' +
               ", expression=" + expression +
               '}';
    }
}
