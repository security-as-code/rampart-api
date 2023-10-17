package org.rampart.lang.impl.core.validators;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.parsers.RampartTargetOsParser;

/**
 * Class to validate the optional "os" list declaration in rule declarations.
 * E.g., patch ("patch name", os: ["aix", "windows"]);
 * @since 1.5
 */
public class TargetOSValidator {

    private final RampartObject validatableObject;

    public TargetOSValidator(RampartObject targetOSObject) {
        this.validatableObject = targetOSObject;
    }

    /**
     * Ensures the given os declaration is well formed, if present (optional field).
     * @return validated RampartList of operating systems or the option ANY if the os declaration
     * is not present, indicating that the rule has no imposed OS restrictions
     * @throws InvalidRampartRuleException if the list entries are of type RampartString and a supported RAMPART operating system
     */
    public RampartList validateTargetOSList() throws InvalidRampartRuleException {
        return RampartTargetOsParser.parseTargetOs(validatableObject, OS_KEY);
    }
}
