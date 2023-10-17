package org.rampart.lang.impl.sql.validators.v2;

import static org.rampart.lang.api.constants.RampartSqlConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.sql.RampartVendor;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.impl.sql.RampartVendorImpl;
import org.rampart.lang.impl.sql.RampartVendorType;

public class RampartSqlVendorValidator implements FirstClassRuleObjectValidator {

    private final RampartSqlOptionsValidator optionsValidator;
    private final Map<String, RampartList> visitorSymbolTable;

    public RampartSqlVendorValidator(Map<String, RampartList> visitorSymbolTable) {
        this.visitorSymbolTable = visitorSymbolTable;
        RampartObject vendorParamList = visitorSymbolTable.get(VENDOR_KEY.toString());
        optionsValidator = new RampartSqlOptionsValidator(
                RampartInterpreterUtils.findRampartNamedValue(OPTIONS_KEY, vendorParamList));
    }

    public RampartVendor validateVendor() throws InvalidRampartRuleException {
        RampartList vendorParamList = visitorSymbolTable.get(VENDOR_KEY.toString());
        if (vendorParamList == null) {
            // vendor is not mandatory
            return new RampartVendorImpl(RampartVendorType.ANY, RampartList.EMPTY);
        }
        // vendorParamList is made out of a RampartString(vendor) and an RampartNamedValue(options for that vendor)
        RampartConstant vendorName = null;
        RampartObjectIterator it = vendorParamList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject param = it.next();
            if (param instanceof RampartConstant) {
                RampartConstant candidateVendor = validateVendorName((RampartConstant) param);
                if (vendorName != null) {
                    throw new InvalidRampartRuleException(
                            "invalid vendor \"" + candidateVendor + "\" only one database vendor can be specified");
                }
                vendorName = candidateVendor;
            }
        }
        if (vendorName == null) {
            throw new InvalidRampartRuleException("vendor name not found");
        }
        RampartVendorType vendor = RampartVendorType.valueOf(vendorName);
        return new RampartVendorImpl(vendor, optionsValidator.validateOptions(vendor));
    }

    private static RampartConstant validateVendorName(RampartConstant vendorName) throws InvalidRampartRuleException {
        if (RampartVendorType.valueOf(vendorName) == null) {
            throw new InvalidRampartRuleException(
                    "vendor must be one of supported types: " + RampartVendorType.getAllVendorTypes());
        }
        return vendorName;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(VENDOR_KEY);
    }
}
