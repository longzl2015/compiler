package zl.compiler.gc;

public class Main {

    public static void main(String[] args) {
        while (true) {
            add();
        }
    }

    private static void add() {
        GenData gd = new GenData();
        Cache.add(gd.getName(), gd.getBytes());
    }
}
