package org.rampart.lang.impl.apiprotect.validators;

import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.apiprotect.RampartApiFilter;
import org.rampart.lang.api.constants.RampartApiProtectConstants;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.ValidationError;
import org.rampart.lang.impl.core.parsers.RampartRuleNameParser;
import org.rampart.lang.impl.core.validators.RuleStructureValidator;
import org.rampart.lang.impl.core.validators.TargetOSValidator;
import org.rampart.lang.impl.core.validators.v2.RampartCodeValidator2_8;
import org.rampart.lang.impl.core.validators.v2.RampartMetadataValidator;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import org.rampart.lang.java.builder.RampartApiBuilder;


/** Validator for the API Protect RAMPART/2.9. */
public final class RampartApiProtectValidator2_9 implements Validatable<RampartApiBuilder, InvalidRampartRuleException>{
    private final Map<String, RampartList> visitorSymbolTable;
    private final RampartCodeValidator2_8 codeValidator;
    private final RampartMetadataValidator metadataValidator;
    private final TargetOSValidator targetOsValidator;

    /** Keys that are used by this specific validator. */
    private static final RampartConstant[] OWN_KEYS = {
            RampartApiProtectConstants.APIPROTECT_KEY,
            RampartGeneralConstants.CODE_KEY,
            RampartGeneralConstants.SOURCE_CODE_KEY,
            RampartGeneralConstants.METADATA_KEY,
    };


    public RampartApiProtectValidator2_9(Map<String, RampartList> visitorSymbolTable) {
        final RampartList myNode = visitorSymbolTable.get(RampartApiProtectConstants.APIPROTECT_KEY.toString());
        this.visitorSymbolTable = visitorSymbolTable;
        this.codeValidator = new RampartCodeValidator2_8(
                visitorSymbolTable.get(RampartGeneralConstants.CODE_KEY.toString()),
                visitorSymbolTable.get(RampartGeneralConstants.SOURCE_CODE_KEY.toString())
            );
        this.metadataValidator = new RampartMetadataValidator(visitorSymbolTable.get(RampartGeneralConstants.METADATA_KEY.toString()));
        this.targetOsValidator = new TargetOSValidator(RampartInterpreterUtils.findRampartNamedValue(RampartGeneralConstants.OS_KEY, myNode));
 }


    //@Override
    public RampartApiBuilder validate() throws InvalidRampartRuleException {
        final RampartMetadata metadata;
        try {
            metadata = metadataValidator.validateMetadata();
        } catch (ValidationError ve) {
            throw new InvalidRampartRuleException(ve.getMessage(), ve);
        }
        final RampartApiInterceptionPoint interception = RampartInterceptionPointValidator.getInterceptionPoint2_9(visitorSymbolTable);
        final RampartApiBuilder rampartApiBuilder = new RampartApiBuilder()
            .addRuleName(RampartRuleNameParser.getRuleName(visitorSymbolTable, RampartApiProtectConstants.APIPROTECT_KEY))
            .addCode(codeValidator.validateCodeBlock())
            .addMetadata(metadata)
            .addTargetOSList(targetOsValidator.validateTargetOSList())
            .addRequestProcessingStage(interception.requestProcessingStage)
            .addUriPatterns(interception.uriPatterns);
        RuleStructureValidator.validateKeys2_9(
                RampartApiProtectConstants.APIPROTECT_KEY,
                visitorSymbolTable.keySet(),
                OWN_KEYS, RampartInterceptionPointValidator.interceptKeys2_9
        );
        return rampartApiBuilder;
    }


    /** Validates rule action against the list of data inputs. */
    public static void crossValidateInputAndActions(
            RampartConstant ruleTypeName,
            RampartAction action,
            RampartList inputs,
            RampartApiFilter apiFilter)
            throws InvalidRampartRuleException {
        if (action.getActionType() == RampartActionType.ALLOW
                && inputs != null
                && inputs.isEmpty() != RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(
                    "Invalid RAMPART " + ruleTypeName + " rule configuration," +
                    " ALLOW action is not valid when used with input source.");
        }

        if (action.getActionType() == RampartActionType.ALLOW
                && apiFilter != null) {
            throw new InvalidRampartRuleException(
                    "Invalid RAMPART " + ruleTypeName + " rule configuration," +
                    " ALLOW action is not valid when used with api filter.");
        }
    }
}
