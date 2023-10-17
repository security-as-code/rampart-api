package org.rampart.lang.impl.core.validators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

/** General rule structure validator, uses static accessors for various test. */
public final class RuleStructureValidator {
    private RuleStructureValidator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that all object keys belong to the allowed set.
     * @param ruleName name of the rule being validated, used in error message.
     * @param thingToValidate keys to validate.
     * @param validKeys keys that are considered to be valid.
     */
    public static void validateKeys2_9(
            RampartConstant ruleName,
            Set<String> thingToValidate,
            RampartConstant[]... validKeys) throws InvalidRampartRuleException {
        final Set<String> invalidKeys = new HashSet<String>(thingToValidate);
        for (RampartConstant[] validKeySet: validKeys) {
            for (RampartConstant validKey: validKeySet) {
                invalidKeys.remove(validKey.toString());
            }
        }

        if (invalidKeys.size() == 0) {
            return;
        }

        final StringBuilder msg = new StringBuilder("\"");
        final Iterator<String> itr = invalidKeys.iterator();
        msg.append(itr.next());
        while (itr.hasNext()) {
            final String next = itr.next();
            msg.append(itr.hasNext() ? "\", \"": "\" and \"")
                .append(next);
        }

        msg.append(invalidKeys.size() > 1 ? "\" are": "\" is")
            .append(" not a recognized declaration in rule \"")
            .append(ruleName)
            .append('"');
        throw new InvalidRampartRuleException(msg.toString());
    }


    /**
     * Validates that all of the keys provided are present.
     * @param ruleName name of the rule being validated, used in error message.
     * @param thingToValidate keys to validate.
     * @param requiredKeys keys that are required for the set to be valid.
     */
    public static void validateRequiredKeys2_9(
            RampartConstant ruleName,
            Set<String> thingToValidate,
            RampartConstant[]... requiredKeys) throws InvalidRampartRuleException {
        final Set<String> missingKeys = new HashSet<String>();
        for (RampartConstant[] requiredKeySet: requiredKeys) {
            for (RampartConstant requiredKey: requiredKeySet) {
                final String keyAsString = requiredKey.toString();
                if (!thingToValidate.contains(keyAsString)) {
                    missingKeys.add(keyAsString);
                }
            }
        }

        if (missingKeys.size() == 0) {
            return;
        }

        final StringBuilder msg = new StringBuilder("\"");
        final Iterator<String> itr = missingKeys.iterator();
        msg.append(itr.next());
        while (itr.hasNext()) {
            final String next = itr.next();
            msg.append(itr.hasNext() ? "\", \"": "\" and \"");
            msg.append(next);
        }

        msg.append(missingKeys.size() > 1 ? "\" declarations are": "\" declaration is")
            .append(" missing in rule \"")
            .append(ruleName)
            .append('"');
        throw new InvalidRampartRuleException(msg.toString());
    }


    /**
     * Validates that all the keys in the symbol table are subset of the allowed keys.
     * @param ruleName name of the rule (used in error message).
     * @param visitorSymbolTable symbol table to validate.
     * @param allowedKeys keys that are allowed to be present in the table.
     * @throws InvalidRampartRuleException if some keys are present in the table but does not belong
     *   to the allowedKeys.
     */
    public static void validateDeclarations(
            RampartConstant ruleName,
            Map<String, ?> visitorSymbolTable,
            Set<String> allowedKeys) throws InvalidRampartRuleException {
        for (String key : visitorSymbolTable.keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new InvalidRampartRuleException(
                        "\"" + key + "\" is not a recognized declaration in rule \"" + ruleName + "\"");
            }
        }
    }


    /**
     * Validates that all the keys in the symbol table are subset of the allowed keys.
     * @param ruleName name of the rule (used in error message).
     * @param visitorSymbolTable symbol table to validate.
     * @param allowedKeys keys that are allowed to be present in the table.
     * @throws InvalidRampartRuleException if some keys are present in the table but does not belong
     *   to the allowedKeys.
     */
    public static void validateDeclarations(
            RampartString ruleName,
            Map<String, ?> visitorSymbolTable,
            Set<String> allowedKeys) throws InvalidRampartRuleException {
        for (String key : visitorSymbolTable.keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new InvalidRampartRuleException(
                        "\"" + key + "\" is not a recognized declaration in rule \"" + ruleName + "\"");
            }
        }
    }


    /**
     * Validates that all the keys in the symbol table are subset of the allowed keys.
     * @param ruleName name of the rule (used in error message).
     * @param visitorSymbolTable symbol table to validate.
     * @param supportedFields keys that are allowed to be present in the table.
     * @throws InvalidRampartRuleException if some keys are present in the table but does not belong
     *   to the allowedKeys.
     */
    public static void validateDeclarations(
            RampartConstant ruleName,
            Map<String, ?> visitorSymbolTable,
            RampartConstant[]... supportedFields) throws InvalidRampartRuleException {
        final Set<String> allowedKeys = new HashSet<String>();
        for (RampartConstant[] allowedKeySet: supportedFields) {
            for (RampartConstant allowedKey: allowedKeySet) {
                allowedKeys.add(allowedKey.toString());
            }
        }
        validateDeclarations(ruleName, visitorSymbolTable, allowedKeys);
    }


    /**
     * Validates that all the keys in the symbol table are subset of the allowed keys.
     * @param ruleName name of the rule (used in error message).
     * @param visitorSymbolTable symbol table to validate.
     * @param supportedFields keys that are allowed to be present in the table.
     * @throws InvalidRampartRuleException if some keys are present in the table but does not belong
     *   to the allowedKeys.
     */
    public static void validateDeclarations(
            RampartString ruleName,
            Map<String, ?> visitorSymbolTable,
            RampartConstant[]... supportedFields) throws InvalidRampartRuleException {
        final Set<String> allowedKeys = new HashSet<String>();
        for (RampartConstant[] allowedKeySet : supportedFields) {
            for (RampartConstant allowedKey : allowedKeySet) {
                allowedKeys.add(allowedKey.toString());
            }
        }
        validateDeclarations(ruleName, visitorSymbolTable, allowedKeys);
    }


    /**
     * Validates that all of the keys provided are present.
     * @param contextDescription description of the rule being validated, used in error message.
     * @param thingToValidate keys to validate.
     * @param requiredKeys keys that are required for the set to be valid.
     */
    public static void validateRequiredKeys(
            String contextDescription,
            Set<String> thingToValidate,
            RampartConstant[]... requiredKeys) throws InvalidRampartRuleException {
        for (RampartConstant[] keySet: requiredKeys) {
            for (RampartConstant key: keySet) {
                if (!thingToValidate.contains(key.toString())) {
                    throw new InvalidRampartRuleException("Missing \"" +key +"\" declaration for " + contextDescription);
                }
            }
        }
    }
}
