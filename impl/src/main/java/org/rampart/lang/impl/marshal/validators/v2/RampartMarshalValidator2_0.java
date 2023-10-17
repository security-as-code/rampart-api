package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.*;
import static org.rampart.lang.impl.marshal.DeserialStrategy.RCE;
import static org.rampart.lang.impl.marshal.DeserialStrategy.DOS;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.RampartActionValidator;
import org.rampart.lang.impl.core.validators.RampartDeserialStrategyValidator;
import org.rampart.lang.impl.core.validators.RuleNameValidator;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.marshal.DeserialStrategy;
import org.rampart.lang.java.builder.RampartMarshalBuilder;

public class RampartMarshalValidator2_0 implements Validatable<RampartMarshalBuilder, InvalidRampartRuleException> {
    protected final RampartMarshalBuilder builder;
    protected final TargetOSValidator targetOSValidator;
    protected final RuleNameValidator ruleNameValidator;
    protected RampartMarshalStructureValidator2_0 structureValidator;
    protected DeserializeTypeValidator2_0 deserialTypeValidator;
    protected RampartDeserialStrategyValidator strategyValidator;
    protected RampartActionValidator actionValidator;

    public RampartMarshalValidator2_0(Map<String, RampartList> visitorSymbolTable) {
        builder = new RampartMarshalBuilder();
        targetOSValidator = new TargetOSValidator(RampartInterpreterUtils.
                findRampartNamedValue(OS_KEY, visitorSymbolTable.get(MARSHAL_KEY.toString())));
        ruleNameValidator = new RuleNameValidator(visitorSymbolTable.get(MARSHAL_KEY.toString()));
        structureValidator = new RampartMarshalStructureValidator2_0(visitorSymbolTable, builder);
        deserialTypeValidator = new DeserializeTypeValidator2_0(visitorSymbolTable);
        strategyValidator = new RampartDeserialStrategyValidator2_0(visitorSymbolTable);
        actionValidator = new RampartMarshalActionValidator2_0(visitorSymbolTable);
    }

    // @Override
    public RampartMarshalBuilder validate() throws InvalidRampartRuleException {
        structureValidator.feedValidators(
                deserialTypeValidator,
                strategyValidator,
                actionValidator);
        RampartString ruleName = ruleNameValidator.validateRuleName();
        structureValidator.validateDeclarations(ruleName);
        builder.addRuleName(ruleName)
              .addDeserialTypes(deserialTypeValidator.validateDeserialType())
              .addAction(actionValidator.validateRampartAction())
              .addTargetOSList(targetOSValidator.validateTargetOSList());
        DeserialStrategy deserialStrategy = strategyValidator.validateStrategy();
        if (deserialStrategy == RCE) {
            builder.withProtectOnRce();
        } else if (deserialStrategy == DOS) {
            builder.withProtectOnDos();
        }
        return builder;
    }
}
