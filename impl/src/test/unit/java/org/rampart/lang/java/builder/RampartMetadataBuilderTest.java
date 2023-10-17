package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.api.core.RampartMetadata;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartGeneralConstants.CWE_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RampartMetadataBuilderTest {
    private RampartMetadataBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartMetadataBuilder();
    }

    @Test
    public void createMetadataWithCwe() {
        String cwe = "CWE-917";
        RampartMetadata metadata = builder
                .addCweMetadata(newRampartList(newRampartString(cwe)), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsCwe(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCwe(), RampartListMatcher.containsInAnyOrder(newRampartString(cwe)));
    }

    @Test
    public void createMetadataWithMultipleCwes() {
        String cwe1 = "CWE-917";
        String cwe2 = "CWE-101";
        RampartMetadata metadata = builder
                .addCweMetadata(newRampartList(newRampartString(cwe1), newRampartString(cwe2)), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsCwe(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCwe(), RampartListMatcher.containsInAnyOrder(newRampartString(cwe1), newRampartString(cwe2)));
    }

    @Test
    public void createMetadataWithLoggableCwe() {
        String cwe = "CWE-917";
        RampartMetadata metadata = builder
                .addCweMetadata(newRampartList(newRampartString(cwe)), RampartBoolean.TRUE)
                .createRampartObject();

        assertThat(metadata.isLoggable(CWE_KEY), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.containsCwe(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCwe(), RampartListMatcher.containsInAnyOrder(newRampartString(cwe)));
    }

    @Test
    public void createMetadataWithCweAndCve() {
        String cwe = "CWE-917";
        String cve = "CVE-2020-12345";
        RampartMetadata metadata = builder
                .addCweMetadata(newRampartList(newRampartString(cwe)), RampartBoolean.TRUE)
                .addCveMetadata(newRampartList(newRampartString(cve)), RampartBoolean.TRUE)
                .createRampartObject();

        assertThat(metadata.containsCwe(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCwe(), RampartListMatcher.containsInAnyOrder(newRampartString(cwe)));
        assertThat(metadata.containsCve(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCve(), RampartListMatcher.containsInAnyOrder(newRampartString(cve)));
    }

    @Test
    public void createMetadataWithCvss() {
        float score = 9.8f;
        float version = 3.1f;
        String vector = "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H";
        RampartMetadata metadata = builder
                .addCvssMetadata(new RampartCvssBuilder()
                                .addScore(newRampartFloat(score))
                                .addVersion(newRampartFloat(version))
                                .addVector(newRampartString(vector))
                                .createRampartObject(), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsCvss(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCvss().getScore(), equalTo(newRampartFloat(score)));
        assertThat(metadata.getCvss().getVersion(), equalTo(newRampartFloat(version)));
        assertThat(metadata.getCvss().getVector(), equalTo(newRampartString(vector)));
    }

    @Test
    public void createMetadataWithDescription() {
        String description = "Forced OGNL evaluation, when evaluated on raw user input in tag attributes, may lead to remote code execution.";
        RampartMetadata metadata = builder
                .addDescriptionMetadata(newRampartString(description), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsDescription(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getDescription(), equalTo(newRampartString(description)));
    }

    @Test
    public void createMetadataWithMultipleAffectedOperatingSystem() {
        String os1 = "windows";
        String os2 = "linux";
        RampartMetadata metadata =
                builder.addAffectedOperatingSystemMetadata(newRampartList(newRampartString(os1), newRampartString(os2)),
                        RampartBoolean.FALSE).createRampartObject();

        assertThat(metadata.containsAffectedOperatingSystem(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getAffectedOperatingSystem(),
                RampartListMatcher.containsInAnyOrder(newRampartString(os1), newRampartString(os2)));
    }

    @Test
    public void createMetadataWithAffectedProductName() {
        String productName = "Struts 2";
        RampartMetadata metadata = builder
                .addAffectedProductNameMetadata(newRampartString(productName), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsAffectedProductName(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getAffectedProductName(), equalTo(newRampartString(productName)));
    }

    @Test
    public void createMetadataWithAffectedProductVersionMultipleRanges() {
        String expectedRangeStart1 = "2.0.0";
        String expectedRangeEnd1 = "2.5.25";
        String expectedRangeStart2 = "1.5.0";
        String expectedRangeEnd2 = "1.5.25";

        RampartMetadata metadata = builder
                .addAffectedProductVersionMetadata(
                        new RampartAffectedProductVersionBuilder()
                            .addRange(newRampartString(expectedRangeStart1), newRampartString(expectedRangeEnd1))
                            .addRange(newRampartString(expectedRangeStart2), newRampartString(expectedRangeEnd2))
                            .createRampartObject(),
                        RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsAffectedProductVersion(), equalTo(RampartBoolean.TRUE));
        RampartAffectedProductVersion.RangeIterator it = metadata.getAffectedProductVersion().getRangeIterator();
        assertThat(it.hasNext(), equalTo(RampartBoolean.TRUE));
        RampartAffectedProductVersion.Range firstRange = it.next();
        assertThat(firstRange.getFrom(), equalTo(newRampartString(expectedRangeStart1)));
        assertThat(firstRange.getTo(), equalTo(newRampartString(expectedRangeEnd1)));
        assertThat(it.hasNext(), equalTo(RampartBoolean.TRUE));
        RampartAffectedProductVersion.Range secondRange = it.next();
        assertThat(secondRange.getFrom(), equalTo(newRampartString(expectedRangeStart2)));
        assertThat(secondRange.getTo(), equalTo(newRampartString(expectedRangeEnd2)));
    }

    @Test
    public void createMetadataWithCreationTime() {
        String time = "2022 Dec 03 09:04:55";
        RampartMetadata metadata = builder
                .addCreationTimeMetadata(newRampartString(time), RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsCreationTime(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getCreationTime(), equalTo(newRampartString(time)));
    }

    @Test
    public void createMetadataWithVersion() {
        RampartInteger version = newRampartInteger(2);
        RampartMetadata metadata = builder
                .addVersionMetadata(version, RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.containsVersion(), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.getVersion(), equalTo(version));
    }

    @Test
    public void createMetadataNonStandard() {
        RampartNamedValue entry = newRampartNamedValue(newRampartConstant("foo"), newRampartString("bar"));
        RampartMetadata metadata = builder
                .addMetadata(entry, RampartBoolean.FALSE)
                .createRampartObject();

        assertThat(metadata.contains(entry.getName()), equalTo(RampartBoolean.TRUE));
        assertThat(metadata.get(entry.getName()), equalTo(entry.getRampartObject()));
    }
}
