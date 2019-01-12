package software.amazon.noggin.runtime.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Validate {
    public static <T> T paramNotNull(T param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(paramName + " most not be null.");
        }
        return param;
    }

    @SafeVarargs
    public static <T> T notNull(T input, String message, Supplier<Object>... args) {
        Validate.isTrue(input != null, message, args);
        return input;
    }

    @SafeVarargs
    public static void isTrue(boolean condition, String message, Supplier<Object>... args) {
        if (!condition) {
            throw new IllegalStateException(String.format(message, Stream.of(args).map(Supplier::get).toArray(Object[]::new)));
        }
    }

    @SafeVarargs
    public static <T extends Collection> T notEmpty(T input, String message, Supplier<Object>... args) {
        Validate.isTrue(!input.isEmpty(), message, args);
        return input;
    }

    @SafeVarargs
    public static <T, U> U isInstanceOf(T input, Class<U> type, String message, Supplier<Object>... args) {
        Validate.isTrue(type.isAssignableFrom(input.getClass()), message, args);
        return type.cast(input);
    }


}
