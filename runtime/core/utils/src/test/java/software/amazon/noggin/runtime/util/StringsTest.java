package software.amazon.noggin.runtime.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringsTest {
    @Test
    public void split() {
        assertThat(Strings.split("", '/')).containsExactly("");
        assertThat(Strings.split("a", '/')).containsExactly("a");
        assertThat(Strings.split("a/b", '/')).containsExactly("a", "b");
        assertThat(Strings.split("/", '/')).containsExactly("", "");
        assertThat(Strings.split("//", '/')).containsExactly("", "", "");
        assertThat(Strings.split("/a//", '/')).containsExactly("", "a", "", "");
        assertThat(Strings.split("abc/def", '/')).containsExactly("abc", "def");

    }
}