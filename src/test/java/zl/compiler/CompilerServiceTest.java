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
        System.out.println(s);

        CompilerService service = new CompilerService();
        HashMap<String, String> map = new HashMap<>();
        map.put("zl.compiler.TestA", s);
        Map<String, byte[]> byteMap = service.compiler(map);

        ClassLoader loader1 = new HotClassLoader(byteMap);
        Class<?> aClass1 = loader1.loadClass("zl.compiler.TestA");

        Method work1 = aClass1.getMethod("getAsdfe");

        Thread.currentThread().setContextClassLoader(loader1);

        int count = 0;
        while (count++ < 1000) {
            Object o = work1.invoke(aClass1.newInstance());
            System.out.println(o);
        }


    }
}