import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.marshal.RampartMarshal;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.constants.RampartMarshalConstants;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import matchers.RampartAppMatcher;

public class RampartMarshalRuleIntegrationTest {

    private static final String INVALID_MARSHAL_RULE_APP_MULTIPLE_PROTECT_STRATEGY =
            "app(\"deserialization controls\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    marshal(\"Deserialization controls\"):\n"
            + "        deserialize(java)\n"
            + "        rce()\n"
            + "        dos()\n"
            + "        protect(message: \"attack found\")\n"
            + "    endmarshal\n"
            + "endapp";

    private static final String INVALID_MARSHAL_RULE_APP_UNSUPPORTED_DESERIALIZE_TYPE =
            "app(\"deserialization controls\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    marshal(\"Deserialization controls\"):\n"
            + "        deserialize(json)\n"
            + "        rce()\n"
            + "        dos()\n"
            + "        protect(message: \"attack found\")\n"
            + "    endmarshal\n"
            + "endapp";

    private static final String INVALID_APP_RCE_INVALID_ACTION_ALLOW =
            "app(\"marshal invalid action\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    marshal(\"invalid\"):\n"
                    + "        deserialize(java)\n"
                    + "        rce()\n"
                    + "        allow()\n"
                    + "    endmarshal\n"
                    + "endapp";

    private static final String INVALID_APP_DOS_INVALID_ACTION_ALLOW =
            "app(\"marshal invalid action\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    marshal(\"invalid\"):\n"
                    + "        deserialize(java)\n"
                    + "        dos()\n"
                    + "        allow()\n"
                    + "    endmarshal\n"
                    + "endapp";

    @Test
    public void validRampartRuleWithTwoRules() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for RCE\"):\n"
                + "        deserialize(java)\n"
                + "        rce()\n"
                + "        protect(message: \"attack found\", severity: 5)\n"
                + "    endmarshal\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(java)\n"
                + "        dos()\n"
                + "        protect(message: \"attack found\", severity: 5)\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for RCE\"):\n"
                        + "        deserialize(java)\n"
                        + "        rce()\n"
                        + "        protect(message: \"attack found\", severity: Medium)\n"
                        + "    endmarshal\n"
                        + "    marshal(\"Deserialization controls for DOS\"):\n"
                        + "        deserialize(java)\n"
                        + "        dos()\n"
                        + "        protect(message: \"attack found\", severity: Medium)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForRceToString() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for RCE\"):\n"
                + "        deserialize(java)\n"
                + "        rce()\n"
                + "        detect(message: \"attack found\")\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for RCE\"):\n"
                        + "        deserialize(java)\n"
                        + "        rce()\n"
                        + "        detect(message: \"attack found\", severity: Unknown)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForDosToString() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(java)\n"
                + "        dos()\n"
                + "        detect(message: \"attack found\")\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for DOS\"):\n"
                        + "        deserialize(java)\n"
                        + "        dos()\n"
                        + "        detect(message: \"attack found\", severity: Unknown)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void validXMLDeserialRuleForDosToString() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(xml)\n"
                + "        dos()\n"
                + "        detect(message: \"attack found\")\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for DOS\"):\n"
                        + "        deserialize(xml)\n"
                        + "        dos()\n"
                        + "        detect(message: \"attack found\", severity: Unknown)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void validDotnetDeserialRuleForDosToString() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(dotnet)\n"
                + "        dos()\n"
                + "        protect(message: \"attack found\", severity: High)\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for DOS\"):\n"
                        + "        deserialize(dotnet)\n"
                        + "        dos()\n"
                        + "        protect(message: \"attack found\", severity: High)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void multipleDeserialAPIRuleForDosToString() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(xml, java, dotnet)\n"
                + "        dos()\n"
                + "        protect(message: \"attack found\", severity: High)\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for DOS\"):\n"
                        + "        deserialize(xml, java, dotnet)\n"
                        + "        dos()\n"
                        + "        protect(message: \"attack found\", severity: High)\n"
                        + "    endmarshal\n"
                        + "endapp"));
    }

    @Test
    public void multipleDeserialAPIRuleForDos() throws IOException {
        String appText = "app(\"deserialization controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for DOS\"):\n"
                + "        deserialize(xml, java, dotnet)\n"
                + "        dos()\n"
                + "        protect(message: \"attack found\", severity: High)\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();
        RampartRule marshalRule = app.getRuleIterator().next();
        assertAll(() -> {
            assertThat(marshalRule, instanceOf(RampartMarshal.class));
            assertThat(((RampartMarshal) marshalRule).getRampartDeserializeTypes(), RampartListMatcher.containsInAnyOrder(
                    RampartMarshalConstants.XML_KEY,
                    RampartGeneralConstants.JAVA_KEY,
                    RampartGeneralConstants.DOTNET_KEY));
        });
    }

    @Test
    public void invalidRuleRCEAndDOS() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(INVALID_MARSHAL_RULE_APP_MULTIPLE_PROTECT_STRATEGY)),
                        INVALID_MARSHAL_RULE_APP_MULTIPLE_PROTECT_STRATEGY.length()).readApp());
    }

    @Test
    public void invalidRuleUnsupportedDeserializeType() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(INVALID_MARSHAL_RULE_APP_UNSUPPORTED_DESERIALIZE_TYPE)),
                        INVALID_MARSHAL_RULE_APP_UNSUPPORTED_DESERIALIZE_TYPE.length()).readApp());
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText =
                "app(\"deserialization controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls\"):\n"
                        + "        deserialize(java)\n"
                        + "        rce()\n"
                        + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                        + "    endmarshal\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        deserialize(java)\n"
                        + "        rce()\n"
                        + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                        + "    endmarshal\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        RampartObject value = ruleMetadata.get(newRampartConstant("foo"));
        assertThat(value, equalTo(newRampartString("bar")));
    }

    @Test
    public void metadataInRuleOlderVersion() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.5)\n"
                        + "    marshal(\"Deserialization controls\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        deserialize(java)\n"
                        + "        rce()\n"
                        + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                        + "    endmarshal\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void validRuleForXXEToString() throws Exception {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(uri: [\"http://struts.apache.org/dtds/struts-2.3.dtd\"],\n"
                + "            reference: {limit: 10, expansion-limit: 1024})\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(uri: [\"http://struts.apache.org/dtds/struts-2.3.dtd\"],\n"
                + "            reference: {limit: 10, expansion-limit: 1024})\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp"));

    }

    @Test
    public void invalidRuleWithBothRceAndXXEConfiguration() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        deserialize(java)\n"
                + "        rce()\n"
                + "        xxe()\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidRuleWithBothDosAndXXEConfiguration() {
        String appText =
                "app(\"app with xxe config\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"Deserialization controls for XXE\"):\n"
                        + "        deserialize(java)\n"
                        + "        dos()\n"
                        + "        xxe()\n"
                        + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                        + "    endmarshal\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }
    @Test
    public void validXXERuleWithSystemPath() throws IOException {
        String appText =
                "app(\"app with xxe for uris\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"rule name\"):\n"
                // there is no validation for the strings in the uri array,
                // so any value will be accepted and made available to the Agent or Portal
                + "        xxe(uri: [\"filename.dtd\", \"blah blah\"])\n"
                + "        allow(message: \"log message\", severity: High)\n"
                + "    endmarshal\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"app with xxe for uris\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    marshal(\"rule name\"):\n"
                        + "        xxe(uri: [\"filename.dtd\", \"blah blah\"])\n"
                        + "        allow(message: \"log message\", severity: High)\n"
                        + "    endmarshal\n"
                        + "endapp"
        ));
    }

    @Test
    public void invalidRuleWithXXE_invalidXXEConfig() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(\"stuff\")\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(ex.getMessage(), equalTo("Not the expected named value pairs for the \"xxe\" configuration."));
    }

    @Test
    public void invalidRuleWithXXE_invalidURIConfig() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(uri: blah)\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(ex.getMessage(), equalTo("Not the expected list of string values for \"uri\" configuration."));
    }

    @Test
    public void invalidRuleWithXXE_invalidReferenceConfig() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(reference: blah)\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(ex.getMessage(), equalTo("Not the expected list of named value pairs for \"reference\" configuration."));
    }

    @Test
    public void invalidRuleWithXXE_invalidReferenceLimitConfig() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(reference: {limit: \"blah\"})\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(ex.getMessage(), equalTo("Not the expected integer value for \"limit\" configuration."));
    }

    @Test
    public void invalidRuleWithXXE_invalidReferenceExpansionLimitConfig() {
        String appText =
                "app(\"app with xxe config\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    marshal(\"Deserialization controls for XXE\"):\n"
                + "        xxe(reference: {expansion-limit: \"blah\"})\n"
                + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
                + "    endmarshal\n"
                + "endapp";

        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(ex.getMessage(), equalTo("Not the expected integer value for \"expansion-limit\" configuration."));
    }

    @Test
    public void invalidDosInvalidActionALLOW() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(INVALID_APP_DOS_INVALID_ACTION_ALLOW)),
                        INVALID_APP_DOS_INVALID_ACTION_ALLOW.length()).readApp());
    }

    @Test
    public void invalidRceInvalidActionALLOW() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(INVALID_APP_RCE_INVALID_ACTION_ALLOW)),
                        INVALID_APP_RCE_INVALID_ACTION_ALLOW.length()).readApp());
    }

}
