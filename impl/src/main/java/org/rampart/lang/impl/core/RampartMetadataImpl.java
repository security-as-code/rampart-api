package org.rampart.lang.impl.core;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.*;
import org.rampart.lang.impl.utils.ObjectUtils;

import java.util.*;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.toJavaBoolean;

public class RampartMetadataImpl implements RampartMetadata {

    public static final RampartMetadata EMPTY = new RampartMetadataImpl();
    private final Map<RampartConstant, RampartNamedValue> metadataMap;
    private final Set<RampartConstant> loggableMetadata;
    private final int hashCode;
    private final String toString;

    private RampartMetadataImpl() {
        this(Collections.<RampartConstant, RampartNamedValue>emptyMap(), Collections.<RampartConstant>emptySet());
    }

    public RampartMetadataImpl(Map<RampartConstant, RampartNamedValue> metadataMap, Set<RampartConstant> loggableMetadata) {
        this.metadataMap = metadataMap;
        this.loggableMetadata = loggableMetadata;
        this.hashCode = ObjectUtils.hash(metadataMap, this.loggableMetadata);
        this.toString = createToStringValue();
    }

    private RampartObject getValueOfStandardEntry(RampartConstant key) {
        RampartNamedValue namedValue = metadataMap.get(key);
        if (namedValue == null) {
            return null;
        }
        return namedValue.getRampartObject();
    }

    public RampartList getCwe() {
        return (RampartList) getValueOfStandardEntry(CWE_KEY);
    }

    public RampartBoolean containsCwe() {
        return contains(CWE_KEY);
    }

    public RampartList getCve() {
        return (RampartList) getValueOfStandardEntry(CVE_KEY);
    }

    public RampartBoolean containsCve() {
        return contains(CVE_KEY);
    }

    public RampartCvss getCvss() {
        return (RampartCvss) getValueOfStandardEntry(CVSS_KEY);
    }

    public RampartBoolean containsCvss() {
        return contains(CVSS_KEY);
    }

    public RampartString getDescription() {
        return (RampartString) getValueOfStandardEntry(DESCRIPTION_KEY);
    }

    public RampartBoolean containsDescription() {
        return contains(DESCRIPTION_KEY);
    }

    public RampartList getAffectedOperatingSystem() {
        return (RampartList) getValueOfStandardEntry(AFFECTED_OS_KEY);
    }

    public RampartBoolean containsAffectedOperatingSystem() {
        return contains(AFFECTED_OS_KEY);
    }

    public RampartString getAffectedProductName() {
        return (RampartString) getValueOfStandardEntry(AFFECTED_PRODUCT_NAME_KEY);
    }

    public RampartBoolean containsAffectedProductName() {
        return contains(AFFECTED_PRODUCT_NAME_KEY);
    }

    public RampartAffectedProductVersion getAffectedProductVersion() {
        return (RampartAffectedProductVersion) getValueOfStandardEntry(AFFECTED_PRODUCT_VERSION_KEY);
    }

    public RampartBoolean containsAffectedProductVersion() {
        return contains(AFFECTED_PRODUCT_VERSION_KEY);
    }

    public RampartString getCreationTime() {
        return (RampartString) getValueOfStandardEntry(CREATION_TIME_KEY);
    }

    public RampartBoolean containsCreationTime() {
        return contains(CREATION_TIME_KEY);
    }

    public RampartInteger getVersion() {
        return (RampartInteger) getValueOfStandardEntry(VERSION_KEY);
    }

    public RampartBoolean containsVersion() {
        return contains(VERSION_KEY);
    }

    public RampartBoolean contains(RampartConstant key) {
        return metadataMap.containsKey(key) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartObject get(RampartConstant key) {
        return metadataMap.containsKey(key) ? metadataMap.get(key).getRampartObject() : null;
    }

    public RampartObjectIterator getObjectIterator() {
        return new RampartObjectIterator() {
            private final Iterator<RampartNamedValue> iterator = metadataMap.values().iterator();

            // @Override
            public RampartBoolean hasNext() {
                return iterator.hasNext() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
            }

            // @Override
            public RampartNamedValue next() {
                return iterator.next();
            }
        };
    }

    public RampartBoolean isLoggable(RampartConstant key) {
        return loggableMetadata.contains(key) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartMetadata mergeWith(RampartMetadata other) {
        if (toJavaBoolean(other.isEmpty())) {
            return this;
        }
        HashMap<RampartConstant, RampartNamedValue> metadataMapCopy = new HashMap<RampartConstant, RampartNamedValue>(metadataMap);
        HashSet<RampartConstant> loggableMetadataCopy = new HashSet<RampartConstant>(this.loggableMetadata);
        RampartObjectIterator it = other.getObjectIterator();
        while (toJavaBoolean(it.hasNext())) {
            RampartNamedValue entry = (RampartNamedValue) it.next();
            if (!metadataMapCopy.containsKey(entry.getName())) {
                metadataMapCopy.put(entry.getName(), entry);
                if (other.isLoggable(entry.getName()) == RampartBoolean.TRUE) {
                    loggableMetadataCopy.add(entry.getName());
                }
            }
        }
        return new RampartMetadataImpl(metadataMapCopy, loggableMetadataCopy);
    }

    public RampartBoolean isEmpty() {
        return metadataMap.isEmpty() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartMetadataImpl)) {
            return false;
        }
        RampartMetadataImpl otherRampartMetadataImpl = (RampartMetadataImpl) other;
        return ObjectUtils.equals(metadataMap, otherRampartMetadataImpl.metadataMap) && ObjectUtils.equals(
                loggableMetadata, otherRampartMetadataImpl.loggableMetadata);
    }

    private String createToStringValue() {
        StringBuilder sb = new StringBuilder(METADATA_KEY.toString()).append("(").append(LINE_SEPARATOR).append("\t");
        String notLoggableMetadataSeparator = "";
        if (!loggableMetadata.isEmpty()) {
            sb.append(LOG_KEY).append(": {").append(LINE_SEPARATOR).append("\t\t");
            String separator = "";
            for (RampartConstant key : loggableMetadata) {
                sb.append(separator).append(metadataMap.get(key));
                separator = "," + LINE_SEPARATOR + "\t\t";
            }
            sb.append("}");
            notLoggableMetadataSeparator = "," + LINE_SEPARATOR + "\t";
        }
        for (RampartNamedValue metadata : metadataMap.values()) {
            if (!loggableMetadata.contains(metadata.getName())) {
                sb.append(notLoggableMetadataSeparator).append(metadata);
                notLoggableMetadataSeparator = "," + LINE_SEPARATOR + "\t";
            }
        }
        return sb.append(")").toString();
    }

}
