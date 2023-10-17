import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RampartLibraryRuleIntegrationTest {
    private static final String LIBRARY_RULE_APP =
            "app(\"library app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    library(\"Sample library rule\"):\n"
                    + "        load(\"some.so\")\n"
                    + "        protect(message: \"log Message\", severity: High)\n"
                    + "    endlibrary\n"
                    + "endapp";

    private static final String LIBRARY_RULE_APP_MULTIPLE_PATHS =
            "app(\"library app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    library(\"Sample library rule\"):\n"
                    + "        load(\"/usr/lib/*\", \"/lib/*\", \"/path/to/jdk/amd64/*\")\n"
                    + "        protect(message: \"log Message\", severity: High)\n"
                    + "    endlibrary\n"
                    + "endapp";

    private static final String MULTI_LIBRARY_RULE_APP =
            "app(\"library app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "     library(\"Sample library rule\"):\n"
                    + "         load(\"some.so\")\n"
                    + "         protect(message: \"log Message\", severity: Very-High)\n"
                    + "     endlibrary\n"
                    + "     library(\"library rule\"):\n"
                    + "         load(\"/usr/lib/*\")\n"
                    + "         allow(message: \"log Message\", severity: Low)\n"
                    + "     endlibrary\n"
                    + "endapp";

    private static final String LIBRARY_RULE_APP_INVALID_ACTION_TYPE =
            "app(\"library app\"):\n"
                    + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "    library(\"Sample library rule\"):\n"
                    + "        load(\"some.so\")\n"
                    + "        protect(message: \"log Message\", severity: \"High\")\n"
                    + "    endlibrary\n"
                    + "endapp";

    @Test
    public void validLibraryRuleParsedSuccessfully() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(LIBRARY_RULE_APP)),
                LIBRARY_RULE_APP.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(LIBRARY_RULE_APP));
    }

    @Test
    public void libraryAppWithVersionLessThan1_5AndNoOtherRulesThrowsException() throws InvalidRampartAppException {
        String libraryRuleApp =
                "app(\"library app\"):\n"
                        + "    requires(version: \"RAMPART/1.4\")\n"
                        + "    library(\"Sample library rule\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endlibrary\n"
                        + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(libraryRuleApp)),
                libraryRuleApp.length()).readApp());

        assertThat(thrown.getMessage(), equalTo("app \"library app\" does not contain any valid rules"));
    }

    @Test
    public void libraryAppWithMultipleLoadOperationDeclarations() throws InvalidRampartAppException {
        String ruleName = "Sample library rule";
        String libraryRuleApp =
                "app(\"library app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    library(\"" + ruleName + "\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        load(\"/usr/lib/*\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endlibrary\n"
                        + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(libraryRuleApp)),
                libraryRuleApp.length()).readApp());

        assertThat(thrown.getMessage(),
                equalTo("duplicate RAMPART object \"load\" specified in rule: [\"" + ruleName + "\"]"));
    }

    @Test
    public void singleRuleToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(LIBRARY_RULE_APP)),
                LIBRARY_RULE_APP.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(LIBRARY_RULE_APP));
    }

    @Test
    public void multipleRuleToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(MULTI_LIBRARY_RULE_APP)),
                MULTI_LIBRARY_RULE_APP.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(MULTI_LIBRARY_RULE_APP));
    }

    @Test
    public void singleRuleToStringImplementationMultiplePaths() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(LIBRARY_RULE_APP_MULTIPLE_PATHS)),
                LIBRARY_RULE_APP_MULTIPLE_PATHS.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(LIBRARY_RULE_APP_MULTIPLE_PATHS));
    }

    @Test
    public void invalidRuleWithInvalidActionType() {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(LIBRARY_RULE_APP_INVALID_ACTION_TYPE)),
                        LIBRARY_RULE_APP_INVALID_ACTION_TYPE.length()).readApp());
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText =
                "app(\"library app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    library(\"Sample library rule\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        protect(message: \"log Message\", severity: High, stacktrace: \"full\")\n"
                        + "    endlibrary\n"
                        + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    library(\"Sample library rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        load(\"some.so\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endlibrary\n"
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
                        + "    library(\"Sample library rule\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        load(\"some.so\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endlibrary\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void taintingInputsIsNotSupportedInVersion2_9() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    library(\"Sample library rule\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        input(http)\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "    endlibrary\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void apiFilterIsNotSupportedInVersion2_9() {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    library(\"Sample library rule\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        api(any)\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "    endlibrary\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

}
