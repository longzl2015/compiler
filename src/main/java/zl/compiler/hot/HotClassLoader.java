package zl.compiler.hot;

import java.util.Map;

/**
 * 自定义了一个 ClassLoader，用于加载 动态编译生成的 class
 * <p>
 * classMap 字段: 保存 class 字节码
 */
public class HotClassLoader extends ClassLoader {

    private Map<String, byte[]> classMap;

    public HotClassLoader(Map<String, byte[]> classMap) {
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
