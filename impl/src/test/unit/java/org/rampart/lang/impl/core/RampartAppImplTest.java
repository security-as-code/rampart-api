package org.rampart.lang.impl.core;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.NoSuchElementException;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartAppImpl;
import org.rampart.lang.impl.core.RampartRuleBase;

public class RampartAppImplTest {

    private static final RampartRuleBase RULE_ONE = mock(RampartRuleBase.class);
    private static final RampartRuleBase RULE_TWO = mock(RampartRuleBase.class);
    private static final RampartRuleBase RULE_THREE = mock(RampartRuleBase.class);

    private static final RampartRuleBase[] RULES_ONE_ELEMENT = new RampartRuleBase[] {RULE_ONE};
    private static final RampartRuleBase[] RULES_THREE_ELEMENTS = new RampartRuleBase[] {RULE_ONE, RULE_TWO, RULE_THREE};

    @Test
    public void hasNext_emptyArrayNoElements() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), new RampartRuleBase[0]);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        assertThat(iterator.hasNext(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void next_emptyArrayThrowsException() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), new RampartRuleBase[0]);

        assertThrows(NoSuchElementException.class, () -> appImpl.getRuleIterator().next());
    }

    @Test
    public void hasNext_oneElement() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_ONE_ELEMENT);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        assertThat(iterator.hasNext(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void next_oneElement() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_ONE_ELEMENT);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        assertThat(iterator.next(), equalTo(RULE_ONE));
    }

    @Test
    public void hasNext_noElementsLeft() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_THREE_ELEMENTS);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        for (int i = 0; i < RULES_THREE_ELEMENTS.length; i++) {
            iterator.next();
        }
        assertThat(iterator.hasNext(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void next_noElementsLeftThrowsException() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_THREE_ELEMENTS);
        RampartRuleIterator iterator = appImpl.getRuleIterator();

        assertThrows(NoSuchElementException.class, () -> {
            for (int i = 0; i < RULES_THREE_ELEMENTS.length; i++) {
                iterator.next();
            }
            iterator.next();
        });
    }

    @Test
    public void hasNext_alwaysTrueForNextElement() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_THREE_ELEMENTS);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        for (int i = 0; i < RULES_THREE_ELEMENTS.length; i++) {
            assertThat("Iterator failed assert at iteration " + i, iterator.hasNext(), equalTo(RampartBoolean.TRUE));
            iterator.next();
        }
    }

    @Test
    public void next_getAllElements() {
        RampartAppImpl appImpl = new RampartAppImpl(mock(RampartString.class), mock(RampartVersion.class), mock(RampartInteger.class),
                mock(RampartMetadata.class), RULES_THREE_ELEMENTS);
        RampartRuleIterator iterator = appImpl.getRuleIterator();
        for (int i = 0; i < RULES_THREE_ELEMENTS.length; i++) {
            assertThat("Iterator failed assert at iteration " + i, iterator.next(), equalTo(RULES_THREE_ELEMENTS[i]));
        }
    }
}
