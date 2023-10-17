package org.rampart.lang.impl.sanitization;

import static org.rampart.lang.api.core.RampartRuleType.SANITIZATION;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.api.sanitization.RampartIgnore;
import org.rampart.lang.api.sanitization.RampartSanitization;
import org.rampart.lang.api.sanitization.RampartUndetermined;
import org.rampart.lang.impl.core.RampartActionableRuleBase;
import org.rampart.lang.impl.utils.ObjectUtils;

//    app("SANITIZATION"):
//       requires(version: "RAMPART/2.2")
//
//        sanitization("Sanitization :02"):
//            request(paths: "/api/user/email")
//            undetermined(values: safe)
//            ignore(payload: ["info@rampart.org",
//                             "sales@rampart.org"],
//                   attribute: ["sender",
//                               "email"])
//            detect(message: "Detected payload for sanitization", severity: High)
//        endsanitization
//
//        sanitization("Sanitization :02"):
//            request(paths: ["/api/user/email", "/api/user/id"])
//            undetermined(values: unsafe)
//            ignore(payload: ["info@rampart.org",
//                             "sales@rampart.org"],
//                   attribute: ["sender",
//                               "email"])
//            detect(message: "Detected payload for sanitization", severity: High)
//        endsanitization
//    endapp
//    app("SANITIZATION"):
//       requires(version: "RAMPART/2.8")
//        sanitization("Sanitization :03"):
//            request(paths: "/api/user/email")
//            undetermined(values: safe, logging:off)
//            detect(message: "Detected payload for sanitization", severity: High)
//        endsanitization
//    endapp

public class RampartSanitizationImpl extends RampartActionableRuleBase implements RampartSanitization {

    private final String toStringValue;
    private final int hashCode;
    private final RampartHttpIOType httpIOType;
    private final RampartList uriPaths;
    private final RampartUndetermined undetermined;
    private final RampartIgnore ignore;

    public RampartSanitizationImpl(RampartString appName, RampartString ruleName,
                                   RampartAction action, RampartList targetOSList,
                                   RampartHttpIOType httpIOType, RampartList uriPaths,
                                   RampartUndetermined undetermined,
                                   RampartIgnore ignore, RampartMetadata metadata) {
        super(appName, ruleName, action, targetOSList, metadata);
        this.httpIOType = httpIOType;
        this.uriPaths = uriPaths;
        this.undetermined = undetermined;
        this.ignore = ignore;
        this.toStringValue = super.toString();
        this.hashCode = ObjectUtils.hash(super.hashCode(), this.httpIOType, this.uriPaths, this.undetermined, this.ignore);
    }

    @Override
    protected void appendRuleBody(StringBuilder builder) {
        builder.append('\t').append(httpIOType).append('(');
        if (uriPaths.isEmpty() == RampartBoolean.FALSE) {
            builder.append("paths: ").append(uriPaths);
        }
        builder.append(')').append(LINE_SEPARATOR);
        builder.append(undetermined);
        if (ignore != null) {
            builder.append(ignore);
        }
        super.appendRuleBody(builder);
    }

    // @Override
    public RampartHttpIOType getHttpIOType() {
        return httpIOType;
    }

    // @Override
    public RampartList getUriPaths() {
        return uriPaths;
    }

    // @Override
    public RampartBoolean areUndeterminedValuesSafe() {
        return undetermined.isSafe();
    }

    // @Override
    // @since 2.8
    public RampartBoolean isUndeterminedValuesLoggingOn() {
        return undetermined.shouldLog();
    }

    // @Override
    public RampartBoolean hasIgnore() {
        return (ignore == null) ? RampartBoolean.FALSE : RampartBoolean.TRUE;
    }

    // @Override
    public RampartIgnore getIgnore() {
        return ignore;
    }

    // @Override
    public RampartRuleType getRuleType() {
        return SANITIZATION;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RampartSanitizationImpl)) {
            return false;
        }
        RampartSanitizationImpl that = (RampartSanitizationImpl) obj;
        return super.equals(obj)
               && ObjectUtils.equals(httpIOType, that.httpIOType)
               && ObjectUtils.equals(uriPaths, that.uriPaths)
               && ObjectUtils.equals(undetermined, that.undetermined)
               && ObjectUtils.equals(ignore, that.ignore);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

}
