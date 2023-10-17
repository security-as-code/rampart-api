package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_EXPANSION_LIMIT_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.REFERENCE_LIMIT_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.URI_KEY;
import static org.rampart.lang.api.constants.RampartMarshalConstants.XXE_KEY;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.marshal.ExternalXmlEntityConfig;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.FirstClassRuleObjectValidator;
import org.rampart.lang.impl.marshal.ExternalXmlEntityConfigImpl;
import org.rampart.lang.java.RampartPrimitives;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExternalXmlEntityConfigValidator2_6 implements FirstClassRuleObjectValidator {

    private static final RampartInteger ZERO =
            RampartPrimitives.newRampartInteger(0);

    protected final RampartList xxeConfigurationList;

    public ExternalXmlEntityConfigValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        this.xxeConfigurationList = visitorSymbolTable.get(XXE_KEY.toString());
    }

    public ExternalXmlEntityConfig validateExternalXmlEntityConfig() throws InvalidRampartRuleException {
        if (xxeConfigurationList == null) {
            return null;
        }
        if (xxeConfigurationList.size().equals(ZERO)) {
            return new ExternalXmlEntityConfigImpl();
        }

        RampartList uris = null;
        RampartInteger referenceLimit = null;
        RampartInteger referenceExpansionLimit = null;

        RampartObjectIterator xxeConfigItr = xxeConfigurationList.getObjectIterator();
        while (xxeConfigItr.hasNext() == RampartBoolean.TRUE) {
            RampartObject next = xxeConfigItr.next();
            if (!(next instanceof RampartNamedValue)) {
                throw new InvalidRampartRuleException(
                        "Not the expected named value pairs for the "
                        + "\"xxe\" configuration.");
            }
            RampartNamedValue namedValue = (RampartNamedValue) next;
            if (namedValue.getName().equals(URI_KEY)) {
                if (!(namedValue.getRampartObject() instanceof RampartList)) {
                    throw new InvalidRampartRuleException(
                            "Not the expected list of string values for \""
                            + URI_KEY + "\" configuration.");
                }
                uris = (RampartList) namedValue.getRampartObject();
                RampartObjectIterator uriItr = uris.getObjectIterator();
                while (uriItr.hasNext() == RampartBoolean.TRUE) {
                    next = uriItr.next();
                    if (!(next instanceof RampartString)) {
                        throw new InvalidRampartRuleException(
                                "Not the expected string value for each item in the list for the \""
                                + URI_KEY + "\" configuration.");
                    }
                }
            } else if (namedValue.getName().equals(REFERENCE_KEY)) {
                if (!(namedValue.getRampartObject() instanceof RampartList)) {
                    throw new InvalidRampartRuleException(
                            "Not the expected list of named value pairs for \""
                            + REFERENCE_KEY + "\" configuration.");
                }
                RampartList refConfig = (RampartList) namedValue.getRampartObject();
                RampartObjectIterator refItr = refConfig.getObjectIterator();
                while (refItr.hasNext() == RampartBoolean.TRUE) {
                    next = refItr.next();
                    if (!(next instanceof RampartNamedValue)) {
                        throw new InvalidRampartRuleException(
                                "Not the expected named value pair for \""
                                + REFERENCE_KEY + "\" configuration.");
                    }
                    RampartNamedValue refParam = (RampartNamedValue) next;
                    if (refParam.getName().equals(REFERENCE_LIMIT_KEY)) {
                        if (!(refParam.getRampartObject() instanceof RampartInteger)) {
                            throw new InvalidRampartRuleException(
                                    "Not the expected integer value for \""
                                    + REFERENCE_LIMIT_KEY + "\" configuration.");
                        }
                        referenceLimit = (RampartInteger) refParam.getRampartObject();
                    } else if (refParam.getName().equals(REFERENCE_EXPANSION_LIMIT_KEY)) {
                        if (!(refParam.getRampartObject() instanceof RampartInteger)) {
                            throw new InvalidRampartRuleException(
                                    "Not the expected integer value for \""
                                    + REFERENCE_EXPANSION_LIMIT_KEY + "\" configuration.");
                        }
                        referenceExpansionLimit = (RampartInteger) refParam.getRampartObject();
                    } else {
                        throw new InvalidRampartRuleException(
                                "\"" + refParam.getName() + "\" is not a recognized \""
                                + namedValue.getName() + "\" parameter");
                    }
                }
            } else {
                throw new InvalidRampartRuleException(
                        "\"" + namedValue.getName() + "\" is not a recognized \""
                        + XXE_KEY + "\" parameter.");
            }
        }
        return new ExternalXmlEntityConfigImpl(uris, referenceLimit, referenceExpansionLimit);
    }


    // @Override
    public List<RampartConstant> allowedKeys() {
        return Collections.singletonList(XXE_KEY);
    }

}
