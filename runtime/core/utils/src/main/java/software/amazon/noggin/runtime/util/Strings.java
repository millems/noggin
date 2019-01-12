package software.amazon.noggin.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Strings {
    private Strings() {}

    public static List<String> split(String input, char delimeter) {
        List<String> result = new ArrayList<>();

        int start = 0;

        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == delimeter) {
                result.add(input.substring(start, i));
                start = i + 1;
            }
        }

        result.add(input.substring(start));

        return Collections.unmodifiableList(result);
    }

    public static String trim(String input, String toTrim) {
        if (input.equals(toTrim)) {
            return "";
        }

        int startIndex = input.startsWith(toTrim) ? toTrim.length() : 0;
        int exclusiveEndIndex = input.endsWith(toTrim) ? input.length() - toTrim.length() : input.length();

        return input.substring(startIndex, exclusiveEndIndex);
    }
}
