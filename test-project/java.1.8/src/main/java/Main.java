import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

class Main {
    public static void main(String []args) {
        System.out.println("test begin");
        new DefaultInterfaceMethodTest().test();
        new LambdaTest().test();
        new StreamTest().test();
        System.out.println("test end");
    }
}