/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.clp.handler;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.Trees;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import net.steelswing.clp.annotation.Define;
import net.steelswing.clp.annotation.IfDefine;
import static net.steelswing.clp.handler.ReflectUtil.getField;

/**
 * File: ClangProcessor.java
 * Created on 28.12.2021, 10:35:22
 *
 * @author LWJGL2
 */
@SupportedAnnotationTypes("net.steelswing.clp.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ClangProcessor extends AbstractProcessor {

    private List<String> defines = new ArrayList<>();

    private CLangTranslator translator;
    private TreeMaker treeMaker;
    private Names names;
    private Trees trees;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        translator = new CLangTranslator(this, treeMaker, names, messager);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(Define.class)) {
            Define annotation = e.getAnnotation(Define.class);
            if (annotation.value() != null) {
                defines.addAll(Arrays.asList(annotation.value()));
            }
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(IfDefine.class)) {
            IfDefine annotation = e.getAnnotation(IfDefine.class);
            if (e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR) {
                boolean contains = false;
                
                for (String string : annotation.value()) {
                    if(contains = defines.contains(string)) {
                        contains = true;
                        break;
                    }
                }
                
                if (!contains) {
                    // remove method body
                    ((JCTree) trees.getTree(e)).accept(translator);
                }
            }
        }

        return false; // Indicate that the annotation is fully handled by us
    }

    public void note(String note) {
        System.err.println(note);
        processingEnv.getMessager().printMessage(Kind.NOTE, note);
    }

    private void debug(String message) {
        note(message);
    }

    public class ReplaceOnAnalyzeTaskListener implements TaskListener {

        private final JavaCompiler compiler;
        private boolean replaced;

        private ReplaceOnAnalyzeTaskListener(Context context) {
            this.compiler = JavaCompiler.instance(context);
        }

        @Override
        public void started(TaskEvent e) {
            if (e.getKind() == TaskEvent.Kind.ANALYZE && !replaced) {
                // We replace the compilation steps at this point because we have a different Context
                Context context = getField(compiler, "delegateCompiler.context");

                // Replace the Attribute step to remove errors when encoutering overloaded operators
//                ReflectUtil.<Map>getField(context, "ht").values().remove(getField(compiler, "delegateCompiler.attr"));
//                JOpsAttr attr2 = new JOpsAttr(context);
//                setField(compiler, "delegateCompiler.attr", attr2);
//
                // Replace the TransTypes step to "desugar" operators into method calls.
//                ReflectUtil.<Map>getField(context, "ht").values().remove(getField(compiler, "delegateCompiler.transTypes"));
//                CLangTransTypes transTypes2 = new CLangTransTypes(context, ClangProcessor.this);
//                setField(compiler, "delegateCompiler.transTypes", transTypes2);
                replaced = true;
            }
        }

        @Override
        public void finished(TaskEvent e) {
        }
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
