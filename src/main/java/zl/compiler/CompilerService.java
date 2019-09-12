package zl.compiler;


import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoulong
 */
@Service
public class CompilerService {


    /**
     * classFullName的simple类名 必须和 code 中的类名一致。
     *
     * @param classFullName
     * @param code
     * @return
     */
    public Map<String, byte[]> compiler(String classFullName, String code) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 通过Diagnostic实例获取编译过程中出错的行号、位置以及错误原因等信息。
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        MyJavaFileManager javaFileManager = new MyJavaFileManager(compiler.getStandardFileManager(collector, null, null));

        SimpleJavaFileObject fileObject = new CharSequenceJavaFileObject(classFullName, code);


        JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, collector, null, null, Collections.singletonList(fileObject));
        Boolean call = task.call();
        if (call) {
            Map<String, byte[]> map = new HashMap<>();
            Map<String, CharSequenceJavaFileObject> classMap = javaFileManager.classMap;
            classMap.forEach(
                    (k, v) -> map.put(k, v.getCompiledBytes())
            );
            return map;
        }

        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
            System.out.println(diagnostic.getCode());
            System.out.println(diagnostic.getLineNumber());
            System.out.println(diagnostic.getColumnNumber());
            System.out.println(diagnostic.getMessage(Locale.getDefault()));
        }

        throw new Exception("");

    }

    public static void main(String[] args) throws Exception {

        CompilerService service = new CompilerService();
        Map<String, byte[]> map = service.compiler("zl.compiler.Tesssfs", "package zl.compiler;\n" +
                "\n" +
                "public class Tessss {\n" +
                "\n" +
                "  public String work(){\n" +
                "      System.out.println(\"ddd\");\n" +
                "      return \"kkk\";\n" +
                "  }\n" +
                "}\n");


        ClassLoader loader = new CompileClassLoader(map);
        Class<?> aClass = loader.loadClass("zl.compiler.Tessss");

        Method work = aClass.getMethod("work");
        work.invoke(aClass.newInstance());

    }

}

/**
 * 自定义了一个 ClassLoader，用于加载 动态编译生成的 class
 * <p>
 * classMap 字段: 保存 class 字节码
 */
class CompileClassLoader extends ClassLoader {

    private Map<String, byte[]> classMap;

    CompileClassLoader(Map<String, byte[]> classMap) {
        this.classMap = classMap;
    }

    /**
     * 在调用 loadClass() 时，
     * 如果父类无法加载该类，则会使用 findClass() 加载类。
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classMap.get(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        throw new ClassNotFoundException("源文件中找不到类:" + name);
    }
}


/**
 * 用于管理 编译生成的 Class 类:
 * <p>
 * 自定义了一个 classMap 字段。该字段用于存储 编译生成的 Class 类
 */
class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    Map<String, CharSequenceJavaFileObject> classMap;

    MyJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classMap = new ConcurrentHashMap<>();
    }

    /**
     *
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) {
        CharSequenceJavaFileObject javaFileObject = new CharSequenceJavaFileObject(qualifiedClassName, kind);
        classMap.put(qualifiedClassName, javaFileObject);
        return javaFileObject;
    }
}

/**
 * 该类 有一个 kind 字段，分为以下四种
 * - '.class'
 * - '.java'
 * - '.html'
 * - other
 * <p>
 * 当为 '.class' 类型时，会将 class 内容存入 outPutStream 字段中
 * 档位 '.java'  类型时，会将 java 源代码存入 content 字段中
 */
class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    /**
     * 用于保存源文件内容
     */
    private CharSequence content;
    /**
     * 用于保存 编译后的 class 文件
     */
    private ByteArrayOutputStream outPutStream;

    CharSequenceJavaFileObject(String className, Kind kind) {
        super(URI.create("String:///" + className.replace('.', '/') + kind.extension), kind);
        content = null;
    }

    CharSequenceJavaFileObject(String className, String source) {
        super(URI.create("string:///" + className.replace('.', '/') + javax.tools.JavaFileObject.Kind.SOURCE.extension),
                javax.tools.JavaFileObject.Kind.SOURCE);
        this.content = source;
    }

    /**
     * 在编译过程中，JavaCompiler 会先调用 getCharContent() 获取 java 源文件内容。
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.content;
    }

    /**
     * JavaCompiler.generate 后，会调用该方法. 因此必须实现该方法。
     */
    @Override
    public OutputStream openOutputStream() {
        outPutStream = new ByteArrayOutputStream();
        return outPutStream;
    }

    byte[] getCompiledBytes() {
        return outPutStream.toByteArray();
    }
}