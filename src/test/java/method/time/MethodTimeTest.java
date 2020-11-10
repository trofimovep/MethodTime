package method.time;

public class MethodTimeTest {
    /**
     * This test class is used for checking bytecode gotten when compiling with {@link method.time.MethodTime} annotation.
     * For compiling this this class execute this command:
     *  javac -cp src\main\out\MethodTime.jar src\test\java\method\time\MethodTimeTest.java
     *
     *  When it compile in compiled class {@see MethodTimeTest.class} it should be added methods for measuring method runtime
     * */

    public static void main(String[] args) {
        testMethod();
    }

    @MethodTime
    private static void testMethod() {
        long var = 8L;
        long var2 = 2 * var;
        System.out.println("var2: " + var2);
    }

    @MethodTime(interval = MethodTime.TimeInterval.MILLISECONDS)
    private static void testMethodMilliseconds() {
        long var = 8L;
        long var2 = 2 * var;
        System.out.println("var2: " + var2);
    }
}
