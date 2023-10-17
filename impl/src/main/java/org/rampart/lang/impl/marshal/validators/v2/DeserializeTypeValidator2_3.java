package org.rampart.lang.impl.marshal.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.DOTNET_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.JAVA_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XML_KEY;

public class DeserializeTypeValidator2_3 extends DeserializeTypeValidator2_0 {
    public DeserializeTypeValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    protected void validateDeserialType(RampartObject deserialParam) throws InvalidRampartRuleException {
        if (!deserialParam.equals(JAVA_KEY) && !deserialParam.equals(DOTNET_KEY) && !deserialParam.equals(XML_KEY)) {
            throw new InvalidRampartRuleException(
                    "\"" + DESERIALIZE_KEY + "\" declaration in marshal rule only supports \"" + JAVA_KEY + "\" or \""
                            + DOTNET_KEY + "\" or \"" + XML_KEY + "\" constants as parameters");
        }
    }
}
