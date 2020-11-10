package method.time;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodTime {
    enum TimeInterval { MILLISECONDS, NANOSECONDS }
    TimeInterval interval() default TimeInterval.NANOSECONDS;
}
