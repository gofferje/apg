package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import apg.ApgLexer;
import apg.ApgParser;
import apg.codegen.ProgramCodeGenerator;
import apg.evaluating.ProgramEvaluator;
import apg.model.Expression;
import apg.model.Program;
import apg.model.Value;
import apg.optimizing.ProgramOptimizer;
import apg.parsing.AntlrToProgram;
import apg.parsing.SyntaxErrorListener;
import apg.typechecking.ProgramTypeChecker;

public class ProgramCompiler {

    private final String fileName;
    private final String action;
    Program program;
    public ProgramCompiler(final String fileName, final String action) {
        this.fileName = fileName;
        this.action = action;
    }

    public void compile() {
        final List<String> parseErrors = parse();

        if (parseErrors.isEmpty()) {

            final List<String> typeErrors = typeCheck();
            if (typeErrors.isEmpty()) {
                if ("codegen".equals(action)) {
                    generateCode().forEach(System.out::println);
                } else {
                    optimize();
                    evaluate().forEach(System.out::println);
                }
            } else {
                typeErrors.forEach(System.err::println);
            }
        } else {
            parseErrors.forEach(System.err::println);
        }
    }

    private List<String> parse() {
        final ApgParser parser = getParser();
        final ParseTree antlrAST = parser.prog();

        if (!SyntaxErrorListener.hasError) {
            final AntlrToProgram translator = new AntlrToProgram();
            program = translator.visit(antlrAST);
            return translator.semanticErrors;
        } else {
            return Arrays.asList("Syntax Error");
        }
    }

    private List<String> typeCheck() {
        final ProgramTypeChecker typeChecker = new ProgramTypeChecker(program);
        program.accept(typeChecker);
        return typeChecker.typeErrors();
    }

    private void optimize() {
        final ProgramOptimizer optimizer = new ProgramOptimizer();
        program.accept(optimizer);
    }

    private List<String> generateCode() {
        final ProgramCodeGenerator codeGenerator = new ProgramCodeGenerator();
        program.accept(codeGenerator);
        return codeGenerator.generatedCode();
    }

    private List<String> evaluate() {
        final ProgramEvaluator evaluator = new ProgramEvaluator(program);
        final List<String> evaluations = new ArrayList<>();
        for(Expression e : program.expressions) {
            final String input  = e.toString();
            final Value result = e.accept(evaluator);
            evaluations.add(input + " is " + result);
            evaluator.reset();
        }
        return evaluations;
    }

    private ApgParser getParser() {
        ApgParser parser = null;
        try {
            final CharStream input = CharStreams.fromFileName(fileName);
            final ApgLexer lexer = new ApgLexer(input);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new ApgParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new SyntaxErrorListener());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parser;
    }
}
