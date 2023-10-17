package org.rampart.lang.impl.http;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartHttpInputValidationImpl implements RampartHttpInputValidation {

    private final RampartHttpValidationType type;
    private final RampartList targets;
    private final RampartList omitRules;
    private final RampartList builtInMatchers;
    private final RampartString regexPattern;
    private final String toStringValue;
    private final int hashCode;

    public RampartHttpInputValidationImpl(RampartHttpValidationType type, RampartList targets, RampartList builtInMatchers, RampartString regexPattern, RampartList omitRules) {
        this.type = type;
        this.targets = targets;
        this.omitRules = omitRules;
        this.builtInMatchers = builtInMatchers;
        this.regexPattern = regexPattern;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(type, builtInMatchers, regexPattern, omitRules);
    }

    @Deprecated
    public RampartHttpInputValidationImpl(RampartHttpValidationType type, RampartList targets, RampartList builtInMatchers,
                                          RampartString regexPattern) {
        this(type, targets, builtInMatchers, regexPattern, null);
    }

    //@Override
    public RampartHttpValidationType getType() {
        return type;
    }

    //@Override
    public RampartList getTargets() {
        return targets;
    }

    //@Override
    public RampartList getBuiltInMatchers() {
        return builtInMatchers;
    }

    //@Override
    public RampartString getRegexPattern() {
        return regexPattern;
    }

    //@Override
    public RampartBoolean hasRegexPattern() {
        return regexPattern() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    //@Override
    public RampartBoolean hasTargets() {
        return targets() ?
                RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    //@Override
    public RampartBoolean hasOmitRule() {
        return omitRule() ?
                RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    //@Override
    public RampartList getOmitRules() {
        return omitRules;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartHttpInputValidationImpl)) {
            return false;
        }
        RampartHttpInputValidationImpl otherInputValidationImpl = (RampartHttpInputValidationImpl) other;
        return ObjectUtils.equals(type, otherInputValidationImpl.type)
                && ObjectUtils.equals(targets, otherInputValidationImpl.targets)
                && ObjectUtils.equals(builtInMatchers, otherInputValidationImpl.builtInMatchers)
                && ObjectUtils.equals(regexPattern, otherInputValidationImpl.regexPattern)
                && ObjectUtils.equals(omitRules, otherInputValidationImpl.omitRules);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(VALIDATE_KEY.toString()).append('(').append(type);
        if (targets()) {
            builder.append(": ").append(targets);
        }
        builder.append(", ");
        if (omitRule()) {
            builder.append(OMITS_KEY);
            appendOmitRules(builder.append(": "));
        } else {
            builder.append(IS_KEY);
            appendMatchers(builder.append(": "));
        }
        return builder.append(')').toString();
    }

    private void appendOmitRules(StringBuilder builder) {
        builder.append('[');
        String delim = "";
        RampartObjectIterator it = omitRules.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append('"').append(it.next()).append('"');
            delim = ", ";
        }
        builder.append(']');
    }

    private void appendMatchers(StringBuilder builder) {
        builder.append('[');
        String delim = "";
        RampartObjectIterator it = builtInMatchers.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            builder.append(delim).append(it.next());
            delim = ", ";
        }
        if (regexPattern()) {
            builder.append(delim).append(regexPattern.formatted());
        }
        builder.append(']');
    }

    private boolean regexPattern() {
        return regexPattern != null;
    }

    private boolean omitRule() {
        return omitRules != null
                && omitRules.isEmpty() == RampartBoolean.FALSE;
    }

    private boolean targets() {
        return targets != null
                && targets.isEmpty() == RampartBoolean.FALSE;
    }
}
