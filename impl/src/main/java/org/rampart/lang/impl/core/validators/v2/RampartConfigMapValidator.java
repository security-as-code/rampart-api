package org.rampart.lang.impl.core.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartNamedValue;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;

import java.util.ArrayList;
import java.util.HashMap;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.RampartOptions;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;

public class RampartConfigMapValidator extends RampartValidatorBase {

    private final HashMap<RampartConstant, RampartNamedValue> configMap;
    private final RampartOptions verifier;

    public RampartConfigMapValidator(RampartObject validatableObject, RampartOptions verifier) {
        super(validatableObject);
        this.configMap = new HashMap<RampartConstant, RampartNamedValue>();
        this.verifier = verifier;
    }

    public void validateOptions() throws InvalidRampartRuleException {
        configMap.clear();
        if (validatableObject != null) {
            validateIsRampartListOfNonEmptyEntries("\"" + OPTIONS_KEY + "\" parameter");
        }
    }

    /**
     * Looks up for incompatible options with the specified target. The same rule declaration could
     * have multiple targets, each one with its set of recognized options.
     *
     * @param target to which the options are to be applied
     * @throws InvalidRampartRuleException
     */
    private void lookForIncompatibleOptions(RampartConstant target) throws InvalidRampartRuleException {
        for (RampartConstant config : configMap.keySet()) {
            if (!verifier.getAllConfigsForTarget(target).contains(config)) {
                throw new InvalidRampartRuleException("unsupported config \"" + config + "\" for \"" + OPTIONS_KEY
                        + "\" parameter in target \"" + target + "\"");
            }
        }
    }

    /**
     * Gets validated options off this validator. Some options might not have been specified by the
     * user, so it also fills in the default required.
     *
     * @return RampartList containing all validated options specified by the user and some default in
     *         case options are missing
     * @throws InvalidRampartRuleException
     */
    public RampartList getValidatedOptions(RampartConstant target) throws InvalidRampartRuleException {
        lookForIncompatibleOptions(target);
        ArrayList<RampartNamedValue> options = new ArrayList<RampartNamedValue>();
        for (RampartConstant key : verifier.getAllConfigsForTarget(target)) {
            RampartNamedValue option = configMap.get(key);
            if (option == null) {
                // Not specified by user, fill in the defaults now
                RampartObject defaultValue = verifier.getDefaults(key);
                if (defaultValue == null) {
                    // there's no defaults for this option so skip it
                    continue;
                }
                option = newRampartNamedValue(key, defaultValue);
            }
            options.add(option);
        }
        return newRampartList(options.toArray(new RampartObject[options.size()]));
    }

    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartNamedValue)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be name value pairs");
        }
        RampartNamedValue option = (RampartNamedValue) entry;
        if (configMap.containsKey(option.getName())) {
            throw new InvalidRampartRuleException(
                    "duplicate configuration key \"" + option.getName() + "\" detected in " + entryContext);
        }
        ConfigValueValidator validateFunction = verifier.getOptionValidator(option.getName());
        if (validateFunction == null) {
            throw new InvalidRampartRuleException(
                    "option \"" + option.getName() + "\" is unsupported for " + entryContext);
        }
        RampartObject newValue;
        if ((newValue = validateFunction.test(option.getRampartObject())) == null) {
            throw new InvalidRampartRuleException(
                    "incorrect value \"" + option.getRampartObject() + "\" for option \"" + option.getName() + "\"");
        }
        configMap.put(option.getName(), newRampartNamedValue(option.getName(), newValue));
    }

}
