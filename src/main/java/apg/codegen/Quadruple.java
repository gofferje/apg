package apg.codegen;

public class Quadruple {
    public String operator;
    public String argument1;
    public String argument2;
    public String result;

    Quadruple(String operator, String argument1, String argument2, String result) {
        this.operator = operator;
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.result = result;
    }

    Quadruple(String argument1, String result) {
        this.argument1 = argument1;
        this.result = result;
    }

    Quadruple(String result) {
        this.result = result;
    }

    String toCode() {
        return String.format("   %s = %s %s %s", result, argument1, operator == null ? "" : operator, argument2 == null ? "" : argument2);
    }
}
