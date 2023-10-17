package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import java.util.*;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl;

public class RampartSqlInjectionTypeValidator implements FirstClassRuleObjectValidator {

    private final Map<String, RampartList> visitorSymbolTable;

    public RampartSqlInjectionTypeValidator(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    public RampartSqlInjectionType validateInjectionType() throws InvalidRampartRuleException {
        RampartList injectionType = visitorSymbolTable.get(INJECTION_KEY.toString());
        if (injectionType == null) {
            throw new InvalidRampartRuleException("missing mandatory \"" + INJECTION_KEY + "\" declaration");
        }

        Set<RampartSqlInjectionTypeImpl.Type> types = EnumSet.noneOf(RampartSqlInjectionTypeImpl.Type.class);
        List<RampartNamedValue> configurations = new ArrayList<RampartNamedValue>();

        RampartObjectIterator it = injectionType.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject element = it.next();
            RampartSqlInjectionTypeImpl.Type type;
            RampartNamedValue config;
            if ((type = validateInjectionType(element)) != null) {
                types.add(type);
            } else if ((config = validateConfiguration(element)) != null) {
                configurations.add(config);
            } else {
                throw new InvalidRampartRuleException(
                        "invalid parameter " + element + " for \"" + INJECTION_KEY + "\" declaration");
            }
        }
        // These are the defaults for when `injection()` is declared:
        //  - Type.SUCCESSFULL_ATTEMPT
        //  - no configurations
        if (types.isEmpty()) {
            types.add(RampartSqlInjectionTypeImpl.Type.SUCCESSFUL_ATTEMPT);
        }
        return new RampartSqlInjectionTypeImpl(types, configurations);
    }

    private static RampartSqlInjectionTypeImpl.Type validateInjectionType(RampartObject validatableObject)
            throws InvalidRampartRuleException {
        if (validatableObject instanceof RampartConstant) {
            RampartConstant type = (RampartConstant) validatableObject;
            if (type.equals(SUCCESSFUL_ATTEMPT_KEY)) {
                return RampartSqlInjectionTypeImpl.Type.SUCCESSFUL_ATTEMPT;
            } else if (type.equals(FAILED_ATTEMPT_KEY)) {
                return RampartSqlInjectionTypeImpl.Type.FAILED_ATTEMPT;
            } else {
                throw new InvalidRampartRuleException("unrecognized constant for " + INJECTION_KEY + " declaration");
            }
        }
        return null;
    }

    private static RampartNamedValue validateConfiguration(RampartObject validatableObject) throws InvalidRampartRuleException {
        if (validatableObject instanceof RampartNamedValue) {
            RampartNamedValue config = (RampartNamedValue) validatableObject;
            if (config.getName().equals(PERMIT_KEY)
                    && config.getRampartObject() instanceof RampartConstant
                    && config.getRampartObject().equals(QUERY_PROVIDED_KEY)) {
                return config;
            }
            throw new InvalidRampartRuleException(
                    "unrecognized configuration " + config + " for " + INJECTION_KEY + " declaration");
        }
        return null;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(INJECTION_KEY);
    }

}
