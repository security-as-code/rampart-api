package org.rampart.lang.impl.core;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.utils.ObjectUtils;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to model an Rampart App
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 */
public class RampartAppImpl implements RampartApp {

    private final RampartString appName;
    private final RampartVersion requiredVersion;
    private final RampartInteger appVersion;
    private final RampartRule[] rules;

    private RampartMetadata metadata = RampartMetadataImpl.EMPTY;

    private final int hashCode;
    private final String toStringValue;

    private volatile ConcurrentHashMap<String, Object> savedValues;

    public RampartAppImpl(
            RampartString appName,
            RampartVersion requiredVersion,
            RampartInteger appVersion,
            RampartMetadata metadata,
            RampartRule[] rules) {
        this.appName = appName;
        this.requiredVersion = requiredVersion;
        this.appVersion = appVersion;
        this.rules = rules;
        if (metadata != null) {
            this.metadata = metadata;
        }
        this.toStringValue = createStringRepresentation();
        for (RampartRule rule : rules) {
            ((RampartRuleBase) rule).setApp(this);
        }
        this.hashCode = ObjectUtils.hash(appName, requiredVersion, appVersion, metadata, rules);
    }

    //@Override
    public RampartString getAppName() {
        return appName;
    }

    //@Override
    public RampartVersion getRequiredVersion() {
        return requiredVersion;
    }

    // @Override
    public RampartInteger getAppVersion() {
        return appVersion;
    }

    // @Override
    public RampartMetadata getMetadata() {
        // For older RampartApp instances metadata = null
        return metadata;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    public void saveValue(String name, Object value) {
        if (savedValues == null) {
            synchronized (this) {
                if (savedValues == null) {
                    savedValues = new ConcurrentHashMap<String, Object>();
                }
            }
        }
        savedValues.put(name, value);
    }

    public Object restoreValue(String name) {
        if (savedValues != null) {
            return savedValues.get(name);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartAppImpl)) {
            return false;
        }
        RampartAppImpl otherApp = (RampartAppImpl) other;
        return ObjectUtils.equals(appName, otherApp.appName)
                && ObjectUtils.equals(requiredVersion, otherApp.requiredVersion)
                && ObjectUtils.equals(appVersion, otherApp.appVersion)
                && ObjectUtils.equals(metadata, otherApp.metadata)
                && ObjectUtils.equals(rules, otherApp.rules);
    }

    // @Override
    public RampartRuleIterator getRuleIterator() {
        return new RampartRuleIterator() {
            private int cursor = 0;

            // @Override
            public RampartBoolean hasNext() {
                return cursor != rules.length ? RampartBoolean.TRUE : RampartBoolean.FALSE;
            }

            // @Override
            public RampartRule next() {
                if (cursor >= rules.length) {
                    throw new NoSuchElementException();
                }
                return rules[cursor++];
            }
        };
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder("app(").append(appName.formatted()).append("):").append(LINE_SEPARATOR);
        appendRequiredVersion(builder);
        appendAppVersion(builder);
        if (!toJavaBoolean(metadata.isEmpty())) {
            builder.append('\t').append(metadata.toString().replaceAll(LINE_SEPARATOR, LINE_SEPARATOR + "\t"))
                    .append(LINE_SEPARATOR);
        }
        for (RampartRule rule : rules) {
            builder.append('\t').append(rule.toString().replaceAll(LINE_SEPARATOR, LINE_SEPARATOR + "\t"))
                    .append(LINE_SEPARATOR);
        }
        return builder.append("endapp").toString();
    }

    private void appendRequiredVersion(StringBuilder builder) {
        builder.append("\trequires(version: ");
        if (requiredVersion.greaterOrEqualThan(RampartVersionImpl.v2_0) == RampartBoolean.FALSE) {
            builder.append("\"RAMPART/").append(requiredVersion).append('"');
        } else {
            builder.append("RAMPART/").append(requiredVersion);
        }
        builder.append(')').append(LINE_SEPARATOR);
    }

    private void appendAppVersion(StringBuilder builder) {
        if (!appVersion.equals(newRampartInteger(1))) {
            builder.append('\t').append(VERSION_KEY).append('(').append(appVersion).append(')').append(LINE_SEPARATOR);
        }
    }
}
