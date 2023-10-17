package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartGeneralConstants.ALLOW_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.DETECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.PROTECT_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.SET_HEADER_KEY;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartActionWithAttributeValidator2_3Plus;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

public class HttpActionValidator2_3 extends RampartActionWithAttributeValidator2_3Plus {

    private static final EnumSet<RampartActionTarget> SUPPORTED_ACTION_TARGETS =
            EnumSet.of(RampartActionTarget.HTTP_RESPONSE, RampartActionTarget.HTTP_SESSION);

    public HttpActionValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.HTTP);
    }

    @Override
    protected RampartActionTarget lookForActionTarget(RampartConstant name) {
        RampartActionTarget target = RampartActionTarget.fromConstant(name);
        if (SUPPORTED_ACTION_TARGETS.contains(target)) {
            return target;
        }
        return null;
    }

    @Override
    protected RampartList validateAttributeConfigMap(RampartObject targetObjValue, RampartActionAttribute attributeType)
            throws InvalidRampartRuleException {
        switch (attributeType) {
            case REGENERATE_ID:
                if (!(targetObjValue instanceof RampartConstant)) {
                    throw new InvalidRampartRuleException(
                            "action target attribute \"" + attributeType + "\" must be a constant");
                }
                return RampartList.EMPTY;
            case SET_HEADER:
                return validateSetHeaderAttributeValues(
                        RampartInterpreterUtils.findRampartNamedValue(attributeType.getName(), targetObjValue));
            default:
                throw new InvalidRampartRuleException("unsupported attribute \"" + attributeType + "\"");
        }
    }

    private static RampartList validateSetHeaderAttributeValues(RampartObject values) throws InvalidRampartRuleException {
        if(!(values instanceof RampartList)) {
            throw new InvalidRampartRuleException("attribute \"" + SET_HEADER_KEY + "\" must have a list of values");
        }
        RampartObjectIterator it = ((RampartList) values).getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject value = it.next();
            if (!(value instanceof RampartNamedValue)
                    || !isInstanceOf(((RampartNamedValue) value).getRampartObject(),
                            RampartString.class, RampartBoolean.class, RampartFloat.class, RampartInteger.class)) {
                throw new InvalidRampartRuleException("\"" + SET_HEADER_KEY
                        + "\" attribute must contain name value pairs with string literals, booleans, floats or integers"
                        + " as values");
            }
        }
        return (RampartList) values;
    }

    private static boolean isInstanceOf(RampartObject obj, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(obj.getClass())) {
                return true;
            }
        }
        return false;
    }

    // @Override
    public List<RampartConstant> allowedKeys() {
        return Arrays.asList(ALLOW_KEY, DETECT_KEY, PROTECT_KEY);
    }
}
