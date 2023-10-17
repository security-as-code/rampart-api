package org.rampart.lang.api.http.matchers;

public class RampartHttpMethodMatcher extends RampartHttpMatcherType {

    RampartHttpMethodMatcher(String name) {
        super(name);
    }

    public static final RampartHttpMethodMatcher GET = new RampartHttpMethodMatcher("GET");
    public static final RampartHttpMethodMatcher POST = new RampartHttpMethodMatcher("POST");
    public static final RampartHttpMethodMatcher HEAD = new RampartHttpMethodMatcher("HEAD");
    public static final RampartHttpMethodMatcher PUT = new RampartHttpMethodMatcher("PUT");
    public static final RampartHttpMethodMatcher DELETE = new RampartHttpMethodMatcher("DELETE");
    public static final RampartHttpMethodMatcher CONNECT = new RampartHttpMethodMatcher("CONNECT");
    public static final RampartHttpMethodMatcher OPTIONS = new RampartHttpMethodMatcher("OPTIONS");
    public static final RampartHttpMethodMatcher TRACE = new RampartHttpMethodMatcher("TRACE");
    public static final RampartHttpMethodMatcher PATCH = new RampartHttpMethodMatcher("PATCH");

    static final RampartHttpMatcherType[] VALUES = new RampartHttpMatcherType[] {
            GET,
            POST,
            HEAD,
            PUT,
            DELETE,
            CONNECT,
            OPTIONS,
            TRACE,
            PATCH};

    @Override
    public RampartMatcherJoinType getMatcherJoinType() {
        return RampartMatcherJoinType.ONE_OF;
    }
}
