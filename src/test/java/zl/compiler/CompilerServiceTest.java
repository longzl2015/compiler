package zl.compiler;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import zl.compiler.hot.HotClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class CompilerServiceTest {

    @Test
    public void compiler() throws Exception {

        String s = FileUtils.readFileToString(new File("src/main/resources/TestA.java"), StandardCharsets.UTF_8);

        CompilerService service = new CompilerService();
        HashMap<String, String> map = new HashMap<>();
        map.put("zl.compiler.TestA", s);
        long start = System.currentTimeMillis();
        Map<String, byte[]> byteMap = service.compiler(map);
        System.out.println("编译时间ms:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        ClassLoader loader1 = new HotClassLoader(byteMap);
        Class<?> aClass1 = loader1.loadClass("zl.compiler.TestA");
        System.out.println("加载时间ms:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        Object newInstance = aClass1.newInstance();
        System.out.println("实例化时间ms:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        Method work1 = aClass1.getMethod("getAsdfe");
        work1.invoke(newInstance);
        System.out.println("调用时间ms:" + (System.currentTimeMillis() - start));


        int count = 0;
        while (count++ < 1000) {
            start = System.currentTimeMillis();
            work1.invoke(newInstance);
            System.out.println("调用时间ms:" + (System.currentTimeMillis() - start));
        }

    }
}