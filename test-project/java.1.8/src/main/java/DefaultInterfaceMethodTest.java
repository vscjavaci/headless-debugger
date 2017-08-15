public class DefaultInterfaceMethodTest implements Interface1, Interface2 {

    @Override
    public void method2() {
    }

    @Override
    public void method1(String str) {
    }

    //DefaultInterfaceMethodTest won't compile without having it's own log() implementation
    @Override
    public void log(String str) {
        System.out.println("DefaultInterfaceMethodTest logging::" + str);
        Interface1.print("abc");
    }

    public void test() {
        log("Hello world!");
    }
}