package org.rampart.lang.impl.sanitization.validators.v2;

import static org.rampart.lang.api.constants.RampartSanitizationConstants.ATTRIBUTE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.IGNORE_KEY;
import static org.rampart.lang.api.constants.RampartSanitizationConstants.PAYLOAD_KEY;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.sanitization.RampartIgnoreImpl;
import org.rampart.lang.java.RampartPrimitives;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RampartIgnoreValidator2_3 implements FirstClassRuleObjectValidator {

    private static final RampartInteger MAX_IGNORE_PARAMS =
            RampartPrimitives.newRampartInteger(2);
    protected final Map<String, RampartList> visitorSymbolTable;

    public RampartIgnoreValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(IGNORE_KEY);
    }

    public RampartIgnore validate() throws InvalidRampartRuleException {
        RampartList ignore = visitorSymbolTable.get(IGNORE_KEY.toString());
        if (ignore == null) {
            return null;
        }
        if (ignore.size().isGreaterThan(MAX_IGNORE_PARAMS).equals(RampartBoolean.TRUE)) {
            throw new InvalidRampartRuleException(
                    "Invalid Sanitization rule configuration."
                    + " Too many parameters. Only expecting a maximum of two"
                    + " parameters for " + PAYLOAD_KEY
                    + " and/or " + ATTRIBUTE_KEY);
        }
        RampartList payload = getPayload(ignore);
        RampartList attribute = getAttribute(ignore);
        if (payload.isEmpty() == RampartBoolean.TRUE
                && attribute.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(
                    "Invalid Sanitization rule configuration. " +
                    "Please ensure at least one of " +
                    "'payload', or 'attribute' is configured correctly.");
        }
        return new RampartIgnoreImpl(payload, attribute);
    }

    private RampartList getPayload(RampartList ignoreProps)
            throws InvalidRampartRuleException {
        return getList(ignoreProps, PAYLOAD_KEY);
    }

    private RampartList getAttribute(RampartList ignoreProps)
            throws InvalidRampartRuleException {
        return getList(ignoreProps, ATTRIBUTE_KEY);
    }

    private RampartList getList(RampartList ignoreProps, RampartConstant rampartConstant)
            throws InvalidRampartRuleException {
        RampartObjectIterator it = ignoreProps.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject prop = it.next();
            if (prop instanceof RampartNamedValue
                && ((RampartNamedValue) prop).getName().equals(rampartConstant)) {
                RampartObject propValue = ((RampartNamedValue) prop).getRampartObject();
                if (!isValidRampartStringList(propValue)) {
                    throw new InvalidRampartRuleException(
                            "\"" + rampartConstant + "\" declaration in the sanitize " +
                            "rule must be a list of string values.");
                }
                return (RampartList) propValue;
            }
        }
        return RampartList.EMPTY;
    }

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
            if (!(it.next() instanceof RampartString)) {
                return false;
            }
        }
        return true;
    }

}
