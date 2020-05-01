package zl.compiler.gc;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static Map<String, byte[]> list = new HashMap<>();

    public static void add(String ss, byte[] bytes) {
        list.put(ss, bytes);
    }

}
