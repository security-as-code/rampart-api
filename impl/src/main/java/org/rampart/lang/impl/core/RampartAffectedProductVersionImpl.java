package org.rampart.lang.impl.core;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.impl.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RampartAffectedProductVersionImpl implements RampartAffectedProductVersion {
    private final ArrayList<Range> ranges = new ArrayList<Range>();
    private final int hashCode;
    private final String toString;

    public RampartAffectedProductVersionImpl(final List<RampartString> fromVersions, final List<RampartString> toVersions) {
        if (fromVersions.size() != toVersions.size()) {
            throw new IllegalArgumentException("both fromVersions and toVersions arrays need to be of same size");
        }
        final Iterator<RampartString> fromVersionIt = fromVersions.iterator();
        final Iterator<RampartString> toVersionIt = toVersions.iterator();
        while (fromVersionIt.hasNext()) {
            this.ranges.add(createRange(fromVersionIt.next(), toVersionIt.next()));
        }
        this.hashCode = ObjectUtils.hash(ranges);
        this.toString = createToStringValue();
    }

    private String createToStringValue() {
        StringBuilder sb = new StringBuilder();
        if (ranges.size() == 1 && ranges.get(0).getFrom().equals(ranges.get(0).getTo())) {
            sb.append(ranges.get(0).getFrom().formatted());
        } else {
            sb.append('{').append(LINE_SEPARATOR);
            String separator = "\t\t";
            for (Range range : ranges) {
                sb.append(separator).append(range);
                separator = "," + LINE_SEPARATOR + "\t\t";
            }
            sb.append('}');
        }
        return sb.toString();
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
        } else if (!(other instanceof RampartAffectedProductVersionImpl)) {
            return false;
        }
        RampartAffectedProductVersionImpl otherRampartAffectedProductVersion = (RampartAffectedProductVersionImpl) other;
        if (ranges.size() != otherRampartAffectedProductVersion.ranges.size()) {
            return false;
        }
        return ranges.equals(otherRampartAffectedProductVersion.ranges);
    }

    private Range createRange(final RampartString fromVersion, final RampartString toVersion) {
        return new Range() {

            private final RampartString from;
            private final RampartString to;
            private final String toString;
            private final int hashCode;

            {
                this.from = fromVersion;
                this.to = toVersion;
                this.hashCode = ObjectUtils.hash(from, to);
                this.toString = createToStringValue();
            }

            private String createToStringValue() {
                return "range: {from: " + from.formatted() + ", to: " + to.formatted() + "}";
            }

            public RampartString getFrom() {
                return from;
            }

            public RampartString getTo() {
                return to;
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
                } else if (!(other instanceof Range)) {
                    return false;
                }
                Range otherRange = (Range) other;
                return ObjectUtils.equals(getFrom(), otherRange.getFrom())
                        && ObjectUtils.equals(getTo(), otherRange.getTo());
            }
        };
    }

    public RangeIterator getRangeIterator() {
        return new RangeIterator() {
            final Iterator<Range> it = ranges.iterator();

            public RampartBoolean hasNext() {
                return it.hasNext() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
            }

            public Range next() {
                return it.next();
            }
        };
    }
}
