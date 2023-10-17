package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.impl.core.RampartAffectedProductVersionImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RampartAffectedProductVersionBuilder {
    private List<RampartString> fromVersions;
    private List<RampartString> toVersions;

    private RampartAffectedProductVersion _createRampartObject() {
        return new RampartAffectedProductVersionImpl(fromVersions, toVersions);
    }

    public RampartAffectedProductVersionBuilderSingleVersion setVersion(RampartString version) {
        return new RampartAffectedProductVersionBuilderSingleVersion(version);
    }

    public RampartAffectedProductVersionBuilderWithRanges addRange(RampartString fromVersion, RampartString toVersion) {
        return new RampartAffectedProductVersionBuilderWithRanges(fromVersion, toVersion);
    }

    public class RampartAffectedProductVersionBuilderWithRanges implements RampartObjectBuilder<RampartAffectedProductVersion> {
        private RampartAffectedProductVersionBuilderWithRanges(final RampartString fromVersion, final RampartString toVersion) {
            fromVersions = new ArrayList<RampartString>() {
                {
                    add(fromVersion);
                }
            };
            toVersions = new ArrayList<RampartString>() {
                {
                    add(toVersion);
                }
            };
        }

        public RampartAffectedProductVersionBuilderWithRanges addRange(RampartString fromVersion, RampartString toVersion) {
            fromVersions.add(fromVersion);
            toVersions.add(toVersion);
            return this;
        }

        public RampartAffectedProductVersion createRampartObject() {
            return _createRampartObject();
        }
    }


    public class RampartAffectedProductVersionBuilderSingleVersion
            implements RampartObjectBuilder<RampartAffectedProductVersion> {
        private RampartAffectedProductVersionBuilderSingleVersion(RampartString version) {
            fromVersions = Collections.singletonList(version);
            toVersions = Collections.singletonList(version);
        }

        public RampartAffectedProductVersion createRampartObject() {
            return _createRampartObject();
        }
    }
}
