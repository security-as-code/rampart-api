package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.URI_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XXE_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ExternalXmlEntityConfigValidator2_6Test {

    private HashMap<String, RampartList> symbolTable;
    private ExternalXmlEntityConfigValidator2_6 validator;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
        validator = new ExternalXmlEntityConfigValidator2_6(symbolTable);
    }

    @Test
    public void xxeConfigHasInvalidConfig() {
        RampartNamedValue invalidConfig = RampartPrimitives.newRampartNamedValue(
                RampartPrimitives.newRampartConstant("invalid-key"),
                RampartPrimitives.newRampartString("invalid-val"));
        RampartList rampartList = RampartPrimitives.newRampartList(invalidConfig);
        symbolTable.put(XXE_KEY.toString(), rampartList);
        validator = new ExternalXmlEntityConfigValidator2_6(symbolTable);

        InvalidRampartRuleException thrown = assertThrows(
                InvalidRampartRuleException.class,
                () -> validator.validateExternalXmlEntityConfig());

        assertThat(thrown.getMessage(),
                equalTo("\"invalid-key\" is not a recognized \"xxe\" parameter."));
    }

    @Test
    public void xxeConfigHasValidConfig() {
        RampartNamedValue invalidConfig = RampartPrimitives.newRampartNamedValue(
                RampartPrimitives.newRampartConstant(URI_KEY.toString()),
                RampartPrimitives.newRampartList(RampartPrimitives.newRampartString(
                        "http://struts.apache.org/dtds/struts-2.3.dtd")));
        RampartList rampartList = RampartPrimitives.newRampartList(invalidConfig);
        symbolTable.put(XXE_KEY.toString(), rampartList);
        validator = new ExternalXmlEntityConfigValidator2_6(symbolTable);

        ExternalXmlEntityConfig xxeConfig = assertDoesNotThrow(
                () -> validator.validateExternalXmlEntityConfig());

        assertNotNull(xxeConfig);
    }

}
