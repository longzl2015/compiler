package zl.compiler.gc;

import java.util.Random;

public class GenData {

    private String name;
    private byte[] bytes;

    public GenData() {
        Random random = new Random();
        name = System.currentTimeMillis() + random.nextInt(10000) + "";
        bytes = new byte[1000];
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
