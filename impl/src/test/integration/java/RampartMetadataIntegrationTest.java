import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.core.RampartAffectedProductVersion;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

import static org.rampart.lang.api.constants.RampartGeneralConstants.CVE_KEY;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

public class RampartMetadataIntegrationTest {

    private static String getAppWithMetadataEntry(String... entries) {
        String metadataString = "";
        String separator = "";
        for (String entry : entries) {
            metadataString += separator + entry;
            separator = ",";
        }
        return "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    metadata(\n"
                + "        " + metadataString + ")\n"
                + "    patch(\"well formed patch\"):\n"
                + "        function(\"com/foo/bar.main()V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";
    }

    private static String getRuleWithMetadataEntry(String... entries) {
        String metadataString = "";
        String separator = "";
        for (String entry : entries) {
            metadataString += separator + entry;
            separator = ",";
        }
        return "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"well formed patch\"):\n"
                + "        metadata(\n"
                + "            " + metadataString + ")\n"
                + "        function(\"com/foo/bar.main()V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";
    }

    @Test
    public void checkCweStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry("cwe: \"CWE-917\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getCwe(), RampartListMatcher.containsInAnyOrder(newRampartString("CWE-917")));
        assertThat(ruleMetadata.containsCwe(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkCveStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry("cve: \"CVE-2020-17530\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getCve(), RampartListMatcher.containsInAnyOrder(newRampartString("CVE-2020-17530")));
        assertThat(ruleMetadata.containsCve(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkCvssStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "cvss: {\n"
                + "    score: 9.8,\n"
                + "    version: 3.1,\n"
                + "    vector: \"CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H\"\n"
                + "}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getCvss().getScore(), equalTo(newRampartFloat(9.8f)));
        assertThat(ruleMetadata.getCvss().getVersion(), equalTo(newRampartFloat(3.1f)));
        assertThat(ruleMetadata.getCvss().getVector(), equalTo(newRampartString("CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H")));
        assertThat(ruleMetadata.containsCvss(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkDescriptionStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "description: \"Forced OGNL evaluation, when evaluated on raw user input in tag attributes, may lead to remote code execution.\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getDescription(), equalTo(newRampartString(
                "Forced OGNL evaluation, when evaluated on raw user input in tag attributes, may lead to remote code execution.")));
        assertThat(ruleMetadata.containsDescription(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkAffectedOperatingSystemStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-os: \"windows\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getAffectedOperatingSystem(), RampartListMatcher.containsInAnyOrder(newRampartString("windows")));
        assertThat(ruleMetadata.containsAffectedOperatingSystem(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkAffectedProductNameStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-name: \"Struts 2\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getAffectedProductName(), equalTo(newRampartString("Struts 2")));
        assertThat(ruleMetadata.containsAffectedProductName(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkAffectedProductVersionStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-version: {\n"
                + "    range: {from: \"2.0.0\", to: \"2.5.25\"}}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        RampartAffectedProductVersion.Range range = ruleMetadata.getAffectedProductVersion().getRangeIterator().next();
        assertThat(ruleMetadata.containsAffectedProductVersion(), equalTo(RampartBoolean.TRUE));
        assertThat(range, not(nullValue()));
        assertThat(range.getFrom(), equalTo(newRampartString("2.0.0")));
        assertThat(range.getTo(), equalTo(newRampartString("2.5.25")));
    }

    @Test
    public void checkTwoAffectedProductVersions() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-version: {\n"
                        + "\t\trange: {from: \"2.0.0\", to: \"2.5.25\"},\n"
                        + "\t\trange: {from: \"3.0.0\", to: \"3.2.1\"}}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        assertThat(ruleMetadata.containsAffectedProductVersion(), equalTo(RampartBoolean.TRUE));

        RampartAffectedProductVersion.RangeIterator rangeIterator = ruleMetadata.getAffectedProductVersion().getRangeIterator();

        RampartAffectedProductVersion.Range range = rangeIterator.next();
        assertThat(range, not(nullValue()));
        assertThat(range.getFrom(), equalTo(newRampartString("2.0.0")));
        assertThat(range.getTo(), equalTo(newRampartString("2.5.25")));

        range = rangeIterator.next();
        assertThat(range, not(nullValue()));
        assertThat(range.getFrom(), equalTo(newRampartString("3.0.0")));
        assertThat(range.getTo(), equalTo(newRampartString("3.2.1")));

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void checkCreationTimeStandardEntryAPI() throws Exception {
        String appText = getAppWithMetadataEntry(
                "creation-time: \"2022 Dec 03 09:04:55\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        assertThat(ruleMetadata.getCreationTime(), equalTo(newRampartString("2022 Dec 03 09:04:55")));
        assertThat(ruleMetadata.containsCreationTime(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkVersionStandardEntryAPIInRampartApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "version: 3");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        assertThat(ruleMetadata.getVersion(), equalTo(newRampartInteger(3)));
        assertThat(ruleMetadata.containsVersion(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void checkVersionStandardEntryAPIInRampartRule() throws Exception {
        String appText = getRuleWithMetadataEntry(
                "version: 3");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        assertThat(ruleMetadata.getVersion(), equalTo(newRampartInteger(3)));
        assertThat(ruleMetadata.containsVersion(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void appMetadataWithStandardEntryLoggable() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    metadata(\n"
                        + "        log: {\n"
                        + "            cve: \"CVE-2101-2221\""
                        + "        })\n"
                        + "    patch(\"well formed patch\"):\n"
                        + "        function(\"com/foo/bar.main()V\")\n"
                        + "        entry()\n"
                        + "        code(language: java):\n"
                        + "            public void patch(JavaFrame frame){\n"
                        + "                // patch code\n"
                        + "            }\n"
                        + "         endcode\n"
                        + "    endpatch\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.getCve(), RampartListMatcher.containsInAnyOrder(newRampartString("CVE-2101-2221")));
        assertThat(ruleMetadata.isLoggable(CVE_KEY), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void metadataEntryLoggableOverridenFromRule() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    metadata(cve: \"CVE-2101-2221\")\n"
            + "    patch(\"well formed patch\"):\n"
            + "        metadata(\n"
            + "            log: {\n"
            + "                cve: \"CVE-2101-2221\""
            + "            })\n"
            + "        function(\"com/foo/bar.main()V\")\n"
            + "        entry()\n"
            + "        code(language: java):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "         endcode\n"
            + "    endpatch\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.isLoggable(CVE_KEY), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void metadataEntryDescriptiveOverridenFromRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    metadata(\n"
                        + "        log: {\n"
                        + "            cve: \"CVE-2101-2221\""
                        + "        })\n"
                        + "    patch(\"well formed patch\"):\n"
                        + "        metadata(cve: \"CVE-2101-2221\")\n"
                        + "        function(\"com/foo/bar.main()V\")\n"
                        + "        entry()\n"
                        + "        code(language: java):\n"
                        + "            public void patch(JavaFrame frame){\n"
                        + "                // patch code\n"
                        + "            }\n"
                        + "         endcode\n"
                        + "    endpatch\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.isLoggable(CVE_KEY), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void toStringWithDescriptiveMetadataInAppNonStandardized() throws Exception {
        String appText = getAppWithMetadataEntry("foo: bar", "kitties: \"like to play\", policy-id: 2");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithLoggableMetadataInAppNonStandardized() throws Exception {
        String appText = getAppWithMetadataEntry(
            "log: {\n"
            + "     foo: bar,\n"
            + "     kitties: \"like to play\",\n"
            + "     policy-id: 2}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithSplitLoggableMetadataInAppNonStandardized() throws Exception {
        String appText = getAppWithMetadataEntry(
            "log: {foo: bar},\n"
            + "log: {kitties: \"like to play\"},\n"
            + "log: {policy-id: 2}");
        String expected = getAppWithMetadataEntry(
            "log: {\n"
            + "     foo: bar,\n"
            + "     kitties: \"like to play\",\n"
            + "     policy-id: 2}");
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringWithDescriptiveMetadataInRuleNonStandardized() throws Exception {
        String appText = getRuleWithMetadataEntry("foo: bar", "kitties: \"like to play\", policy-id: 2");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithLoggableMetadataInRuleNonStandardized() throws Exception {
        String appText = getRuleWithMetadataEntry(
                "log: {\n"
                        + "     foo: bar,\n"
                        + "     kitties: \"like to play\",\n"
                        + "     policy-id: 2}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithSplitLoggableMetadataInRuleNonStandardized() throws Exception {
        String appText = getRuleWithMetadataEntry(
                "log: {foo: bar},\n"
                        + "log: {kitties: \"like to play\"},\n"
                        + "log: {policy-id: 2}");
        String expected = getRuleWithMetadataEntry(
                "log: {\n"
                        + "     foo: bar,\n"
                        + "     kitties: \"like to play\",\n"
                        + "     policy-id: 2}");
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringWithDescriptiveMetadataCweInApp() throws Exception {
        String appText = getAppWithMetadataEntry("cwe: \"CWE-917\"");
        String expected = getAppWithMetadataEntry("cwe: [\"CWE-917\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringWithDescriptiveMetadataCveInApp() throws Exception {
        String appText = getAppWithMetadataEntry("cve: \"CVE-2020-17530\"");
        String expected = getAppWithMetadataEntry("cve: [\"CVE-2020-17530\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringWithDescriptiveMetadataCvssInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "cvss: {\n"
                        + "    score: 9.8,\n"
                        + "    version: 3.1,\n"
                        + "    vector: \"CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H\"\n"
                        + "}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataDescriptionInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "description: \"Forced OGNL evaluation, when evaluated on raw user input in tag attributes, may lead to remote code execution.\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataAffectedOperatingSystemInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-os: \"windows\"");
        String expected = getAppWithMetadataEntry(
                "affected-os: [\"windows\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringWithDescriptiveMetadataAffectedProductNameInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-name: \"Struts 2\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataAffectedProductVersionInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-version: {\n"
                        + "    range: {from: \"2.0.0\", to: \"2.5.25\"}}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataAffectedProductVersionInAppMultipleRanges() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-version: {\n"
                        + "    range: {from: \"2.0.0\", to: \"2.5.25\"},"
                        + "    range: {from: \"1.0.0\", to: \"1.5.25\"}}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataAffectedProductVersionInRuleMultipleRanges() throws Exception {
        String appText = getRuleWithMetadataEntry(
                "affected-product-version: {\n"
                        + "    range: {from: \"2.0.0\", to: \"2.5.25\"},"
                        + "    range: {from: \"1.0.0\", to: \"1.5.25\"}}");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataCreationTimeInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "creation-time: \"2022 Dec 03 09:04:55\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringWithDescriptiveMetadataVersionInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "version: 3");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void toStringReparseCweMetadataInApp() throws Exception {
        String appText = getAppWithMetadataEntry("cwe: \"CWE-917\"");
        String expected = getAppWithMetadataEntry("cwe: [\"CWE-917\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        apps = new StringRampartAppReader(apps.iterator().next().toString()).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringReparseCveMetadataInApp() throws Exception {
        String appText = getAppWithMetadataEntry("cve: \"CVE-2020-17530\"");
        String expected = getAppWithMetadataEntry("cve: [\"CVE-2020-17530\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        apps = new StringRampartAppReader(apps.iterator().next().toString()).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringReparseAffectedOperatingSystemMetadataInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-os: \"windows\"");
        String expected = getAppWithMetadataEntry(
                "affected-os: [\"windows\"]");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        apps = new StringRampartAppReader(apps.iterator().next().toString()).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(expected));
    }

    @Test
    public void toStringReparseAffectedProductNameMetadataInApp() throws Exception {
        String appText = getAppWithMetadataEntry(
                "affected-product-name: \"Struts 2\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        apps = new StringRampartAppReader(apps.iterator().next().toString()).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void metadataInAppOlderVersion() {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/2.5)\n"
                + "    metadata(\n"
                + "        foo: \"bar\")\n"
                + "    patch(\"well formed patch\"):\n"
                + "        function(\"com/foo/bar.main()V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "         endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void metadataNotFound() throws IOException {
        String appText = getAppWithMetadataEntry("foo: \"bar\"");

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();

        assertThat(ruleMetadata.contains(newRampartConstant("bar")), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void appMetadataDeclarationOrderDoesNotMatter() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    metadata(\n"
            + "        log: {\n"
            + "            cve: \"CVE-2101-2221\""
            + "        })\n"
            + "    version(3)\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    patch(\"well formed patch\"):\n"
            + "        function(\"com/foo/bar.main()V\")\n"
            + "        entry()\n"
            + "        code(language: java):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "         endcode\n"
            + "    endpatch\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    version(3)\n"
            + "    metadata(\n"
            + "        log: {\n"
            + "            cve: [\"CVE-2101-2221\"]"
            + "        })\n"
            + "    patch(\"well formed patch\"):\n"
            + "        function(\"com/foo/bar.main()V\")\n"
            + "        entry()\n"
            + "        code(language: java):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "         endcode\n"
            + "    endpatch\n"
            + "endapp"));
    }

    @Test
    public void appMetadataDeclarationBeforeRequiresWithUnsupportedVersion() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    metadata(\n"
                        + "        log: {\n"
                        + "            cve: \"CVE-2101-2221\""
                        + "        })\n"
                        + "    requires(version: RAMPART/2.5)\n"
                        + "    patch(\"well formed patch\"):\n"
                        + "        function(\"com/foo/bar.main()V\")\n"
                        + "        entry()\n"
                        + "        code(language: java):\n"
                        + "            public void patch(JavaFrame frame){\n"
                        + "                // patch code\n"
                        + "            }\n"
                        + "         endcode\n"
                        + "    endpatch\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }
}
