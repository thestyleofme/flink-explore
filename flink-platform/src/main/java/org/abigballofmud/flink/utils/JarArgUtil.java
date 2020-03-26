package org.abigballofmud.flink.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 11:07
 * @since 1.0
 */
public class JarArgUtil {

    private JarArgUtil() {
        throw new IllegalStateException("util class");
    }

    private static final Pattern ARGUMENTS_TOKENIZE_PATTERN = Pattern.compile("([^\"']\\S*|\".+?\"|'.+?')\\s*");

    /**
     * Takes program arguments as a single string, and splits them into a list of string.
     *
     * <pre>
     * tokenizeArguments("--foo bar")            = ["--foo" "bar"]
     * tokenizeArguments("--foo \"bar baz\"")    = ["--foo" "bar baz"]
     * tokenizeArguments("--foo 'bar baz'")      = ["--foo" "bar baz"]
     * tokenizeArguments(null)                   = []
     * </pre>
     *
     * <strong>WARNING: </strong>This method does not respect escaped quotes.
     */
    public static List<String> tokenizeArguments(@Nullable final String args) {
        if (args == null) {
            return Collections.emptyList();
        }
        final Matcher matcher = ARGUMENTS_TOKENIZE_PATTERN.matcher(args);
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group()
                    .trim()
                    .replace("\"", "")
                    .replace("'", ""));
        }
        return tokens;
    }
}
