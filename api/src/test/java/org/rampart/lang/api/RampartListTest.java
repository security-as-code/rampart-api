package org.rampart.lang.api;
import static org.rampart.lang.api.utils.RampartUtils.newRampartString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.rampart.lang.api.utils.RampartUtils;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class RampartListTest {

    private final static RampartString THETA = newRampartString("theta");
    private final static RampartString BETA = newRampartString("beta");
    private final static RampartString ALPHA = newRampartString("alpha");
    private final static RampartString OMEGA = newRampartString("omega");
    private final static RampartString PSI = newRampartString("psi");

    @Test
    public void toString_listWithValues() {
        RampartList list = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("world!!"),
                newRampartString("What"),
                newRampartString("are"),
                newRampartString("you"),
                newRampartString("looking"),
                newRampartString("at?"));

        String expected = "[\"hello\", \"world!!\", \"What\", \"are\", \"you\", \"looking\", \"at?\"]";

        assertThat(list.toString(), equalTo(expected));
    }

    @Test
    public void toString_emptyList() {
        RampartList list = RampartUtils.newRampartList();

        assertThat(list.toString(), equalTo("[]"));
    }

    @Test
    public void toString_singleValue() {
        RampartList list = RampartUtils.newRampartList(newRampartString("Hello!"));

        assertThat(list.toString(), equalTo("[\"Hello!\"]"));
    }

    @Test
    public void toString_withNullValues() {
        RampartList list = RampartUtils.newRampartList(null, null);

        assertThat(list.toString(), equalTo("[null, null]"));
    }

    @Test
    public void equals_listAndDifferentType() {
        RampartString string = newRampartString("Hello!");
        RampartList list = RampartUtils.newRampartList(string);

        assertThat(list, not(equalTo(string)));
    }

    @Test
    public void equals_listAndNull() {
        RampartList list = RampartUtils.newRampartList();

        assertThat(list, not(equalTo(null)));
    }

    @Test
    public void equals_listsWithDifferentSizes() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"));

        assertThat(firstList, not(equalTo(secondList)));
    }

    @Test
    public void equals_listsSameValues() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("world!!"));

        assertThat(firstList, equalTo(secondList));
    }

    @Test
    public void equals_bothListsWithSameNullValues() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                null,
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"),
                null,
                newRampartString("world!!"));

        assertThat(firstList, equalTo(secondList));
    }

    @Test
    public void equals_oneListWithNullValuesOtherListWithoutNullValues() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                null,
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("oops"),
                newRampartString("world!!"));

        assertThat(firstList, not(equalTo(secondList)));
    }

    @Test
    public void equals_oneListWithoutNullValuesOtherListWithNullValues() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("oops"),
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"),
                null,
                newRampartString("world!!"));

        assertThat(firstList, not(equalTo(secondList)));
    }

    @Test
    public void equals_listsWithDifferentValues() {
        RampartList firstList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("world!!"));
        RampartList secondList = RampartUtils.newRampartList(
                newRampartString("hello"),
                newRampartString("plants!!"));

        assertThat(firstList, not(equalTo(secondList)));
    }

    @Test
    public void equals_listsWithDifferentSingleValue() {
        RampartList firstList = RampartUtils.newRampartList(newRampartString("potatoes"));
        RampartList secondList = RampartUtils.newRampartList(newRampartString("cabbage"));

        assertThat(firstList, not(equalTo(secondList)));
    }

    @Test
    public void equals_listsWithSameSingleValue() {
        RampartList firstList = RampartUtils.newRampartList(newRampartString("potatoes"));
        RampartList secondList = RampartUtils.newRampartList(newRampartString("potatoes"));

        assertThat(firstList, equalTo(secondList));
    }

    @Test
    public void containsAllEmptyList() {
        RampartList myList = RampartList.EMPTY;
        assertThat(myList.containsAll(RampartUtils.newRampartList(BETA, ALPHA, OMEGA)), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void containsAllCompareWithEmptyList() {
        RampartList myList = RampartUtils.newRampartList(THETA);
        assertThat(myList.containsAll(RampartList.EMPTY), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void containsAllCompareWithListSingleElementNoMatches() {
        RampartList myList = RampartUtils.newRampartList(THETA);
        assertThat(myList.containsAll(RampartUtils.newRampartList(BETA, ALPHA, OMEGA)), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void containsAllCompareWithListSingleElementMatches() {
        RampartList myList = RampartUtils.newRampartList(THETA, BETA, OMEGA);
        assertThat(myList.containsAll(RampartUtils.newRampartList(THETA)), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void containsAllNoMatches() {
        RampartList myList = RampartUtils.newRampartList(THETA, BETA);
        assertThat(myList.containsAll(RampartUtils.newRampartList(THETA, ALPHA)), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void containsAllAllMatch() {
        RampartList myList = RampartUtils.newRampartList(THETA, BETA, ALPHA);
        assertThat(myList.containsAll(RampartUtils.newRampartList(THETA, BETA)), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void containsAllEmptyListCompareWithSingleElementListNoMatches() {
        RampartList myList = RampartList.EMPTY;
        assertThat(myList.containsAll(RampartUtils.newRampartList(PSI)), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void containsAllNoneMatches() {
        RampartList myList = RampartUtils.newRampartList(THETA, BETA);
        assertThat(myList.containsAll(RampartUtils.newRampartList(PSI, ALPHA)), equalTo(RampartBoolean.FALSE));
    }

}
