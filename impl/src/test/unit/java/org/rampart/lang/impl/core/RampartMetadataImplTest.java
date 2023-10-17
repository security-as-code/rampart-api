package org.rampart.lang.impl.core;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.HashSet;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartMetadata;
import org.junit.jupiter.api.Test;

public class RampartMetadataImplTest {

    private static final RampartConstant KEY_ONE = newRampartConstant("keyOne");
    private static final RampartConstant KEY_TWO = newRampartConstant("keyTwo");
    private static final RampartString VALUE_ONE = newRampartString("value 1");
    private static final RampartString VALUE_TWO = newRampartString("value 2");

    @Test
    public void correctToStringForLoggableMetadata() {
        HashMap<RampartConstant, RampartNamedValue> metadataMap = new HashMap<RampartConstant, RampartNamedValue>(2) {{
            put(KEY_ONE, newRampartNamedValue(KEY_ONE, VALUE_ONE));
            put(KEY_TWO, newRampartNamedValue(KEY_TWO, VALUE_TWO));
        }};
        HashSet<RampartConstant> log = new HashSet<RampartConstant>(1) {{
            add(KEY_TWO);
        }};
        RampartMetadata metadata = new RampartMetadataImpl(metadataMap, log);
        assertThat(metadata.toString(), equalTo("metadata(\n\tlog: {\n\t\tkeyTwo: \"value 2\"},\n\tkeyOne: \"value 1\")"));
    }
}
