package org.rampart.lang.impl.factory;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.rampart.lang.api.constants.RampartPatchConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.patch.validators.v1.RampartPatchValidator1_1;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartRuleValidatorTest {

    private static final RampartVersion SUPPORTED_RAMPART_VERSION =
            newRampartVersion(1, 1);

    private Map<String, RampartList> symbolTable;
    private Map<String, RampartList> unknown_rule;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
        symbolTable.put(PATCH_KEY.toString(), newRampartList(newRampartString("patchvalue")));
        symbolTable.put(FUNCTION_KEY.toString(), newRampartList(newRampartString("functionvalue")));
        symbolTable.put(CALL_KEY.toString(), newRampartList(newRampartString("callvalue")));
        symbolTable.put(CODE_KEY.toString(),
                newRampartList(
                        newRampartNamedValue(LANGUAGE_KEY, JAVA_KEY),
                        newRampartNamedValue(IMPORT_KEY,
                                newRampartList(newRampartString("java.io.IOException")))));
        symbolTable.put(SOURCE_CODE_KEY.toString(), newRampartList(newRampartString("sourcecodevalue")));

        unknown_rule = new LinkedHashMap<>();
        unknown_rule.put("bob", newRampartList());
    }

    @Test
    public void createRuleValidatorValid() {
        Validatable<?,?> validatable =
                RampartRuleValidatorFactory.createRuleValidator(SUPPORTED_RAMPART_VERSION, symbolTable);
        assertThat(validatable, instanceOf(RampartPatchValidator1_1.class));
    }

    @Test
    public void createRuleValidatorUnknownRuleType() {
        Validatable<?,?> validatable =
            RampartRuleValidatorFactory.createRuleValidator(SUPPORTED_RAMPART_VERSION, unknown_rule);
        assertThat(validatable, nullValue());
    }
}
