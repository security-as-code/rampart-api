package org.rampart.lang.impl.factory;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.java.builder.RampartRuleBuilder;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;
import java.util.Map;

/**
 * Factory class to create Rampart rule validators for all rule types
 * @since 1.1
 */
public class RampartRuleValidatorFactory {

    private RampartRuleValidatorFactory() {}

    /**
     * Creates appropriate validator for the requested rule type
     * @param version rampart api version requested
     * @param visitorSymbolTable symbolTable containing rule contents
     * @return a validator for the requested rule type or null for an unsupported rule type
     */
    public static Validatable<? extends RampartRuleBuilder, InvalidRampartRuleException> createRuleValidator(
            RampartVersion version, Map<String, RampartList> visitorSymbolTable) {
        if (version == null) {
            return null;
        }
        RampartConstant rule = RampartSingleAppVisitor.getRampartRuleName(visitorSymbolTable);
        RampartRuleType ruleType;
        try {
            ruleType = RampartRuleType.fromConstant(rule);
        } catch (IllegalArgumentException iae) {
            // invalid rule type
            return null;
        }

        switch (ruleType) {
            case FILESYSTEM:
                return RampartFileSystemValidatorFactory.createValidator(version, visitorSymbolTable);
            case HTTP:
                return RampartHttpValidatorFactory.createValidator(version, visitorSymbolTable);
            case LIBRARY:
                return RampartLibraryValidatorFactory.createValidator(version, visitorSymbolTable);
            case PATCH:
                return RampartPatchValidatorFactory.createValidator(version, visitorSymbolTable);
            case PROCESS:
                return RampartProcessValidatorFactory.createValidator(version, visitorSymbolTable);
            case SQL:
                return RampartSqlValidatorFactory.createValidator(version, visitorSymbolTable);
            case MARSHAL:
                return RampartMarshalValidatorFactory.createValidator(version, visitorSymbolTable);
            case SOCKET:
                return RampartSocketValidatorFactory.createValidator(version, visitorSymbolTable);
            case DNS:
                return RampartDnsValidatorFactory.createValidator(version, visitorSymbolTable);
            case SANITIZATION:
                return RampartSanitizationFactory.createValidator(version, visitorSymbolTable);
            case API:
                return RampartApiValidatorFactory.createValidator(version, visitorSymbolTable);
            default:
                // our coding error, this switch is incomplete
                throw new IllegalArgumentException("unknown rampart rule type specified: " + ruleType);
        }
    }

}
