package apg.evaluating;

import java.util.HashMap;
import java.util.Map;

import apg.model.Declaration;
import apg.model.FunctionDefinition;
import apg.model.Program;
import apg.model.Value;

public class ProgramContext {
    public final Map<String, Declaration> variables;

    public final Map<String, FunctionDefinition> functions;

    public final Map<String, Value> params;

    public final Map<String, String> paramTypes;

    public ProgramContext(final Program program) {
        this.variables = program.variables;
        this.functions = program.functions;
        this.params = new HashMap<>();
        this.paramTypes = new HashMap<>();
    }

    public void clearParams() {
        params.clear();
        paramTypes.clear();
    }
}
