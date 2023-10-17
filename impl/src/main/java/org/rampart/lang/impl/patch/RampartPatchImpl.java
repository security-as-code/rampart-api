package org.rampart.lang.impl.patch;

import static org.rampart.lang.api.core.RampartRuleType.PATCH;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.patch.RampartFunction;
import org.rampart.lang.api.patch.RampartLocation;
import org.rampart.lang.api.patch.RampartPatch;
import org.rampart.lang.impl.core.RampartRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * Class to model an Rampart Patch rule
 * Eg.
 *  patch("sample patch rule"):
 *      function("com/foo/bar.main([Ljava/lang/String;)V)
 *      entry()
 *      code(language: "java") {
 *          public void patch() {
 *              // patch code
 *          }
 *      }
 *  endpatch
 *
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 * @since 1.0
 */
public class RampartPatchImpl extends RampartRuleBase implements RampartPatch {
    private final RampartFunction function;
    private final RampartLocation location;
    private final String toStringValue;
    private final int hashCode;

    public RampartPatchImpl(RampartString appName, RampartString patchName, RampartFunction function, RampartCode code,
                            RampartLocation location, RampartList targetOSList, RampartMetadata metadata) {
        super(appName, patchName, code, targetOSList, metadata);
        this.function = function;
        this.location = location;
        this.hashCode = ObjectUtils.hash(patchName, function, code, location, super.hashCode());
        this.toStringValue = super.toString();
    }

    // @Override
    public RampartFunction getFunction() {
        return function;
    }

    // @Override
    public RampartLocation getLocation() {
        return location;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return PATCH;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartPatchImpl)) {
            return false;
        }
        RampartPatchImpl otherPatch = (RampartPatchImpl) other;
        return ObjectUtils.equals(getRuleName(), otherPatch.getRuleName())
                && ObjectUtils.equals(function, otherPatch.function)
                && ObjectUtils.equals(getCode(), otherPatch.getCode())
                && ObjectUtils.equals(location, otherPatch.location)
                && super.equals(otherPatch);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(function).append(LINE_SEPARATOR)
               .append('\t').append(location).append(LINE_SEPARATOR);
    }
}
