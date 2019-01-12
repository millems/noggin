package software.amazon.noggin.util;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Names {
    private static final Pattern NON_ALPHANUM = Pattern.compile("[^A-Za-z0-9]");

    private Names() {}

    public static String toJavaClassName(String input) {
        return Stream.of(NON_ALPHANUM.split(input))
                     .map(Names::capitalize)
                     .collect(Collectors.joining());
    }

    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase(Locale.US) + input.substring(1);
    }
}
