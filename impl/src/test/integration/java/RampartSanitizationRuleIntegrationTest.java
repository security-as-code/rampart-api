import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;
import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.api.sanitization.RampartSanitization;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class RampartSanitizationRuleIntegrationTest {

    private static final String MOD_WITH_SINGLE_SANITIZATION_RULE =
            "app(\"sanitization mod\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endsanitization\n"
            + "endapp";

    private static final String MOD_WITH_SINGLE_SANITIZATION_RULE_WITH_IGNORE =
            "app(\"sanitization mod\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization with Ignore\"):\n"
            + "        request(paths: \"/api/user/email\")\n"
            + "        undetermined(values: safe, logging: on)\n"
            + "        ignore(payload: [\"*/*;q=0.8\", \"application/xml;q=0.9\"], attribute: [\"email\", \"sender\"])\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endsanitization\n"
            + "endapp";

    private static final String MOD_WITH_MULTIPLE_SANITIZATION_RULE_WITH_IGNORE =
            "app(\"sanitization mod\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe, logging: off)\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endsanitization\n"
            + "    sanitization(\"Test Sanitization with Ignore\"):\n"
            + "        request(paths: \"/api/user/email\")\n"
            + "        undetermined(values: safe, logging: on)\n"
            + "        ignore(payload: [\"*/*;q=0.8\", \"application/xml;q=0.9\"], attribute: [\"email\", \"sender\"])\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endsanitization\n"
            + "endapp";

    @Test
    public void validSanitizationRuleParsedSuccessfully() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(MOD_WITH_SINGLE_SANITIZATION_RULE);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        RampartRuleIterator iterator = mod.getRuleIterator();

        assertAll(() -> {
            assertThat(iterator.hasNext() == RampartBoolean.TRUE, equalTo(true));
            assertThat(iterator.next().getRuleName(), equalTo(newRampartString("Test Sanitization")));
        });
    }

    @Test
    public void validSanitizationRuleWithIgnoreParsedSuccessfully() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(MOD_WITH_SINGLE_SANITIZATION_RULE_WITH_IGNORE);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        RampartRuleIterator iterator = mod.getRuleIterator();

        assertAll(() -> {
            assertThat(iterator.hasNext() == RampartBoolean.TRUE, equalTo(true));
            assertThat(iterator.next().getRuleName(), equalTo(newRampartString("Test Sanitization with Ignore")));
        });
    }

    @Test
    public void singleRuleToStringImplementation() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(MOD_WITH_SINGLE_SANITIZATION_RULE_WITH_IGNORE);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        assertThat(mod, RampartAppMatcher.equalTo(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization with Ignore\"):\n"
                + "        request(paths: [\"/api/user/email\"])\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        ignore(payload: [\"*/*;q=0.8\", \"application/xml;q=0.9\"], attribute: [\"email\", \"sender\"])\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp"
        ));
    }

    @Test
    public void sanitizationRuleAllowsWildcardsUpTo2_8() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/2.7)\n"
                + "    sanitization(\"Test Sanitization with wildcard paths\"):\n"
                + "        request(paths: [\"*suffix\", \"/prefix/*\", \"*match*\"])\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp");

        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        assertThat(mod, RampartAppMatcher.equalTo(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/2.7)\n"
                + "    sanitization(\"Test Sanitization with wildcard paths\"):\n"
                + "        request(paths: [\"*suffix\", \"/prefix/*\", \"*match*\"])\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp"));
    }

    @Test
    public void sanitizationRuleAllowsWildcards() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization with wildcard paths\"):\n"
                + "        request(paths: [\"*suffix\", \"/prefix/*\", \"*match*\"])\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp");

        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        assertThat(mod, RampartAppMatcher.equalTo(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization with wildcard paths\"):\n"
                + "        request(paths: [\"*suffix\", \"/prefix/*\", \"*match*\"])\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp"));
    }

    @Test
    public void multipleRuleToStringImplementation() throws IOException {
        StringRampartAppReader reader = new StringRampartAppReader(MOD_WITH_MULTIPLE_SANITIZATION_RULE_WITH_IGNORE);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        assertThat(mod, RampartAppMatcher.equalTo(
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: off)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "    sanitization(\"Test Sanitization with Ignore\"):\n"
                + "        request(paths: [\"/api/user/email\"])\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        ignore(payload: [\"*/*;q=0.8\", \"application/xml;q=0.9\"], attribute: [\"email\", \"sender\"])\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp"
        ));
    }

    @Test
    public void appWithVersionLessThan2_3AndNoOtherRulesThrowsException()
            throws InvalidRampartAppException {
        final String modString =
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/2.2)\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> {
            StringRampartAppReader reader = new StringRampartAppReader(modString);
            reader.readApps();
        });

        assertThat(thrown.getMessage(), equalTo("app \"sanitization mod\" does not contain any valid rules"));
    }

    @Test
    public void verifyIgnoreWithoutURLAndPayload() throws IOException {
        final String modString =
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        ignore(attribute: [\"email\", \"sender\"])\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        StringRampartAppReader reader = new StringRampartAppReader(modString);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        RampartRuleIterator iterator = mod.getRuleIterator();

        assertAll(() -> {
            assertThat(iterator.hasNext() == RampartBoolean.TRUE, equalTo(true));
            assertThat(iterator.next().getRuleName(), equalTo(newRampartString("Test Sanitization")));
        });
    }

    @Test
    public void verifyIgnoreWithoutURLAndAttributes() throws IOException {
        final String modString =
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        ignore(payload: [\"*/*;q=0.8\", \"application/xml;q=0.9\"])\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        StringRampartAppReader reader = new StringRampartAppReader(modString);
        RampartApp mod = reader.readApps().toArray(new RampartApp[0])[0];
        RampartRuleIterator iterator = mod.getRuleIterator();

        assertAll(() -> {
            assertThat(iterator.hasNext() == RampartBoolean.TRUE, equalTo(true));
            assertThat(iterator.next().getRuleName(), equalTo(newRampartString("Test Sanitization")));
        });
    }

    @Test
    public void appWithInvalidHttpIOTypeThrowException() throws InvalidRampartAppException {
        final String mod =
                "app(\"sanitization mod\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        response()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> {
            StringRampartAppReader reader = new StringRampartAppReader(mod);
            reader.readApps();
        });

        assertThat(thrown.getMessage(), equalTo("\"response\" is not valid directive for RampartSanitization. Must be \"request\""));
    }


    @Test
    public void invalidRuleActionWithStacktrace() {
        String appText =
                "app(\"Sanitization App\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Sanitization rule\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"rule triggered - undetermined values safe\", severity: Medium, stacktrace: \"full\")\n"
                + "    endsanitization\n"
                + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("parameter \"stacktrace: \"full\"\" to the action \"protect\" is not supported"));
    }

    @Test
    public void simpleSanitizationRuleProgrammaticAccessWildcardedRequestUri() throws IOException {
        Optional<RampartApp> app =
                new StringRampartAppReader(MOD_WITH_SINGLE_SANITIZATION_RULE).readApps().stream().findAny();

        assertThat(app.isPresent(), equalTo(true));
        RampartRule rule = app.get().getRuleIterator().next();
        assertThat(rule, instanceOf(RampartSanitization.class));
        assertThat(((RampartSanitization) rule).getUriPaths(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
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
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void unrecognizedValuesCanHaveLoggingOff() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: off)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartRule rule = apps.iterator().next().getRuleIterator().next();
        assertThat(rule.getRuleName(), equalTo(newRampartString("Test Sanitization")));
        RampartSanitization rampartSanitization = (RampartSanitization) rule;
        assertThat(rampartSanitization.isUndeterminedValuesLoggingOn(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void unrecognizedValuesCanHaveLoggingOn() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartRule rule = apps.iterator().next().getRuleIterator().next();
        assertThat(rule.getRuleName(), equalTo(newRampartString("Test Sanitization")));
        RampartSanitization rampartSanitization = (RampartSanitization) rule;
        assertThat(rampartSanitization.isUndeterminedValuesLoggingOn(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void undeterminedUnsafeValuesCannotHaveLoggingOff() {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization Rule\"):\n"
                + "        request(paths: [\"/api/path\"])\n"
                + "        undetermined(values: unsafe, logging: off)\n"
                + "        ignore(payload: [\"foo\"], attribute: [\"bar\"])\n"
                + "        detect(message: \"log message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("Invalid Sanitization rule configuration. Unsupported combination of parameters. " +
                "\"logging\" can not be \"off\" when \"values\" is \"unsafe\""));
    }

    @Test
    public void undeterminedUnsafeValuesCanHaveEmptyLoggingParameter() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization Rule\"):\n"
                + "        request(paths: [\"/api/path\"])\n"
                + "        undetermined(values: unsafe)\n"
                + "        ignore(payload: [\"foo\"], attribute: [\"bar\"])\n"
                + "        detect(message: \"log message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartRule rule = apps.iterator().next().getRuleIterator().next();
        RampartSanitization rampartSanitization = (RampartSanitization) rule;
        assertThat(rampartSanitization.isUndeterminedValuesLoggingOn(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void appWithVersion2_9DoesNotThrowExceptionWithInvalidIgnoreParameters() throws Exception {
        String appText =
                "app(\"App with invalid ignore param\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    sanitization(\"Test Sanitization Rule\"):\n"
                        + "        request(paths: [\"/api/path\"])\n"
                        + "        undetermined(values: unsafe)\n"
                        + "        ignore(payload: [\"foo\"], other: [\"/api/\"])\n"
                        + "        detect(message: \"log message\", severity: High)\n"
                        + "    endsanitization\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartSanitization rule = (RampartSanitization) apps.iterator().next().getRuleIterator().next();
        assertThat(rule.getIgnore().getPayload(), equalTo(newRampartList(newRampartString("foo"))));
        assertThat(rule.getIgnore().getAttribute(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void appWithVersion2_10CannotHaveInvalidIgnoreParameters() throws Exception {
        String appText =
                "app(\"App with invalid ignore param\"):\n"
                        + "    requires(version: RAMPART/2.10)\n"
                        + "    sanitization(\"Test Sanitization Rule\"):\n"
                        + "        request(paths: [\"/api/path\"])\n"
                        + "        undetermined(values: unsafe)\n"
                        + "        ignore(payload: [\"foo\"], other: [\"/api/\"])\n"
                        + "        detect(message: \"log message\", severity: High)\n"
                        + "    endsanitization\n"
                        + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("Invalid Sanitization rule configuration. " +
                "Only expecting 'ignore' parameters 'payload' and/or 'attribute', but found invalid parameter \"other\""));
    }

    @Test
    public void appWithVersion2_3CanHaveBlankIgnoreValues() throws Exception {
        String appText =
                "app(\"App with invalid ignore param\"):\n"
                        + "    requires(version: RAMPART/2.3)\n"
                        + "    sanitization(\"Test Sanitization Rule\"):\n"
                        + "        request(paths: [\"/api/path\"])\n"
                        + "        undetermined(values: unsafe)\n"
                        + "        ignore(payload: [\"\"])\n"
                        + "        detect(message: \"log message\", severity: High)\n"
                        + "    endsanitization\n"
                        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartSanitization rule = (RampartSanitization) apps.iterator().next().getRuleIterator().next();
        assertThat(rule.getIgnore().getPayload(), equalTo(newRampartList(newRampartString(""))));
    }

    @Test
    public void appWithVersion2_10CannotHaveBlankIgnoreValues() throws Exception {
        String appText =
                "app(\"App with invalid ignore param\"):\n"
                        + "    requires(version: RAMPART/2.10)\n"
                        + "    sanitization(\"Test Sanitization Rule\"):\n"
                        + "        request(paths: [\"/api/path\"])\n"
                        + "        undetermined(values: unsafe)\n"
                        + "        ignore(payload: [\"\"])\n"
                        + "        detect(message: \"log message\", severity: High)\n"
                        + "    endsanitization\n"
                        + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"payload\" declaration in the sanitize rule must be a list of string values."));
    }
}