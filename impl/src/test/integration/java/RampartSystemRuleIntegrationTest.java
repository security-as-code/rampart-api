import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RampartSystemRuleIntegrationTest {
    private static final String WELL_FORMED_FILESYSTEM_RULE_APP =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        write(\"/etc/*\")\n"
                + "        allow(message: \"write log Message\", severity: Medium)\n"
                + "    endfilesystem\n"
                + "    filesystem(\"Another sample filesystem rule\"):\n"
                + "        read(\"/etc/passwd\")\n"
                + "        protect(message: \"read log Message\", severity: Very-High)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String FILESYSTEM_RULE_APP_FOR_DIRECTORY =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/*\")\n"
                + "        allow(severity: High)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String FILESYSTEM_RULE_APP_MULTIPLE_PATHS =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/*\",\"/opt/*\",\"/usr/lib/*\")\n"
                + "        allow(severity: High)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String FILESYSTEM_RULE_APP_INVALID_ACTION_TYPE =
            "app(\"Sample invalid app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/*\")\n"
                + "        allow(severity: \"High\")\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String FILESYSTEM_RULE_APP_TRAVERSAL =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        traversal()\n"
                + "        protect(severity: 3)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String FILESYSTEM_RULE_APP_RELATIVE_TRAVERSAL_INPUT_DETECT_ACTION =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        input(deserialization, database)"
                + "        traversal(relative)\n"
                + "        detect(message: \"blabla\", severity: High)\n"
                + "    endfilesystem\n"
                + "endapp";

    @Test
    public void validRampartRuleWithTwoRules() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(WELL_FORMED_FILESYSTEM_RULE_APP)), WELL_FORMED_FILESYSTEM_RULE_APP.length())
                .readApp();
        assertThat(app, RampartAppMatcher.equalTo(WELL_FORMED_FILESYSTEM_RULE_APP));
    }

    @Test
    public void validRampartRuleForDirectory() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(FILESYSTEM_RULE_APP_FOR_DIRECTORY)), FILESYSTEM_RULE_APP_FOR_DIRECTORY.length())
                .readApp();
        assertThat(app, RampartAppMatcher.equalTo(FILESYSTEM_RULE_APP_FOR_DIRECTORY));
    }

    @Test
    public void singleRuleToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(FILESYSTEM_RULE_APP_FOR_DIRECTORY)),
                FILESYSTEM_RULE_APP_FOR_DIRECTORY.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(FILESYSTEM_RULE_APP_FOR_DIRECTORY));
    }

    @Test
    public void singleRuleToStringImplementationMultiplePaths() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(FILESYSTEM_RULE_APP_MULTIPLE_PATHS)),
                FILESYSTEM_RULE_APP_MULTIPLE_PATHS.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(FILESYSTEM_RULE_APP_MULTIPLE_PATHS));
    }

    @Test
    public void invalidRuleWithInvalidActionType() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(FILESYSTEM_RULE_APP_INVALID_ACTION_TYPE)),
                FILESYSTEM_RULE_APP_INVALID_ACTION_TYPE.length()).readApp());
    }

    @Test
    public void validRuleWithTraversal() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(FILESYSTEM_RULE_APP_TRAVERSAL)),
                FILESYSTEM_RULE_APP_TRAVERSAL.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"Sample well formed app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        input(http)\n"
                        + "        traversal()\n"
                        + "        protect(severity: Low)\n"
                        + "    endfilesystem\n"
                        + "endapp"));
    }

    @Test
    public void validRuleWithTraversalAndInputsToStringImplementation() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(FILESYSTEM_RULE_APP_RELATIVE_TRAVERSAL_INPUT_DETECT_ACTION)),
                FILESYSTEM_RULE_APP_RELATIVE_TRAVERSAL_INPUT_DETECT_ACTION.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(FILESYSTEM_RULE_APP_RELATIVE_TRAVERSAL_INPUT_DETECT_ACTION));
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText =
                "app(\"Sample well formed app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/*\")\n"
                        + "        allow(message: \"test message\", severity: High, stacktrace: \"full\")\n"
                        + "    endfilesystem\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        input(http)\n"
                        + "        traversal()\n"
                        + "        protect(severity: Low)\n"
                        + "    endfilesystem\n"
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
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        input(http)\n"
                        + "        traversal()\n"
                        + "        protect(severity: Low)\n"
                        + "    endfilesystem\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void foreignKeysInOperationDeclaration() {
        String appText =
            "app(\"Mod for File read detect\"):\n"
            + "     requires(version: RAMPART/2.2)\n"
            + "     filesystem(\"File read detect\"):\n"
            + "        read(paths: [\"/home/mkennedy/my_testfile\", \"/tmp/foo\"])\n"
            + "        detect(message: \"file read detected\", severity: Low)\n"
            + "     endfilesystem\n"
            + "endapp";
        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void rampart2_3_ruleWithAllowActionWithStacktraceFull() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.3)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            allow(message: \"allowing to read a file test.tmp\", severity: Low, stacktrace: \"full\")\n"
                + "        endfilesystem\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void rampart2_4_ruleWithAllowActionAndStacktraceFull() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.4)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            allow(message: \"allowing to read a file test.tmp\", severity: Low, stacktrace: \"full\")\n"
                + "        endfilesystem\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void rampart2_8_traversalRuleDefaultsToHttpWhenNoInputIsProvided() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            traversal()\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        String expectedAppText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            input(http)\n"
                + "            traversal()\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(expectedAppText)));
    }


    @Test
    public void rampart2_8_InputWithReadIsNotValid() {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            input(http)\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }


    @Test
    public void rampart2_9_allowsInputWithRead() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            input(http)\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void rampart2_9_nputWithTraversalIsNotValid() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            input(http)\n"
                + "            traversal()\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }


    @Test
    public void rampart2_9_doesNotAddAddInputUnlessExplicitlyProvided() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    /** Test for RAMPART-314 bug. */
    public void rulesWithDifferentInputClausesAreNotEqualToEachOther() throws IOException {
        String appTextNoInput =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        String appTextInputHttp =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            input(http)\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        String appTextInputDatabase =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            input(database)\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";

        final RampartApp appNoInput =
                new StringRampartAppReader(appTextNoInput).readApps().iterator().next();
        final RampartApp appInputHttp =
                new StringRampartAppReader(appTextInputHttp).readApps().iterator().next();
        final RampartApp appInputDatabase =
                new StringRampartAppReader(appTextInputDatabase).readApps().iterator().next();
        assertThat(getFirstRule(appNoInput), not(equalTo(getFirstRule(appInputHttp))));
        assertThat(getFirstRule(appNoInput), not(equalTo(getFirstRule(appInputDatabase))));
        assertThat(getFirstRule(appInputDatabase), not(equalTo(getFirstRule(appInputHttp))));
    }


    @Test
    public void apiFilterAnyClauseIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Api filter\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            api(any)\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void apiFilterUrlPatternIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Api filter\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            api(\"/api/v1\", \"/api/v2/*\")\n"
                + "            protect(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void rampart2_8_InputWithApiIsNotValid() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            api(any)\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        InvalidRampartAppException ex = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputWithAllowActionIsNotValidForFileRead() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            input(database)\n"
                + "            read(\"/tmp/test.tmp\")\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputWithAllowActionIsNotValidForFileWrite() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            input(database)\n"
                + "            write(\"/tmp/test.tmp\")\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
    }


    @Test
    public void apiWithAllowActionIsNotValidForFileWrite() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "        filesystem(\"Whitelist - Detect read access to /tmp/test.tmp\"):\n"
                + "            api(\"/api\")\n"
                + "            write(\"/tmp/test.tmp\")\n"
                + "            allow(severity: High)\n"
                + "        endfilesystem\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
    }

    /** Retrieves rule from the app. */
    private RampartRule getFirstRule(RampartApp app) {
        return app.getRuleIterator().next();
    }

}
