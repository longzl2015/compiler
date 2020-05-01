package zl.compiler.hot;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于管理 编译生成的 Class 类:
 * <p>
 * 自定义了一个 classMap 字段。该字段用于存储 编译生成的 Class 类
 */
public class MemJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final Map<String, CharSequenceJavaFileObject> javaFileObjectMap;

    public MemJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        javaFileObjectMap = new ConcurrentHashMap<>();
    }

    /**
     * 返回一个 JavaFileObject。在之后的阶段，生成的class字节码会被写入该 JavaFileObject 中。
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) {
        CharSequenceJavaFileObject javaFileObject = new CharSequenceJavaFileObject(qualifiedClassName, kind);
        javaFileObjectMap.put(qualifiedClassName, javaFileObject);
        return javaFileObject;
    }

    /**
     * 在 CompilationTask 执行完后，可以通过该方法获取 编译后的
     *
     * @return
     */
    public Map<String, byte[]> getClassMap() {
        Map<String, byte[]> map = new HashMap<>();
        javaFileObjectMap.forEach(
                (k, v) -> map.put(k, v.getCompiledBytes())
        );
        return map;
    }
}
