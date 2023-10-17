package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.patch.RampartFunction;
import org.rampart.lang.api.patch.RampartLocation;
import org.rampart.lang.api.patch.RampartPatch;
import org.rampart.lang.api.patch.RampartPatchType;
import org.rampart.lang.impl.patch.RampartFunctionImpl;
import org.rampart.lang.impl.patch.RampartLocationImpl;
import org.rampart.lang.impl.patch.RampartPatchImpl;
import org.rampart.lang.java.parser.RampartSingleAppVisitor;

/**
 * Class used by the validators to build an RampartPatchImpl
 * @see RampartPatchImpl
 * @see RampartSingleAppVisitor
 */
public class RampartPatchBuilder implements RampartRuleBuilder<RampartPatch> {
    private RampartString ruleName;
    private RampartString functionName;
    private RampartList checksumValues = RampartList.EMPTY;
    private RampartCode code;
    private RampartPatchType locationType;
    private RampartObject locationParameter;
    private RampartList locationOccurrenceValues = RampartList.EMPTY;
    private RampartList targetOSList;
    private RampartLocation location;
    private RampartFunction function;
    private RampartMetadata metadata;

    // @Override
    public RampartPatch createRampartRule(RampartString appName) {
        if (location != null || function != null) {
            return new RampartPatchImpl(appName, ruleName, function, code, location, targetOSList, metadata);
        }
        return new RampartPatchImpl(appName, ruleName, new RampartFunctionImpl(functionName, checksumValues),
                code, new RampartLocationImpl(locationType, locationParameter, locationOccurrenceValues),
                targetOSList, metadata);
    }

    //@Override
    public RampartPatchBuilder addRuleName(RampartString ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    //@Override
    public RampartPatchBuilder addCode(RampartCode code) {
        this.code = code;
        return this;
    }

    /**
     * @param targetOSList RampartList of RampartConstants
     * @return
     */
    //@Override
    public RampartPatchBuilder addTargetOSList(RampartList targetOSList) {
        this.targetOSList = targetOSList;
        return this;
    }

    public RampartRuleBuilder<RampartPatch> addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Deprecated: Will be removed in next version, use addFunction(RampartFuntion) instead
     */
    @Deprecated
    public RampartPatchBuilder addFunctionName(RampartString functionName) {
        this.functionName = functionName;
        return this;
    }

    /**
     * Deprecated: Will be removed in next version, use addLocation(RampartLocation) instead
     */
    @Deprecated
    public RampartPatchBuilder addLocationParameter(RampartObject locationParameter) {
        this.locationParameter = locationParameter;
        return this;
    }

    /**
     * Deprecated: Will be removed in next version, use addLocation(RampartLocation) instead
     */
    @Deprecated
    public RampartPatchBuilder addLocationType(RampartPatchType locationType) {
        this.locationType = locationType;
        return this;
    }

    /**
     * Deprecated: Will be removed in next version, use addFunction(RampartFuntion) instead
     */
    @Deprecated
    public RampartPatchBuilder addChecksums(RampartList checksumValues) {
        this.checksumValues = checksumValues;
        return this;
    }

    /**
     * Deprecated: Will be removed in next version, use addLocation(RampartLocation) instead
     */
    @Deprecated
    public RampartPatchBuilder addOccurrences(RampartList occurrenceValues) {
        this.locationOccurrenceValues = occurrenceValues;
        return this;
    }

    public RampartCode getRampartCode() {
        return code;
    }

    public RampartPatchBuilder addFunction(RampartFunction function) {
        this.function = function;
        return this;
    }

    public RampartPatchBuilder addLocation(RampartLocation location) {
        this.location = location;
        return this;
    }
}
