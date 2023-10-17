package org.rampart.lang.utils;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartCodeImpl;
import org.rampart.lang.impl.core.RampartMetadataImpl;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.java.RampartPrimitives;

/** Some values that could be used for testing. */
public class Values {
    private Values() {
        throw new UnsupportedOperationException();
    }


    /** Some application names for testing. */
    public static final RampartString[] VALID_APPLICATION_NAMES = {
        RampartPrimitives.newRampartString("Hello, World"),
        RampartPrimitives.newRampartString("My first App"),
        RampartPrimitives.newRampartString("<<<!>>>"),
    };


    /** Some rule names for testing. */
    public static final RampartString[] VALID_RULE_NAMES = {
        RampartPrimitives.newRampartString("Hello, rule"),
        RampartPrimitives.newRampartString("My first rule"),
        RampartPrimitives.newRampartString("[[[*]]]"),
    };


    /** Lists of some OS specifications. */
    public static final RampartList[] VALID_OS_LISTS = {
        RampartPrimitives.newRampartList(RampartGeneralConstants.AIX_KEY),
        RampartPrimitives.newRampartList(RampartGeneralConstants.WINDOWS_KEY),
        RampartPrimitives.newRampartList(RampartGeneralConstants.WINDOWS_KEY, RampartGeneralConstants.LINUX_KEY),
        RampartPrimitives.newRampartList(RampartGeneralConstants.WINDOWS_KEY, RampartGeneralConstants.AIX_KEY),
    };

    /** Fake application (as the RAMPART API is not perfect). */
    public static final RampartApp FAKE_APPLICATION =
            new RampartApp() {
                @Override
                public RampartRuleIterator getRuleIterator() {
                    return null;
                }

                @Override
                public RampartVersion getRequiredVersion() {
                    return RampartVersionImpl.v1_0;
                }

                @Override
                public RampartMetadata getMetadata() {
                    return RampartMetadataImpl.EMPTY;
                }

                @Override
                public RampartInteger getAppVersion() {
                    return null;
                }

                @Override
                public RampartString getAppName() {
                    return RampartPrimitives.newRampartString("Hello, RAMPART!");
                }
            };

    /**
     * Creates a new Java code for RAMPART 2.8 level.
     * @param code code (body) to write.
     * @param imports import directives.
     * @return RAMPART code description.
     */
    public static RampartCode javaCodeBlockForRampart2_8(String code, String... imports) {
        final RampartObject[] elements = new RampartObject[imports.length];
        for (int i = 0; i < imports.length; i++) {
            elements[i] = RampartPrimitives.newRampartString(imports[i]);
        }

        return new RampartCodeImpl(
            RampartGeneralConstants.JAVA_KEY, RampartVersionImpl.v2_8,
            RampartPrimitives.newRampartString(code),
            RampartPrimitives.newRampartList(elements)
        );
    }
}