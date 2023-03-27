package apg.parsing;

import java.util.ArrayList;
import java.util.List;

import apg.ApgBaseVisitor;
import apg.ApgParser;
import apg.model.Program;

public class AntlrToProgram extends ApgBaseVisitor<Program> {

    public List<String> semanticErrors;
    @Override
    public Program visitProgram(final ApgParser.ProgramContext ctx) {
        Program prog = new Program();
        semanticErrors = new ArrayList<>();
        AntlrToExpression exprVisitor = new AntlrToExpression(prog.variables.keySet(), prog.functions, semanticErrors);
        AntlrToDeclaration declVisitor = new AntlrToDeclaration(prog.variables.keySet(), semanticErrors);
        AntlrToFunctionDefinition funDefVisitor = new AntlrToFunctionDefinition(prog.functions, exprVisitor, semanticErrors);
        for(int i=0; i < ctx.getChildCount(); i++) {
            if (i == ctx.getChildCount() - 1) {
                // EOF
            } else if (ctx.getChild(i) instanceof ApgParser.DeclarationContext) {
                prog.addDeclaration(declVisitor.visit(ctx.getChild(i)));
            } else if (ctx.getChild(i) instanceof ApgParser.FunctiondefinitionContext) {
                prog.addFunctionDefinition(funDefVisitor.visit(ctx.getChild(i)));
            } else {
                prog.addExpression(exprVisitor.visit(ctx.getChild(i)));
            }
        }
        return prog;
    }
}
