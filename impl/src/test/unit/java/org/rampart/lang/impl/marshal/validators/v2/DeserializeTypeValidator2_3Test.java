package org.rampart.lang.impl.marshal.validators.v2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import org.rampart.lang.api.RampartList;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DOTNET_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.JAVA_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XML_KEY;

public class DeserializeTypeValidator2_3Test extends DeserializeTypeValidator2_0Test {
    protected Constructor<? extends DeserializeTypeValidator2_0> getDeserializeTypeValidatorConstructor()
            throws Exception {
        return DeserializeTypeValidator2_3.class.getDeclaredConstructor(Map.class);
    }

    @Test
    public void deserializeDeclarationAllSupported() throws Exception {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(JAVA_KEY, DOTNET_KEY, XML_KEY));
        RampartList list = getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType();

        assertThat(list, RampartListMatcher.containsInAnyOrder(JAVA_KEY, DOTNET_KEY, XML_KEY));
    }

    @Test
    public void deserializeDeclarationXmlIsSupported() throws Exception {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(XML_KEY));
        getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType();
    }

}
