package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartValidatorBase;
import org.rampart.lang.impl.sql.RampartVendorType;

public class RampartSqlOptionsValidator extends RampartValidatorBase {

    protected RampartSqlOptionsValidator(RampartObject validatableObject) {
        super(validatableObject);
    }

    public RampartList validateOptions(RampartVendorType vendor) throws InvalidRampartRuleException {
        if (validatableObject == null) {
            return RampartList.EMPTY;
        }
        RampartList vendorOptions = validateIsRampartListOfNonEmptyEntries("\"" + OPTIONS_KEY + "\" declaration");
        if (vendor.getSupportedOptions().containsAll(vendorOptions) == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(vendor.getSupportedOptions().isEmpty() == RampartBoolean.TRUE ?
                    "vendor \"" + vendor + "\" does not support any options" :
                    "vendor \"" + vendor + "\" options must be one of " + vendor.getSupportedOptions() + " for sql rule");
        }
        return vendorOptions;
    }

    @Override
    protected void validateListEntry(RampartObject entry, String entryContext) throws InvalidRampartRuleException {
        if (!(entry instanceof RampartConstant)) {
            throw new InvalidRampartRuleException(entryContext + " list entries must be constants");
        }
    }
}
