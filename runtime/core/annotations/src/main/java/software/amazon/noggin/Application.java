package software.amazon.noggin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Application {
    String name() default "noggin-application";
    NogginRuntime runtime() default NogginRuntime.LAMBDA;
}
