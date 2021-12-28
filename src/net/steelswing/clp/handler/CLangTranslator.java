/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.clp.handler;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import net.steelswing.clp.JCLiterals;

/**
 * File: Da.java
 * Created on 28.12.2021, 11:29:52
 *
 * @author LWJGL2
 */
public class CLangTranslator extends TreeTranslator {

    private final ClangProcessor main;
    private TreeMaker treeMaker;
    private Names names;
    private Messager messager;

    private final List<JCTree.JCStatement> emptyStatements = List.nil();
    private final List<JCTree.JCExpression> emptyExpressions = List.nil();

    public CLangTranslator(ClangProcessor main, TreeMaker treeMaker, Names names, Messager messager) {
        this.main = main;
        this.treeMaker = treeMaker;
        this.names = names;
        this.messager = messager;

    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        JCTree.JCBlock jcBlock = jcMethodDecl.getBody();
        jcBlock.stats = emptyStatements;

        jcBlock.stats = jcBlock.getStatements().append(getThrowStatement());
//        JCTree.JCStatement aNull = treeMaker.Return(treeMaker.Literal(TypeTag.VOID, null));
//        jcBlock.stats= jcBlock.getStatements().append(aNull);
//
        super.visitMethodDef(jcMethodDecl);
    }


    public JCTree.JCMethodDecl generateEqualMethod(JCTree.JCClassDecl classDecl) {
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = treeMaker.Ident(names.fromString("java"));
        returnType = treeMaker.Select(returnType, names.fromString("lang"));
        returnType = treeMaker.Select(returnType, names.fromString("String"));
        Name method = names.fromString("equal");
        JCTree.JCExpression ObjectExpr = treeMaker.Ident(names.fromString("java"));
        ObjectExpr = treeMaker.Select(ObjectExpr, names.fromString("lang"));
        ObjectExpr = treeMaker.Select(ObjectExpr, names.fromString("Object"));
        JCTree.JCVariableDecl param =
                treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER),
                        names.fromString("o"),
                        ObjectExpr,
                        null);
        param.pos = classDecl.pos;
        List<JCTree.JCVariableDecl> params = List.of(param);

        List<JCTree.JCStatement> statement = List.nil();
        JCTree.JCStatement aNull = treeMaker.Return(treeMaker.Literal(TypeTag.BOT, null));
        statement = statement.append(aNull);
        JCTree.JCBlock block = treeMaker.Block(0, statement);

        List<JCTree.JCTypeParameter> emptyParams = List.nil();
        List<JCTree.JCExpression> emptyExpressions = List.nil();

        JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(publicModifier, method, returnType, emptyParams, params, emptyExpressions, block, null);
        System.out.println(methodDecl);
        return methodDecl;
    }

    private JCTree.JCExpressionStatement getLogStatement() {
        return treeMaker.Exec(//
                treeMaker.Apply(//
                        List.of(memberAccess("java.lang.String")),//
                        memberAccess("java.lang.System.out.println"),//
                        List.of(JCLiterals.stringValue(treeMaker, "Hello"))
                )//
        );
    }

    private JCStatement getThrowStatement() throws UnsupportedOperationException {
        JCTree.JCStatement throwStaement = treeMaker.Throw(treeMaker.NewClass(
                null, emptyExpressions,
                buildExceptionClassExpression("java.lang.UnsupportedOperationException", treeMaker, names),
                List.of(JCLiterals.stringValue(treeMaker, "Method is not defined.")),
                null
        ));
        return treeMaker.Block(0, List.of(throwStaement));
    }

    private static JCTree.JCExpression buildExceptionClassExpression(String exceptionClass, TreeMaker factory, Names symbolsTable) {
        String[] parts = exceptionClass.split("\\.");
        JCTree.JCIdent identifier = factory.Ident(symbolsTable.fromString(parts[0]));
        JCTree.JCFieldAccess selector = null;
        for (int i = 1; i < parts.length; i++) {
            selector = factory.Select(selector == null ? identifier : selector, symbolsTable.fromString(parts[i]));
        }
        return selector == null ? identifier : selector;
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }

    private void printMessage(String method, String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, method + "-->" + message);
    }


}
