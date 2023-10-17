package matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.rampart.lang.api.core.RampartApp;

public class RampartAppMatcher extends TypeSafeMatcher<RampartApp> {
    private final String expectedText;
    private int index;
    private char expected;
    private char actual;

    private RampartAppMatcher(String expectedText) {
        this.expectedText = expectedText;
        initParsing();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("character '" + expected + "' at index " + index + " instead of '" + actual + "'");
        initParsing();
    }

    @Override
    protected boolean matchesSafely(RampartApp app) {
        return app.toString().chars().allMatch(x -> {
            if (Character.isWhitespace(expectedText.charAt(index)) && !Character.isWhitespace(x)) {
                while (Character.isWhitespace(expectedText.charAt(index))) {
                    index++;
                }
            }
            if (!Character.isWhitespace(x) && !Character.isWhitespace(expectedText.charAt(index))) {
                actual = (char) x;
                expected = expectedText.charAt(index++);
                return actual == expected;
            }
            return true;
        });
    }

    public static Matcher<RampartApp> equalTo(String text) {
        return new RampartAppMatcher(text);
    }

    private void initParsing() {
        index = 0;
        expected = '\0';
        actual = '\0';
    }
}
