package matchers;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartBoolean;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public class RampartListMatcher extends TypeSafeMatcher<RampartList>{
    private final RampartObject[] objs;

    public RampartListMatcher(RampartObject[] objs) {
        this.objs = objs;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("RampartList with "+  newRampartList(objs) + " in any order");
    }

    @Override
    protected boolean matchesSafely(RampartList item) {
        for (RampartObject obj : objs) {
            if (item.contains(obj) == RampartBoolean.FALSE) {
                return false;
            }
        }
        return true;
    }

    public static Matcher<RampartList> containsInAnyOrder(RampartObject... objs) {
        return new RampartListMatcher(objs);
    }

}
