package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.impl.http.RampartHttpInputValidationImpl;

public class RampartHttpInputValidationBuilder implements RampartObjectBuilder<RampartHttpInputValidation> {
    private RampartHttpValidationType validationType;
    private RampartList targets = RampartList.EMPTY;
    private RampartList builtInMatchers = RampartList.EMPTY;
    private RampartString regexPattern;
    private RampartList omitMatchers = RampartList.EMPTY;

    public RampartHttpInputValidation createRampartObject() {
        return new RampartHttpInputValidationImpl(validationType, targets, builtInMatchers, regexPattern, omitMatchers);
    }

    public RampartHttpInputValidationBuilder addValidationType(RampartHttpValidationType validationType) {
        this.validationType = validationType;
        return this;
    }

    /**
     * @param targets RampartList of RampartStrings
     * @return
     */
    public RampartHttpInputValidationBuilder addTargets(RampartList targets) {
        this.targets = targets;
        return this;
    }

    /**
     * @param builtInMatchers non empty RampartList of RampartConstants
     * @return
     */
    public RampartHttpInputValidationBuilder addBuiltInMatchers(RampartList builtInMatchers) {
        this.builtInMatchers = builtInMatchers;
        return this;
    }

    public RampartHttpInputValidationBuilder addRegexPattern(RampartString regexPattern) {
        this.regexPattern = regexPattern;
        return this;
    }

    /**
     * @param omitMatchers non empty RampartList of RampartStrings
     * @return
     */
    public RampartHttpInputValidationBuilder addOmitMatchers(RampartList omitMatchers) {
        this.omitMatchers = omitMatchers;
        return this;
    }
}
