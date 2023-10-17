package org.rampart.lang.api.http.matchers;

public class RampartPatternMatcher extends RampartHttpMatcherType {

    public static final RampartPatternMatcher ALPHANUMERIC = new RampartPatternMatcher("alphanumeric");
    public static final RampartPatternMatcher INTEGER = new RampartPatternMatcher("integer");
    public static final RampartPatternMatcher INTEGER_POSITIVE = new RampartPatternMatcher("integer-positive");
    public static final RampartPatternMatcher INTEGER_UNSIGNED = new RampartPatternMatcher("integer-unsigned");
    public static final RampartPatternMatcher HTML_ATTRIBUTE_UNQUOTED = new RampartPatternMatcher("html-attribute-unquoted");
    public static final RampartPatternMatcher HTML_TEXT = new RampartPatternMatcher("html-text");
    public static final RampartPatternMatcher HTML_NO_SINGLE_QUOTES = new RampartPatternMatcher("html-no-single-quotes");
    public static final RampartPatternMatcher HTML_NO_DOUBLE_QUOTES = new RampartPatternMatcher("html-no-double-quotes");
    // REGEX should not be a type of this class. The new model splits built in matchers and the
    // regex matcher
    @Deprecated
    public static final RampartPatternMatcher REGEX = new RampartPatternMatcher();
    public static final RampartPatternMatcher SQL_NO_SINGLE_QUOTES = new RampartPatternMatcher("sql-no-single-quotes");
    public static final RampartPatternMatcher SQL_NO_DOUBLE_QUOTES = new RampartPatternMatcher("sql-no-double-quotes");

    private RampartPatternMatcher(String name) {
        super(name);
    }

    private RampartPatternMatcher() {
        this(null);
    }

    static final RampartHttpMatcherType[] VALUES = new RampartHttpMatcherType[] {
            ALPHANUMERIC,
            INTEGER,
            INTEGER_POSITIVE,
            INTEGER_UNSIGNED,
            HTML_ATTRIBUTE_UNQUOTED,
            HTML_TEXT,
            HTML_NO_SINGLE_QUOTES,
            HTML_NO_DOUBLE_QUOTES,
            SQL_NO_SINGLE_QUOTES,
            SQL_NO_DOUBLE_QUOTES};

    @Override
    public RampartMatcherJoinType getMatcherJoinType() {
        return RampartMatcherJoinType.ALL_OF;
    }
}
