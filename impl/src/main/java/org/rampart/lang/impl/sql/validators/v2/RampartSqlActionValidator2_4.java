package org.rampart.lang.impl.sql.validators.v2;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartActionAttribute;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartActionWithAttributeValidator2_3Plus;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.RampartPrimitives;

import java.util.List;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartGeneralConstants.CODE_KEY;
import static org.rampart.lang.api.constants.RampartGeneralConstants.NEW_RESPONSE_KEY;

public class RampartSqlActionValidator2_4 extends RampartActionWithAttributeValidator2_3Plus {
    public RampartSqlActionValidator2_4(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable, RampartRuleType.SQL);
    }

    public List<RampartConstant> allowedKeys() {
        return RampartSqlActionValidator.SUPPORTED_ACTIONS;
    }

    @Override
    protected RampartActionTarget lookForActionTarget(RampartConstant name) {
        RampartActionTarget target = RampartActionTarget.fromConstant(name);
        if (target != RampartActionTarget.HTTP_RESPONSE) {
            return null;
        }
        return RampartActionTarget.HTTP_RESPONSE;
    }

    @Override
    protected RampartList validateAttributeConfigMap(RampartObject targetObjValue, RampartActionAttribute attributeType)
            throws InvalidRampartRuleException {
        if (attributeType == RampartActionAttribute.NEW_RESPONSE) {
            return validateSendErrorAttributeValue(
                    RampartInterpreterUtils.findRampartNamedValue(RampartActionAttribute.NEW_RESPONSE.getName(), targetObjValue));
        }
        throw new InvalidRampartRuleException("unsupported attribute \"" + attributeType + "\"");
    }

    private static RampartList validateSendErrorAttributeValue(RampartObject values) throws InvalidRampartRuleException {
        if (!(values instanceof RampartList)) {
            throw new InvalidRampartRuleException("attribute \"" + NEW_RESPONSE_KEY + "\" must have a list of values");
        }
        RampartList valuesList = (RampartList) values;
        if (RampartPrimitives.toJavaInt(valuesList.size()) != 1) {
            throw new InvalidRampartRuleException(
                    "only a single setting must be defined for the \"" + NEW_RESPONSE_KEY + "\" attribute");
        }
        if (!(valuesList.getFirst() instanceof RampartNamedValue)
                || !CODE_KEY.equals(((RampartNamedValue) valuesList.getFirst()).getName())) {
            throw new InvalidRampartRuleException(
                    "only the \"" + CODE_KEY + "\" setting is supported for the attribute \"" + NEW_RESPONSE_KEY + "\"");
        }
        RampartObject statusCode = ((RampartNamedValue) valuesList.getFirst()).getRampartObject();
        if (!"400".equals(statusCode.toString())) {
            throw new InvalidRampartRuleException(
                    "\"" + statusCode + "\" is not a supported HTTP status code");
        }
        return valuesList;
    }
}
