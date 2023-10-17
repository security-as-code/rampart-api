package org.rampart.lang.impl.interpreter;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;
import org.rampart.lang.api.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class RampartInterpreterUtilsTest {

    private final static RampartInteger DUMMY_INTEGER_VALUE = newRampartInteger(5);
    private final static RampartString DUMMY_STRING_VALUE = newRampartString("rampart");
    private final static RampartConstant DUMMY_CONSTANT_VALUE = newRampartConstant("JAVA");
    private final static RampartFloat DUMMY_FLOAT_VALUE = newRampartFloat(3.0f);
    private final static RampartBoolean DUMMY_BOOLEAN_VALUE = RampartBoolean.TRUE;
    private final static RampartNamedValue DUMMY_NAMED_VALUE =
            newRampartNamedValue(DUMMY_CONSTANT_VALUE, DUMMY_INTEGER_VALUE);
    private final static RampartList DUMMY_LIST_VALUE = newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_FLOAT_VALUE,
            DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE, DUMMY_CONSTANT_VALUE, DUMMY_NAMED_VALUE);

    @Test
    public void findFirstStringValue() {
        RampartString rampartString = RampartInterpreterUtils
                .findFirstRampartString(newRampartList(DUMMY_INTEGER_VALUE, DUMMY_BOOLEAN_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartString, is(newRampartString("rampart")));
    }

    @Test
    public void findFirstStringValueNested() {
        RampartString rampartString = RampartInterpreterUtils.findFirstRampartString(DUMMY_LIST_VALUE);
        assertThat(rampartString, is(newRampartString("rampart")));
    }

    @Test
    public void findFirstStringValueNull() {
        RampartString rampartString = RampartInterpreterUtils
                .findFirstRampartString(newRampartList(DUMMY_INTEGER_VALUE, DUMMY_BOOLEAN_VALUE));
        assertThat(rampartString, is(nullValue()));
    }

    @Test
    public void findFirstIntegerValue() {
        RampartInteger rampartInteger = RampartInterpreterUtils
                .findFirstRampartInteger(newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartInteger, is(newRampartInteger(5)));
    }

    @Test
    public void findFirstIntegerValueNested() {
        RampartInteger rampartInteger = RampartInterpreterUtils.findFirstRampartInteger(DUMMY_LIST_VALUE);
        assertThat(rampartInteger, is(newRampartInteger(5)));
    }

    @Test
    public void findFirstIntegerValueNull() {
        RampartInteger rampartInteger = RampartInterpreterUtils
                .findFirstRampartInteger(newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartInteger, is(nullValue()));
    }

    @Test
    public void findFirstBooleanValue() {
        RampartBoolean rampartBoolean = RampartInterpreterUtils
                .findFirstRampartBoolean(newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartBoolean, is(RampartBoolean.TRUE));
    }

    @Test
    public void findFirstBooleanValueNested() {
        RampartBoolean rampartBoolean = RampartInterpreterUtils.findFirstRampartBoolean(DUMMY_LIST_VALUE);
        assertThat(rampartBoolean, is(RampartBoolean.TRUE));
    }

    @Test
    public void findFirstBooleanValueNull() {
        RampartBoolean rampartBoolean = RampartInterpreterUtils
                .findFirstRampartBoolean(newRampartList(DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartBoolean, is(nullValue()));
    }

    @Test
    public void findFirstFloatValue() {
        RampartFloat rampartFloat = RampartInterpreterUtils
                .findFirstRampartFloat(newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE,
                        DUMMY_FLOAT_VALUE));
        assertThat(rampartFloat, is(newRampartFloat(3.0f)));
    }

    @Test
    public void findFirstFloatValueNested() {
        RampartFloat rampartFloat = RampartInterpreterUtils.findFirstRampartFloat(DUMMY_LIST_VALUE);
        assertThat(rampartFloat, is(newRampartFloat(3.0f)));
    }

    @Test
    public void findFirstFloatValueNull() {
        RampartFloat rampartFloat = RampartInterpreterUtils
                .findFirstRampartFloat(newRampartList(DUMMY_BOOLEAN_VALUE, DUMMY_INTEGER_VALUE, DUMMY_STRING_VALUE));
        assertThat(rampartFloat, is(nullValue()));
    }

    @Test
    public void findNamedValue() {
        RampartInteger rampartInteger = (RampartInteger) RampartInterpreterUtils
                .findRampartNamedValue(DUMMY_CONSTANT_VALUE, newRampartList(DUMMY_STRING_VALUE, DUMMY_NAMED_VALUE,
                        DUMMY_FLOAT_VALUE));
        assertThat(rampartInteger, is(newRampartInteger(5)));
    }

    @Test
    public void findNamedNested() {
        RampartInteger rampartInteger = (RampartInteger) RampartInterpreterUtils
                .findRampartNamedValue(DUMMY_CONSTANT_VALUE, DUMMY_LIST_VALUE);
        assertThat(rampartInteger, is(newRampartInteger(5)));
    }

    @Test
    public void findNamedValueNull() {
        RampartInteger rampartInteger = (RampartInteger) RampartInterpreterUtils
                .findRampartNamedValue(DUMMY_CONSTANT_VALUE, newRampartList(DUMMY_STRING_VALUE, DUMMY_FLOAT_VALUE));
        assertThat(rampartInteger, is(nullValue()));
    }

    @Test
    public void findFirstConstantValue() {
        RampartConstant rampartConstant = RampartInterpreterUtils
                .findFirstRampartConstant(newRampartList(DUMMY_INTEGER_VALUE, DUMMY_BOOLEAN_VALUE, DUMMY_CONSTANT_VALUE));
        assertThat(rampartConstant, is(newRampartConstant("JAVA")));
    }

    @Test
    public void findFirstConstantValueNested() {
        RampartConstant rampartConstant = RampartInterpreterUtils.findFirstRampartConstant(DUMMY_LIST_VALUE);
        assertThat(rampartConstant, is(newRampartConstant("JAVA")));
    }

    @Test
    public void findFirstConstantValueNull() {
        RampartConstant rampartConstant = RampartInterpreterUtils
                .findFirstRampartConstant(newRampartList(DUMMY_INTEGER_VALUE, DUMMY_BOOLEAN_VALUE));
        assertThat(rampartConstant, is(nullValue()));
    }
}
