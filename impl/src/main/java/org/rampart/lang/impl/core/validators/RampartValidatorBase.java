package org.rampart.lang.impl.core.validators;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.RampartPrimitives;

/**
 * Class to offer reusable validation functionality to its subclasses
 * @since 1.5
 */
public abstract class RampartValidatorBase {
    protected final RampartObject validatableObject;

    protected RampartValidatorBase(RampartObject validatableObject) {
        this.validatableObject = validatableObject;
    }

    /**
     * Validates the given RampartObject is of type RampartString, is not null and is not the empty string
     *
     * @param stringContext context of the string being verified
     * @return RampartString instance of the string
     * @throws InvalidRampartRuleException when the string is null or empty
     */
    protected RampartString validateIsNotEmptyString(String stringContext) throws InvalidRampartRuleException {
        RampartString validatableString = RampartInterpreterUtils
                .findFirstRampartString(validatableObject);
        if (isNullOrEmptyString(validatableString)) {
            throw new InvalidRampartRuleException(stringContext + " is missing");
        }
        return validatableString;
    }

    /**
     * Validates the given RampartObject is of type RampartList, is not empty and contains only non-empty values.
     *
     * @param listContext context of the list being verified
     * @return RampartList instance of the list
     * @throws InvalidRampartRuleException if the
     */
    protected RampartList validateIsRampartListOfNonEmptyEntries(String listContext) throws InvalidRampartRuleException {
        if (!(validatableObject instanceof RampartList)
                || ((RampartList) validatableObject).isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(listContext + " must be followed by a non empty list");
        }
        RampartList validatableList = (RampartList) validatableObject;
        validateListEntries(validatableList, listContext);
        return validatableList;
    }

    private void validateListEntries(RampartList list, String entryContext) throws InvalidRampartRuleException {
        RampartObjectIterator it = list.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateListEntry(it.next(), entryContext);
        }
    }

    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartString)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be quoted string literals");
        }
        RampartString rampartStringEntry = (RampartString) entry;
        if (isNullOrEmptyString(rampartStringEntry)) {
            throw new InvalidRampartRuleException(entryContext + " entry must not be an empty string literal");
        }
    }

    public static boolean isNullOrEmptyString(RampartString rampartString) {
        return rampartString == null || RampartPrimitives.toJavaBoolean(rampartString.trim().isEmpty());
    }

}
