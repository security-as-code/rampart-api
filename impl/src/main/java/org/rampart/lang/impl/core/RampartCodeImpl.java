package org.rampart.lang.impl.core;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.utils.ObjectUtils;

/**
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 */
public class RampartCodeImpl implements RampartCode {
    private final RampartConstant language;
    private final RampartVersion requiresVersion;
    private final RampartString sourceCode;
    private final RampartList imports;
    private final int hashCode;
    private final String toStringValue;

    public RampartCodeImpl(RampartConstant language, RampartVersion requiresVersion,
                           RampartString sourceCode, RampartList imports) {
        this.language = language;
        this.requiresVersion = requiresVersion;
        this.sourceCode = sourceCode;
        this.imports = imports;
        this.hashCode = ObjectUtils.hash(language, sourceCode, imports);
        this.toStringValue = createStringRepresentation();
    }

//    @Override
    public RampartConstant getLanguage() {
        return language;
    }

//    @Override
    public RampartString getSourceCode() {
        return sourceCode;
    }

//    @Override
    public RampartList getImports() {
        return imports;
    }

    @Override
    public String toString() {
       return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartCodeImpl)) {
            return false;
        }
        RampartCodeImpl otherCodeImpl = (RampartCodeImpl) other;
        return ObjectUtils.equals(language, otherCodeImpl.language)
                && ObjectUtils.equals(sourceCode, otherCodeImpl.sourceCode)
                && ObjectUtils.equals(imports, otherCodeImpl.imports);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(CODE_KEY.toString()).append('(').append(LANGUAGE_KEY).append(": ");
        if (requiresVersion.getMajor().isLessThan(newRampartInteger(2)) == RampartBoolean.TRUE) {
            builder.append('"').append(language).append('"');
        } else {
            builder.append(language);
        }
        if (imports.isEmpty() == RampartBoolean.FALSE) {
            builder.append(", ").append(IMPORT_KEY).append(": ").append(imports);
        }
        return builder.append("):")
                      .append(sourceCode)
                      .append("end").append(CODE_KEY)
                      .toString();
    }
}
