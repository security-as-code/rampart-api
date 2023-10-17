package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.DOTNET_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.JAVA_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeserializeTypeValidator2_0 extends RampartValidatorBase implements FirstClassRuleObjectValidator {

    public DeserializeTypeValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable.get(DESERIALIZE_KEY.toString()));
    }

    public RampartList validateDeserialType() throws InvalidRampartRuleException {
        RampartList deserializeObjectParams =
                validateIsRampartListOfNonEmptyEntries("\"" + DESERIALIZE_KEY + "\" declaration");
        ArrayList<RampartConstant> deserializeTypes = new ArrayList<RampartConstant>();
        RampartObjectIterator it = deserializeObjectParams.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject deserializeParam = it.next();
            validateDeserialType(deserializeParam);
            deserializeTypes.add((RampartConstant) deserializeParam);
        }
        return newRampartList(deserializeTypes.toArray(new RampartObject[deserializeTypes.size()]));
    }

    protected void validateDeserialType(RampartObject deserialParam) throws InvalidRampartRuleException {
        if (!deserialParam.equals(JAVA_KEY) && !deserialParam.equals(DOTNET_KEY)) {
            throw new InvalidRampartRuleException(
                    "\"" + DESERIALIZE_KEY + "\" declaration in marshal rule only supports \"" + JAVA_KEY + "\" or \""
                            + DOTNET_KEY + "\" constants as parameters");
        }
    }

    @Override
    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartConstant)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be constants");
        }
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(DESERIALIZE_KEY);
    }
}
