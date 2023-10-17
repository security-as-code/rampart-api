package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartAppImpl;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;

public class RampartAppBuilder implements RampartObjectBuilder<RampartApp> {
    private RampartVersion requiredLanguageVersion;
    private RampartString appName;
    private RampartInteger appVersion = newRampartInteger(1);
    private RampartRule[] rules;
    private RampartMetadata metadata;

    //@Override
    public RampartApp createRampartObject() {
        return new RampartAppImpl(appName, requiredLanguageVersion, appVersion, metadata, rules);
    }

    public RampartAppBuilder addRequiredLanguageVersion(RampartVersion version) {
        requiredLanguageVersion = version;
        return this;
    }

    public RampartAppBuilder addAppName(RampartString name) {
        appName = name;
        return this;
    }

    public RampartAppBuilder addAppVersion(RampartInteger version) {
        appVersion = version;
        return this;
    }

    public RampartAppBuilder addMetadata(RampartMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public RampartAppBuilder addRules(RampartRule[] rules) {
        this.rules = rules;
        return this;
    }

    public RampartVersion getRequiredLanguageVersion() {
        return requiredLanguageVersion;
    }

    public RampartString getAppName() {
        return appName;
    }

    public RampartInteger getAppVersion() {
        return appVersion;
    }

}
