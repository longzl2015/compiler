package zl.compiler;


import org.springframework.stereotype.Service;
import zl.compiler.hot.CharSequenceJavaFileObject;
import zl.compiler.hot.HotClassLoader;
import zl.compiler.hot.MemJavaFileManager;

import javax.tools.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhoulong
 */
@Service
public class CompilerService {

    /**
     * classFullName的simple类名 必须和 code 中的类名一致。
     *
     * @param javaSources key classFullName; value code
     */
    public Map<String, byte[]> compiler(Map<String, String> javaSources) throws Exception {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(collector, null, null);
        MemJavaFileManager memFileManager = new MemJavaFileManager(standardFileManager);

        List<SimpleJavaFileObject> javaFileObjects = new ArrayList<>();
        javaSources.forEach(
                (k, v) -> javaFileObjects.add(new CharSequenceJavaFileObject(k, v))
        );

        JavaCompiler.CompilationTask task = compiler.getTask(null, memFileManager, collector, null, null, javaFileObjects);
        Boolean call = task.call();
        if (call) {
            // 调用成功，返回 class
            return memFileManager.getClassMap();
        }

        String s = simplifyDiagnostic(collector);
        //System.out.println(s);
        throw new Exception(s);

    }


    private String simplifyDiagnostic(DiagnosticCollector<JavaFileObject> collector) {
        StringBuilder builder = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
            builder.append("编译出错原因: ")
                    .append(diagnostic.getMessage(Locale.getDefault())).append("\n")
                    .append("  行数 ").append(diagnostic.getLineNumber()).append("\n")
                    .append("  列数 ").append(diagnostic.getColumnNumber()).append("\n")
                    .append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) throws Exception {

        Map<String, String> javaList = new HashMap<>();
        javaList.put("zl.compiler.Test", "package zl.compiler;\n" +
                "\n" +
                "public class Test {\n" +
                "\n" +
                "  public String work(){\n" +
                "DagExecution dagExecution = new DagExecution();" +
                "      System.out.println(new Test2().toString());\n" +
                "      System.out.println(\"Test\");\n" +
                "      return \"kkk\";\n" +
                "  }\n" +
                "}\n");
        javaList.put("zl.compiler.Test1", "package zl.compiler;\n" +
                "\n" +
                "public class Test1 {\n" +
                "\n" +
                "  public String work(){\n" +
                "      new Test().work();\n" +
                "      System.out.println(\"Test1\");\n" +
                "      return \"kkk\";\n" +
                "  }\n" +
                "}\n");
        javaList.put("zl.compiler.Test2", "package zl.compiler;\n" +
                "\n" +
                "public class Test2 {\n" +
                "\n" +
                "  public String work(){\n" +
                "      new Test1().work();\n" +
                "      System.out.println(\"Test2\");\n" +
                "\n" +
                "      return \"kkk\";\n" +
                "  }\n" +
                "}\n");


        CompilerService service = new CompilerService();
        Map<String, byte[]> map = service.compiler(javaList);


        ClassLoader loader = new HotClassLoader(map);
        Class<?> aClass = loader.loadClass("zl.compiler.Test2");

        Method work = aClass.getMethod("work");
        work.invoke(aClass.newInstance());

    }

}


