package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.ATTRIBUTE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.PAYLOAD_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.IGNORE_KEY;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RampartIgnoreValidator2_10 extends RampartIgnoreValidator2_3 {

    private final ArrayList<RampartConstant> VALID_PROPERTIES =
            new ArrayList<RampartConstant>(Arrays.asList(PAYLOAD_KEY, ATTRIBUTE_KEY));

    public RampartIgnoreValidator2_10(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
    }

    @Override
    public RampartIgnore validate() throws InvalidRampartRuleException {
        RampartIgnore rampartIgnoreImpl = super.validate();

        RampartList ignore = visitorSymbolTable.get(IGNORE_KEY.toString());
        throwIfContainsInvalidProps(ignore);

        return rampartIgnoreImpl;
    }

    private void throwIfContainsInvalidProps(RampartList ignoreProps) throws InvalidRampartRuleException {
        RampartObjectIterator it = ignoreProps.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject prop = it.next();
            if (prop instanceof RampartNamedValue
                    && !(VALID_PROPERTIES.contains(((RampartNamedValue) prop).getName()))) {
                throw new InvalidRampartRuleException(
                        "Invalid Sanitization rule configuration."
                                + " Only expecting 'ignore' parameters '" + PAYLOAD_KEY + "'"
                                + " and/or '" + ATTRIBUTE_KEY + "', but found invalid parameter "
                                +  "\"" + ((RampartNamedValue) prop).getName()+ "\"");
            }
        }
    }

    @Override
    protected boolean isValidRampartStringList(RampartObject rampartObject) {
        if (!(rampartObject instanceof RampartList)) {
            return false;
        }
        RampartList rampartObjectsList = ((RampartList) rampartObject);
        if (rampartObjectsList.isEmpty() == RampartBoolean.TRUE) {
            return false;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartString value = (RampartString) it.next();
            if (!(value instanceof RampartString) || value.isEmpty() == RampartBoolean.TRUE) {
                return false;
            }
        }
        return true;
    }
}