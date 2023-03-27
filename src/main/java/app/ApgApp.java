package app;

/**
 * Apg - Another Programming language by Gofferjé
 */
public class ApgApp {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: filename (codegen|eval)");
        } else {
            String fileName = args[0];
            String action = args[1];

            ProgramCompiler compiler = new ProgramCompiler(fileName, action);
            compiler.compile();
        }
    }

}
