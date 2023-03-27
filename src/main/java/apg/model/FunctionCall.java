package apg.model;

import java.util.Arrays;
import java.util.List;

public class FunctionCall extends Expression {
    public String id;
    public List<Expression> params;

    public FunctionCall(String id, List<Expression> params) {
        this.id = id;
        this.params = params;
    }

    @Override
    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return id + "(" + Arrays.toString(params.toArray()).replace("[", "").replace("]", "") + ")";
    }
}
