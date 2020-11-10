package processors.time;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import method.time.MethodTime;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Set;

@SupportedAnnotationTypes(value = {MethodTimeAnnotationProcessor.ANNOTATION_TYPE})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public final class MethodTimeAnnotationProcessor extends AbstractProcessor {

    public static final String ANNOTATION_TYPE = "method.time.MethodTime";
    private JavacProcessingEnvironment javacProcessingEnv;
    private static JCTree.JCExpression printlnExpression;
    private static JCTree.JCExpression outExpression;
    //    private Messager messager;
    private JavacElements utils;
    private TreeMaker maker;

    @Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
        this.javacProcessingEnv = (JavacProcessingEnvironment) procEnv;
        this.maker = TreeMaker.instance(javacProcessingEnv.getContext());
        this.utils = javacProcessingEnv.getElementUtils();
        outExpression = maker.Ident(utils.getName("System"));
        outExpression = maker.Select(outExpression, utils.getName("out"));
        printlnExpression = maker.Select(printlnExpression, utils.getName("println"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) return false;

        final Elements elements = javacProcessingEnv.getElementUtils();
        final TypeElement annotation = elements.getTypeElement(ANNOTATION_TYPE);
        final Set<? extends Element> methods = roundEnv.getElementsAnnotatedWith(annotation);

        for (final Element m : methods) {
            MethodTime time = m.getAnnotation(MethodTime.class);

            if (time != null) {
                JCTree blockNode = utils.getTree(m);
                if (blockNode instanceof JCTree.JCMethodDecl) {
                    final List<JCTree.JCStatement> oldStatements = ((JCTree.JCMethodDecl) blockNode).body.stats;
                    List<JCTree.JCStatement> resultStatements = List.nil();

                    // Add a start variable
                    JCTree.JCVariableDecl startTime = makeTimeStartVar(maker, utils, time);
                    resultStatements = resultStatements.append(startTime);

                    // Add a code from the original method
                    resultStatements = resultStatements.appendList(oldStatements);

                    // Add a finish time
                    JCTree.JCVariableDecl elapsedTime = makeElapsedTime(maker, utils, time, startTime);
                    resultStatements = resultStatements.append(elapsedTime);
                    resultStatements = resultStatements.appendList(outElapsedTime(maker, time, utils, elapsedTime));

                    ((JCTree.JCMethodDecl) blockNode).body.stats = resultStatements;
                }
            }
        }
        return true;
    }

    private JCTree.JCVariableDecl makeTimeStartVar(TreeMaker maker, JavacElements utils, MethodTime time) {
        JCTree.JCExpression currentTime = makeCurrentTime(maker, utils, time);
        String fieldName = "time_start_" + (int) (Math.random() * 10000);
        return maker.VarDef(maker.Modifiers(Flags.FINAL), utils.getName(fieldName), maker.TypeIdent(TypeTag.LONG), currentTime);
    }

    private JCTree.JCVariableDecl makeElapsedTime(TreeMaker maker, JavacElements utils, MethodTime time, JCTree.JCVariableDecl start) {
        JCTree.JCExpression currentTime = makeCurrentTime(maker, utils, time);
        JCTree.JCExpression elapsedTimeExpression = maker.Binary(JCTree.Tag.MINUS, currentTime, maker.Ident(start.name));
        String fieldName = "elapsed_time_" + (int) (Math.random() * 10000);
        return maker.VarDef(maker.Modifiers(Flags.FINAL), utils.getName(fieldName),
                maker.TypeIdent(TypeTag.LONG), elapsedTimeExpression);
    }

    private List<JCTree.JCStatement> outElapsedTime(TreeMaker maker, MethodTime time, JavacElements utils, JCTree.JCVariableDecl elapsedTime) {
        JCTree.JCExpression printfExpression = maker.Select(outExpression, utils.getName("printf"));
        List<JCTree.JCExpression> printlnArgs = List.nil();
        printlnArgs = printlnArgs.append(
                maker.Literal("\nMethod runtime: %d " + time.interval().toString().toLowerCase() + " \n")
        );
        printlnArgs = printlnArgs.append(maker.Ident(elapsedTime.name));
        JCTree.JCExpression print = maker.Apply(List.nil(), printfExpression, printlnArgs);
        JCTree.JCExpressionStatement stmt = maker.Exec(print);
        List<JCTree.JCStatement> stmts = List.nil();
        stmts = stmts.append(stmt);
        return stmts;
    }

    private JCTree.JCExpression makeCurrentTime(TreeMaker maker, JavacElements utils, MethodTime time) {
        JCTree.JCExpression exp = maker.Ident(utils.getName("System"));
        String methodName = "nanoTime"; // default;
        if (time.interval() == MethodTime.TimeInterval.MILLISECONDS) methodName = "currentTimeMillis";
        exp = maker.Select(exp, utils.getName(methodName));
        return maker.Apply(List.nil(), exp, List.nil());
    }

}
