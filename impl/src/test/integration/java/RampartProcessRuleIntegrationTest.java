import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RampartProcessRuleIntegrationTest {
    private static final String PROCESS_RULE_APP =
            "app(\"process app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    process(\"Sample process rule\"):\n"
                    + "        execute(\"/bin/ls\")\n"
                    + "        protect(message: \"log Message\", severity: High)\n"
                    + "    endprocess\n"
                    + "endapp";

    private static final String PROCESS_RULE_APP_MULTIPLE_PATHS =
            "app(\"process app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    process(\"Sample process rule\"):\n"
                    + "        execute(\"/bin/chmod\", \"/bin/chown\", \"/bin/dnf\")\n"
                    + "        protect(message: \"log Message\", severity: Very-High)\n"
                    + "    endprocess\n"
                    + "endapp";

    private static final String MULTIPLE_PROCESS_RULE_APP =
            "app(\"process app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    process(\"Sample process rule 1\"):\n"
                    + "        execute(\"/bin/*\")\n"
                    + "        protect(message: \"log Message\", severity: High)\n"
                    + "    endprocess\n"
                    + "    process(\"Sample process rule 2\"):\n"
                    + "        execute(\"/bin/ls\")\n"
                    + "        allow(message: \"log Message\", severity: Low)\n"
                    + "    endprocess\n"
                    + "endapp";

    private static final String PROCESS_RULE_APP_INVALID_ACTION_TYPE =
            "app(\"process app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    process(\"Sample process rule\"):\n"
                    + "        execute(\"/bin/ls\")\n"
                    + "        protect(message: \"log Message\", severity: \"High\")\n"
                    + "    endprocess\n"
                    + "endapp";

    @Test
    public void validProcessRuleParsedSuccessfully() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(PROCESS_RULE_APP)),
                PROCESS_RULE_APP.length()).readApp();
        RampartRuleIterator iterator = app.getRuleIterator();
        assertAll(() -> {
            assertThat(iterator.hasNext() == RampartBoolean.TRUE, equalTo(true));
            assertThat(iterator.next().getRuleName(), equalTo(newRampartString("Sample process rule")));
        });
    }

    @Test
    public void processAppWithVersionLessThan2_0AndNoOtherRulesThrowsException() throws InvalidRampartAppException {
        String processRuleApp =
                "app(\"process app\"):\n"
                        + "    requires(version: \"RAMPART/1.4\")\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endprocess\n"
                        + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(processRuleApp)), processRuleApp.length()).readApp());

        assertThat(thrown.getMessage(), equalTo("app \"process app\" does not contain any valid rules"));
    }

    @Test
    public void singleRuleToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(PROCESS_RULE_APP)),
                PROCESS_RULE_APP.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(PROCESS_RULE_APP));
    }

    @Test
    public void multipleRuleToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(MULTIPLE_PROCESS_RULE_APP)),
                MULTIPLE_PROCESS_RULE_APP.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(MULTIPLE_PROCESS_RULE_APP));
    }

    @Test
    public void singleRuleToStringImplementationMultiplePaths() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(PROCESS_RULE_APP_MULTIPLE_PATHS)),
                PROCESS_RULE_APP_MULTIPLE_PATHS.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(PROCESS_RULE_APP_MULTIPLE_PATHS));
    }

    @Test
    public void invalidRuleWithInvalidActionType() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(PROCESS_RULE_APP_INVALID_ACTION_TYPE)),
                        PROCESS_RULE_APP_INVALID_ACTION_TYPE.length()).readApp());
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText =
                "app(\"process app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
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
                        + "    process(\"Sample process rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }


    @Test
    public void tainitingInputInRuleVersionBefore2_8() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.8)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        input(http)\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }


    @Test
    public void tainitingInputInRuleVersion2_9() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        input(http)\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }



    @Test
    public void apiFilterAnyClauseIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        api(any)\n"
                        + "        protect(severity: High)\n"
                        + "    endprocess\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void apiFilterUrlPatternIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        api(\"/api/v1\", \"/api/v2/*\")\n"
                        + "        protect(severity: High)\n"
                        + "    endprocess\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void apiFilterInRuleVersionBefore2_8() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.8)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        api(any)\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endprocess\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputWithAllowActionIsNotValid() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        input(http)\n"
                        + "        allow(message: \"log Message\", severity: High)\n"
                        + "    endprocess\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void apiWithAllowActionIsNotValid() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    process(\"Sample process rule\"):\n"
                        + "        execute(\"/bin/ls\")\n"
                        + "        api(\"/api\")\n"
                        + "        allow(message: \"log Message\", severity: High)\n"
                        + "    endprocess\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }
}
