package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartCodeImpl;

public class RampartCodeBuilder implements RampartObjectBuilder<RampartCode> {
    private RampartConstant language;
    private RampartVersion requiresVersion;
    private RampartString sourceCode;
    private RampartList imports = RampartList.EMPTY;

    public RampartCode createRampartObject() {
        return new RampartCodeImpl(language, requiresVersion, sourceCode, imports);
    }

    public RampartCodeBuilder addLanguage(RampartConstant language) {
        this.language = language;
        return this;
    }

    public RampartCodeBuilder addRequiresVersion(RampartVersion requiresVersion) {
        this.requiresVersion = requiresVersion;
        return this;
    }

    public RampartCodeBuilder addSourceCode(RampartString sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    /**
     * @param imports non empty RampartList of RampartStrings
     * @return
     */
    public RampartCodeBuilder addImports(RampartList imports) {
        this.imports = imports;
        return this;
    }
}
