package org.rampart.lang.api.core;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartApp extends RampartObject {

    RampartString getAppName();

    RampartVersion getRequiredVersion();

    RampartInteger getAppVersion();

    RampartRuleIterator getRuleIterator();

    RampartMetadata getMetadata();
}