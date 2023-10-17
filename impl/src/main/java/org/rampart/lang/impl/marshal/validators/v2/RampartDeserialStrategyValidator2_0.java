package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartDeserialStrategyValidator;
import org.rampart.lang.impl.marshal.DeserialStrategy;

public class RampartDeserialStrategyValidator2_0 implements RampartDeserialStrategyValidator {

    private static final String MESSAGE_SUFFIX =
            "\"" + RCE_KEY + "\" or "
            + "\"" + DOS_KEY + "\" declarations";

    protected final Map<String, RampartList> visitorSymbolTable;

    public RampartDeserialStrategyValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
    }

    // @Override
    public DeserialStrategy validateStrategy() throws InvalidRampartRuleException {
        if (containsValidStrategyDeclaration(RCE_KEY)
            && containsValidStrategyDeclaration(DOS_KEY))  {
            throw new InvalidRampartRuleException(
                    "RAMPART marshal rules must contain only one of " + MESSAGE_SUFFIX);
        }
        if (containsValidStrategyDeclaration(RCE_KEY)) {
            return DeserialStrategy.RCE;
        }
        if (containsValidStrategyDeclaration(DOS_KEY))  {
            return DeserialStrategy.DOS;
        }
        throw new InvalidRampartRuleException(
                "RAMPART marshal rules must contain at least one of " + MESSAGE_SUFFIX);
    }

    private boolean containsValidStrategyDeclaration(RampartConstant strategy)
            throws InvalidRampartRuleException {
        RampartList params = visitorSymbolTable.get(strategy.toString());
        if (params == null) {
            return false;
        }
        if (params.isEmpty() == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(
                    "declaration of \"" + strategy
                    + "\" in marshal rule cannot declare parameters");
        }
        return RCE_KEY.equals(strategy) || DOS_KEY.equals(strategy);
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(RCE_KEY, DOS_KEY);
    }
}
