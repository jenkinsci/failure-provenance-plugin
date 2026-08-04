package io.jenkins.plugins.failureprovenance;

import java.util.Locale;
import java.util.regex.Pattern;

final class FailureNormalizer {
    private static final Pattern ANSI = Pattern.compile("\\u001B\\[[;\\d]*m");
    private static final Pattern TIMESTAMP = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}[T ][0-2]\\d:[0-5]\\d:[0-5]\\d(?:[.,]\\d+)?(?:Z|[+-]\\d{2}:?\\d{2})?\\b");
    private static final Pattern UUID = Pattern.compile("\\b[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern WORKSPACE = Pattern.compile("(?:[A-Za-z]:)?(?:/|\\\\)(?:[^\\s:]+(?:/|\\\\))*workspace(?:/|\\\\)[^\\s:]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern PORT = Pattern.compile("(?<=:)(?:[1-9]\\d{0,4})(?!\\d)");
    private static final Pattern HEX = Pattern.compile("\\b[0-9a-f]{12,}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_NUMBER = Pattern.compile("(?i)(?:build|run|job)\\s*#?\\d+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private FailureNormalizer() {}

    static String normalize(String value) {
        String normalized = ANSI.matcher(value).replaceAll("");
        normalized = TIMESTAMP.matcher(normalized).replaceAll("<timestamp>");
        normalized = UUID.matcher(normalized).replaceAll("<uuid>");
        normalized = WORKSPACE.matcher(normalized).replaceAll("<workspace>");
        normalized = PORT.matcher(normalized).replaceAll("<port>");
        normalized = HEX.matcher(normalized).replaceAll("<hex>");
        normalized = BUILD_NUMBER.matcher(normalized).replaceAll("<build>");
        return WHITESPACE.matcher(normalized).replaceAll(" ").trim().toLowerCase(Locale.ROOT);
    }
}
