public class LambdaTest {
    public void test() {
        Interface1 i1 = (s) -> System.out.println(s);

        i1.method1("abc");
    }
}
