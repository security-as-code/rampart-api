package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.ALLOW_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XXE_KEY;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.builder.RampartMarshalBuilder;
import java.util.Map;

public class RampartMarshalStructureValidator2_6 extends RampartMarshalStructureValidator2_0 {

    public RampartMarshalStructureValidator2_6(Map<String, RampartList> visitorSymbolTable, RampartMarshalBuilder builder) {
        super(visitorSymbolTable, builder);
    }

    @Override
    public void crossValidate() throws InvalidRampartRuleException {
        if (builder.hasDeserialTypes()
                && builder.hasExternalXmlEntityConfig()) {
            throw new InvalidRampartRuleException(
                    "RAMPART marshal rules must contain only one of \""
                    + DESERIALIZE_KEY + "\" or \""
                    + XXE_KEY + "\" declarations");
        }
        if (!builder.hasDeserialTypes()
                && !builder.hasExternalXmlEntityConfig()) {
            throw new InvalidRampartRuleException(
                    "RAMPART marshal rules must contain at least one of \""
                    + DESERIALIZE_KEY + "\" or \""
                    + XXE_KEY + "\" declarations");
        }
        if (builder.hasDeserialTypes()
                && isAllowAction(builder.getAction())) {
            throw new InvalidRampartRuleException(
                    "RAMPART marshal deserialize rule can not have an action of \"" + ALLOW_KEY + "\"");
        }
    }

    private static boolean isAllowAction(RampartAction rampartAction) {
        return rampartAction != null
                && RampartActionType.ALLOW.equals(rampartAction.getActionType());
    }
}
