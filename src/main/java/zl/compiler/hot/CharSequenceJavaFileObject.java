package zl.compiler.hot;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

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
public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    /**
     * 用于保存源文件内容
     */
    private CharSequence content;
    /**
     * 用于保存 编译后的 class 文件
     */
    private ByteArrayOutputStream outPutStream;

    public CharSequenceJavaFileObject(String className, Kind kind) {
        super(URI.create("String:///" + className.replace('.', '/') + kind.extension), kind);
        content = null;
    }

    public CharSequenceJavaFileObject(String className, String source) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
        this.content = source;
    }

    /**
     * 在编译过程中，JavaCompiler 会先调用 getCharContent() 获取 java 源文件内容。
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        if (content == null) {
            throw new UnsupportedOperationException("getCharContent()");
        }
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

    public byte[] getCompiledBytes() {
        return outPutStream.toByteArray();
    }
}
