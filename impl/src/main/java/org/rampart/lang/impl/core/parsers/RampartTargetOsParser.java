package org.rampart.lang.impl.core.parsers;

import static org.rampart.lang.api.constants.RampartGeneralConstants.AIX_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.ANY_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.LINUX_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.OS_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SOLARIS_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.WINDOWS_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

/**
 * Parser for the optional "Operating System" list in rule declaration.
 *
 * E.g., patch ("patch name", os: ["aix", "windows"]);
 */
public class RampartTargetOsParser {
    public static final RampartList SUPPORTED_OPERATING_SYSTEMS =
            newRampartList(ANY_KEY, AIX_KEY, LINUX_KEY, SOLARIS_KEY, WINDOWS_KEY);

    /**
     * Parses the target OS list.
     * @param input input object to parse.
     * @param fieldKey key of the input field.
     * @return parsed target OS.
     */
    public static RampartList parseTargetOs(RampartObject input, RampartConstant fieldKey) throws InvalidRampartRuleException {
        if (input == null) {
            return newRampartList(ANY_KEY);
        }

        if (!(input instanceof RampartList)
                || ((RampartList) input).isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("\"" + fieldKey + "\" declaration must be followed by a non empty list");
        }

        final RampartList targetOsList = (RampartList) input;
        final RampartObjectIterator targetOSIterator = targetOsList.getObjectIterator();
        while (targetOSIterator.hasNext() == RampartBoolean.TRUE) {
            validateOsEntry(targetOSIterator.next(), fieldKey);
        }
        return sanitizeTargetOSList(targetOsList);
    }


    /** Parses target OS from the standard visitor table. */
    public static RampartList parseTargetOs(
            Map<String, RampartList> visitorSymbolTable,
            RampartConstant ruleName,
            RampartConstant osFieldKey) throws InvalidRampartRuleException {
        final RampartList ruleObject = visitorSymbolTable.get(ruleName.toString());
        final RampartObject osObject = RampartInterpreterUtils.findRampartNamedValue(osFieldKey, ruleObject);
        return parseTargetOs(osObject, osFieldKey);
    }


    /** Parses target OS from the standard visitor table. */
    public static RampartList parseTargetOs(
            Map<String, RampartList> visitorSymbolTable,
            RampartConstant ruleName) throws InvalidRampartRuleException {
        return parseTargetOs(visitorSymbolTable, ruleName, OS_KEY);
    }


    /** Validates that the entry is valid OS entry. */
    public static void validateOsEntry(RampartObject entry, RampartConstant fieldKey) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartConstant)) {
            throw new InvalidRampartRuleException("\"" + fieldKey + "\" declaration list entries must be constants");
        }
        final RampartConstant osListEntryString = (RampartConstant) entry;
        if (!isSupportedOperatingSystem(osListEntryString)) {
            throw new InvalidRampartRuleException("unsupported entry in \"" + fieldKey + "\": \"" + osListEntryString
                    + "\". Must be one of: " + SUPPORTED_OPERATING_SYSTEMS);
        }
    }


    public static boolean isSupportedOperatingSystem(RampartConstant operatingSystem) {
        return SUPPORTED_OPERATING_SYSTEMS.contains(operatingSystem) == RampartBoolean.TRUE;
    }


    /**
     * If the list contains "any" there is no need to have the other values inside.
     * @param targetOSList list to be sanitized
     * @return list with existing values or list with ANY_OS_KEY only.
     */
    public static RampartList sanitizeTargetOSList(RampartList targetOSList) {
        return targetOSList.contains(ANY_KEY) == RampartBoolean.TRUE
                ? newRampartList(ANY_KEY)
                : targetOSList;
    }
}
