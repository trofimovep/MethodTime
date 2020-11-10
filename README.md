# MethodTime

###### Description
This lib makes the measurement of method runtime by getting difference between start method time and finish method time.
######Using
Add [a MethodTime.jar](/src/main/out/MethodTime.jar) as a library to your project, add annotation `@MethodTime` to method you want to measure and finally compile it. By default it measures in nanosecnds.
###### Example
If one wants to measure `testMethod()` in nanoseconds and `testMethodMilliseconds()` in milliseconds it should be coding like this:
```java
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
````

After compiling in compiled `.class` file will be added the measuring statements:
```java
    @MethodTime
    private static void testMethod() {
        long var0 = System.nanoTime();
        long var2 = 8L;
        long var4 = 2L * var2;
        System.out.println("var2: " + var4);
        long var6 = System.nanoTime() - var0;
        System.out.printf("\nMethod runtime: %d nanoseconds \n", var6);
    }

    @MethodTime(
        interval = TimeInterval.MILLISECONDS
    )
    private static void testMethodMilliseconds() {
        long var0 = System.currentTimeMillis();
        long var2 = 8L;
        long var4 = 2L * var2;
        System.out.println("var2: " + var4);
        long var6 = System.currentTimeMillis() - var0;
        System.out.printf("\nMethod runtime: %d milliseconds \n", var6);
    }
``` 
###### Attention
If one compiles with `Maven, Gradle, (etc.)` or `javac` it will be fine, but `IDE's` compiling tools may needs some optional settings.    
   
