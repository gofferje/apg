package apg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    public Map<String, Declaration> variables;

    public Map<String, FunctionDefinition> functions;
    public List<Expression> expressions;

    public Program() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.expressions = new ArrayList<>();
    }

    public void addDeclaration(Declaration variable) {
        this.variables.put(variable.id, variable);
    }

    public void addFunctionDefinition(FunctionDefinition funDef) {
        this.functions.put(funDef.id, funDef);
    }

    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }

    public <T> T accept(final ProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
