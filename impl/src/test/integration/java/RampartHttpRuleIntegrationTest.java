import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.api.http.RampartHttp;
import org.rampart.lang.api.http.RampartHttpFeaturePattern;
import org.rampart.lang.api.http.RampartHttpInputValidation;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import java.io.IOException;
import java.util.Collection;

import matchers.RampartListMatcher;
import org.junit.jupiter.api.Test;

public class RampartHttpRuleIntegrationTest {
    private static final String EMAIL_REGEX_EXP_VERIFIER = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\\\.[a-z0-9!#$%&'*"
            + "+/=?^_`{|}~-]+)*|\\\"(?:[\\\\x01-\\\\x08\\\\x0b\\\\x0c\\\\x0e-\\\\x1f\\\\x21"
            + "\\\\x23-\\\\x5b\\\\x5d-\\\\x7f]|\\\\\\\\[\\\\x01-\\\\x09\\\\x0b\\\\x0c\\\\x0e"
            + "-\\\\x7f])*\\\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\\\.)+[a-z0-9](?:"
            + "[a-z0-9-]*[a-z0-9])?|\\\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.){3}"
            + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\\\x01-\\\\x08"
            + "\\\\x0b\\\\x0c\\\\x0e-\\\\x1f\\\\x21-\\\\x5a\\\\x53-\\\\x7f]|\\\\\\\\[\\\\x01-"
            + "\\\\x09\\\\x0b\\\\x0c\\\\x0e-\\\\x7f])+)\\\\])";

    @Test
    public void validHttpRule1_4AppIsParsedSuccessfully() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void unsupportedMethodValidationType1_4Rule() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"invalid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"GET\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("http validate values must contain one, and only one, of: [cookie, csrf, header, parameter]"));
    }

    @Test
    public void methodValidationType1_6RuleSupported() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"GET\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"GET\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp")));
    }

    @Test
    public void methodValidationType1_6RuleUnsupportedIntegerEnforceType() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"invalid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"integer\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"integer\" is an invalid enforcement type"));
    }

    @Test
    public void methodValidationType1_6RuleSupportedListOfHttpMethods() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"GET\", \"POST\", \"DELETE\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [\"GET\", \"POST\", \"DELETE\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp")));
    }

    @Test
    public void methodValidationType1_6RuleEmptyEnforceType() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"invalid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(\"method\", enforce: [])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("enforce value list cannot be empty"));
    }

    @Test
    public void injectionHeaders1_6RuleSupported() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(uri:[\"/webapp/index.jsp\"])\n"
            + "        injection(\"headers\")\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void injectionHeaders1_6RuleUnsupportedForRequestDeclaration() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        injection(\"headers\")\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"request\" declaration is only supported with \"validate\" declaration"));
    }

    @Test
    public void injectionHeadersAndInputValidation1_6RuleUnsupported() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(uri:[\"/webapp/index.jsp\"])\n"
            + "        injection(\"headers\")\n"
            + "        validate(\"method\", enforce: [\"GET\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"response\" declaration is only supported with \"injection\" declaration"));
    }

    @Test
    public void emptyInjectionDeclaration1_6RuleUnsupported() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.6\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(uri:[\"/webapp/index.jsp\"])\n"
            + "        injection()\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("only a single parameter is allowed for \"injection\" declaration"));
    }

    @Test
    public void injectionHeaders2_2RuleSupported() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(paths:[\"/webapp/index.jsp\"])\n"
            + "        injection(headers)\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void injectionCookies2_2RuleSupported() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response()\n"
            + "        injection(cookies)\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void injectionHeaders2_2RuleUnsupportedForRequestDeclaration() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"valid http rule\"):\n"
            + "        request(paths:[\"/webapp/index.jsp\"])\n"
            + "        injection(headers)\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(),
                equalTo("invalid declaration of \"request\" with declaration of \"injection\""));
    }

    @Test
    public void emptyInjectionDeclaration2_2RuleUnsupported() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(paths:[\"/webapp/index.jsp\"])\n"
            + "        injection()\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"injection\" declaration must be followed by a non empty list"));
    }

    @Test
    public void injectionHeaders2_1RuleUnsupported() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.1)\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(paths:[\"/webapp/index.jsp\"])\n"
            + "        injection(headers)\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "\"injection\" is not a recognized declaration in rule \"valid http rule\""));
    }

    @Test
    public void injectionHeaders1_4RuleUnsupported() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"valid http rule\"):\n"
            + "        response(uri:[\"/webapp/index.jsp\"])\n"
            + "        injection(\"headers\")\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("unsupported element found. All http elements must be one of: "
                + "[action, http, request, validate]"));
    }

    @Test
    public void httpRuleAppWithCodeblockThrowsException() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     http(\"http with codeblock\"):\n"
            + "         request(paths: \"/webapp/index.jsp\")\n"
            + "         validate(parameters: \"sid\", is: integer)\n"
            + "         code(language: \"java\"):\n"
            + "             public void http() {\n"
            + "                 // code"
            + "             }\n"
            + "         endcode\n"
            + "     endhttp\n"
            + "endapp\n";
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "\"code\" is not a recognized declaration in rule \"http with codeblock\""));
    }

    @Test
    public void appSpecifyingTooLowVersionDoesNotParseHttpRuleApp() {
        // Note the specification of "RAMPART/1.2" as the app version. HTTP rules were introduced at 1.4
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.2\")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(\"/webapp/index.jsp\")\n"
            + "        validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "        action(protect: \"log message\", severity: \"Very-High\")\n"
            + "    endhttp\n"
            + "endapp\n";

        // Since there are no valid rules in the app we expect an exception to be thrown.
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("app \"Sample HTTP rule app\" does not contain any valid rules"));
    }

    @Test
    public void singleRule1_4ToStringImplementation() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(uri:[\"/webapp/index.jsp\"])\n"
            + "        validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "        action(protect: \"log message\", severity: \"Low\")\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void multiple1_4RuleToStringImplementation() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "     requires(version: \"RAMPART/1.4\")\n"
            + "     http(\"well formed http rule\"):\n"
            + "         request(uri: [\"/webapp/index.jsp\"])\n"
            + "         validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "         action(allow: \"log message\", severity: \"Medium\")\n"
            + "     endhttp\n"
            + "     http(\"Sample HTTP rule 2\"):\n"
            + "         request(uri: [\"/webapp/index.jsp\"])\n"
            + "         validate(parameter: [\"firstname\", \"surname\"], enforce: [\"[a-z]+\"])\n"
            + "         action(protect: \"firstname or surname are not within the correct range\", severity: \"Medium\")\n"
            + "     endhttp\n"
            + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "     requires(version: \"RAMPART/1.4\")\n"
            + "     http(\"well formed http rule\"):\n"
            + "         request(uri: [\"/webapp/index.jsp\"])\n"
            + "         validate(parameter: [\"sid\"], enforce: [\"integer\"])\n"
            + "         action(allow: \"log message\", severity: \"Medium\")\n"
            + "     endhttp\n"
            + "     http(\"Sample HTTP rule 2\"):\n"
            + "         request(uri: [\"/webapp/index.jsp\"])\n"
            + "         validate(parameter: [\"firstname\", \"surname\"], enforce: [\"[a-z]+\"])\n"
            + "         action(protect: \"firstname or surname are not within the correct range\", severity: \"Medium\")\n"
            + "     endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void invalidRuleWithInvalidActionType() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(paths: \"/webapp/index.jsp\")\n"
            + "        validate(parameters: \"sid\", is: integer)\n"
            + "        action(protect: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidInputValidationRuleBadRequestPathsFormat() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"invalid http rule\"):\n"
            + "        request(\"/webapp/index.jsp\")\n"
            + "        validate(parameters: [\"sid\"], is: [integer])\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Throwable thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"/webapp/index.jsp\" passed to \"request\" declaration"));
    }

    @Test
    public void inputValidationRule() throws IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(paths: \"/webapp/index.jsp\")\n"
            + "        validate(parameters: \"sid\", is: integer)\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule\"):\n"
            + "        request(paths: [\"/webapp/index.jsp\"])\n"
            + "        validate(parameters: [\"sid\"], is: [integer])\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void openRedirectRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http, database)\n"
            + "        open-redirect()\n"
            + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http, database)\n"
            + "        open-redirect()\n"
            + "        protect(message: \"open redirect attack occurred\", severity: Medium)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }


    @Test
    public void openRedirectRuleNoInputDeclared() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect()\n"
                + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        open-redirect()\n"
                + "        protect(message: \"open redirect attack occurred\", severity: Medium)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    @Test
    public void sessionFixationRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id, severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void headerSettingRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}}, message: \"HTTP response headers were set\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}},\n"
            + "        message: \"HTTP response headers were set\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void headerSettingRuleWithMultipleHeaderValueTypes() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: { set-header: {\n"
            + "             string-header: \"bar\",\n"
            + "             integer-header: 2,\n"
            + "             boolean-header: false,\n"
            + "             float-header: 2.3}},\n"
            + "        message: \"HTTP response headers were set\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: { set-header: {\n"
            + "             string-header: \"bar\",\n"
            + "             integer-header: 2,\n"
            + "             boolean-header: false,\n"
            + "             float-header: 2.3}},\n"
            + "        message: \"HTTP response headers were set\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void legacyCsrfSameOriginSyntax() throws IOException {
        String appText = "app(\"CSRF Same-Origins\"):\n"
                + "    requires(version: \"RAMPART/1.6\")\n"
                + "    http(\"Deny HTTP requests with invalid origin header\"):\n"
                + "        request(uri: [\"/OA_HTML/OA.jsp\"])\n"
                + "        validate(csrf: [\"origins\"],"
                + "                 hosts: [\"lvtserp.adb.org\"])\n"
                + "        action(detect: \"HTTP origin validation failed\", severity: 7)\n"
                + "    endhttp\n"
                + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo("app(\"CSRF Same-Origins\"):\n"
                + "    requires(version: \"RAMPART/1.6\")\n"
                + "    http(\"Deny HTTP requests with invalid origin header\"):\n"
                + "        request(uri: [\"/OA_HTML/OA.jsp\"])\n"
                + "        validate(csrf: [\"origins\"],"
                + "                 hosts: [\"lvtserp.adb.org\"])\n"
                + "        action(detect: \"HTTP origin validation failed\", severity: \"High\")\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    @Test
    public void csrfSynchronizedTokensRuleWithOptions() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF for http requests with options\"):\n"
            + "        request(paths: [\"/*index.html\", \"/FileServlet/*\", \"/*.jpg\", \"/*servletPath*\"])\n"
            + "        csrf(synchronized-tokens,\n"
            + "        options: {\n"
            + "            exclude: [\"/FileServlet/web.html\"],\n"
            + "            method: [GET, POST],\n"
            + "            token-type: shared,\n"
            + "            token-name: \"CUSTOM-XSRF-TKN\",\n"
            + "            ajax: no-validate})\n"
            + "        protect(message: \"CSRF validation failed\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF for http requests with options\"):\n"
            + "        request(paths: [\"/*index.html\", \"/FileServlet/*\", \"/*.jpg\", \"/*servletPath*\"])\n"
            + "        csrf(synchronized-tokens,\n"
            + "        options: {\n"
            + "            exclude: [\"/FileServlet/web.html\"],\n"
            + "            method: [GET, POST],\n"
            + "            token-name: \"CUSTOM-XSRF-TKN\",\n"
            + "            token-type: shared,\n"
            + "            ajax: no-validate})\n"
            + "        protect(message: \"CSRF validation failed\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void csrfSynchronizedTokensRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF for http requests\"):\n"
            + "        request(paths: [\"/*index.html\", \"/FileServlet/*\", \"/*.jpg\", \"/*servletPath*\"])\n"
            + "        csrf(synchronized-tokens)\n"
            + "        detect(message: \"CSRF validation failed\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF for http requests\"):\n"
            + "        request(paths: [\"/*index.html\", \"/FileServlet/*\", \"/*.jpg\", \"/*servletPath*\"])\n"
            + "        csrf(synchronized-tokens, options: {\n"
            + "             method: [POST],\n"
            + "             token-name: \"_X-CSRF-TOKEN\",\n"
            + "             token-type: shared,\n"
            + "             ajax: validate})\n"
            + "        detect(message: \"CSRF validation failed\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void csrfSameOriginRuleWithExcludeOptionIsInvalidIn2_5() throws InvalidRampartAppException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/2.5)\n"
                + "    http(\"Block HTTP requests with invalid origin header\"):\n"
                + "        request(paths: \"/webapp/index.jsp\")\n"
                + "        csrf(same-origin,\n"
                + "            options: {\n"
                + "                exclude: [\"/different_origin.html\"],\n"
                + "                hosts: [\"host1\", \"host2\", \"host3:8080\"]})\n"
                + "        protect()\n"
                + "    endhttp\n"
                + "endapp\n";

        Throwable thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("unsupported config \"exclude\" for \"options\" parameter in target \"same-origin\""));
    }

    @Test
    public void csrfSameOriginRuleWithOptions() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Block HTTP requests with invalid origin header\"):\n"
            + "        request(paths: \"/webapp/index.jsp\")\n"
            + "        csrf(same-origin,\n"
            + "            options: {\n"
            + "                exclude: [\"/different_origin.html\"],\n"
            + "                hosts: [\"host1\", \"host2\", \"host3:8080\"]})\n"
            + "        protect()\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Block HTTP requests with invalid origin header\"):\n"
            + "        request(paths: [\"/webapp/index.jsp\"])\n"
            + "        csrf(same-origin,\n"
            + "            options: {\n"
            + "                exclude: [\"/different_origin.html\"],\n"
            + "                hosts: [\"host1\", \"host2\", \"host3:8080\"]})\n"
            + "        protect(severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void csrfSameOriginRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Block HTTP requests with invalid origin header\"):\n"
            + "        request(paths: \"/webapp/index.jsp\")\n"
            + "        csrf(same-origin)\n"
            + "        protect(message: \"CSRF validation failed\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Block HTTP requests with invalid origin header\"):\n"
            + "        request(paths: [\"/webapp/index.jsp\"])\n"
            + "        csrf(same-origin)\n"
            + "        protect(message: \"CSRF validation failed\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    // inspired by MC-3371
    @Test
    public void csrfSTPWithExclude() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"CSRF STP Mod 3\"):\n" +
            "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n" +
            "    http(\"CSRF STP\"):\n" +
            "        request()\n" +
            "        csrf(synchronized-tokens, options: {exclude: [\"/OA_HTML/*\", \"*/forms/lservlet\", \"/\"], method: [GET], token-name: \"_X-CSRF-TOKEN\", token-type: shared, ajax: validate})\n" +
            "        detect(message: \"CSRF STP validation failed\", severity: Medium)\n" +
            "    endhttp\n" +
            "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void xssRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS reflected\"):\n"
            + "        response(paths: \"/profile\")\n"
            + "        input(http)\n"
            + "        xss(html)\n"
            + "        detect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS reflected\"):\n"
            + "        response(paths: [\"/profile\"])\n"
            + "        input(http)\n"
            + "        xss(html, options: {policy: loose})\n"
            + "        detect(message: \"XSS found\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void xssRuleWithPolicyOption() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {policy: strict})\n"
            + "        protect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {policy: strict})\n"
            + "        protect(message: \"XSS found\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void xssRuleWithExcludeOption() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {exclude: [\"/FileServlet/index.html\", \"/servletPath\"]})\n"
            + "        protect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {policy: loose, exclude: [\"/FileServlet/index.html\", \"/servletPath\"]})\n"
            + "        protect(message: \"XSS found\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void xssRuleWithExcludeOptionVersion2_4() {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.4)\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {exclude: [\"/FileServlet/index.html\", \"/servletPath\"]})\n"
            + "        protect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "option \"exclude\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void xssRuleWithExcludeAndPolicyStrictOption() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {policy: strict, exclude: [\"/FileServlet/index.html\", \"/servletPath\"]})\n"
            + "        protect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"XSS stored and reflected\"):\n"
            + "        response()\n"
            + "        input(database, http)\n"
            + "        xss(html, options: {policy: strict, exclude: [\"/FileServlet/index.html\", \"/servletPath\"]})\n"
            + "        protect(message: \"XSS found\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void xssRuleNoInputDeclared() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"XSS reflected\"):\n"
                + "        response()\n"
                + "        xss(html)\n"
                + "        detect(message: \"XSS found\", severity: 7)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"XSS reflected\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        xss(html, options: {policy: loose})\n"
                + "        detect(message: \"XSS found\", severity: High)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    @Test
    public void xssRuleNoInputDeclaredProgrammaticAccess() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"XSS reflected\"):\n"
                + "        response()\n"
                + "        xss(html)\n"
                + "        detect(message: \"XSS found\", severity: 7)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps.isEmpty(), equalTo(false));
        apps.forEach(
                app -> {
                    RampartRuleIterator it = app.getRuleIterator();
                    assertThat(it.hasNext(), equalTo(RampartBoolean.TRUE));
                    RampartRule httpRule = it.next();
                    assertThat(httpRule, instanceOf(RampartHttp.class));
                    assertThat(((RampartHttp) httpRule).getDataInputs(), equalTo(newRampartList(RampartInput.HTTP)));
                }
        );
    }

    @Test
    public void xssRuleWithAllowActionFailsValidation() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    http(\"XSS reflected\"):\n"
                        + "        response(paths: \"/profile\")\n"
                        + "        xss(html)\n"
                        + "        allow()\n"
                        + "    endhttp\n"
                        + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "invalid declaration of \"allow\" with declaration of \"xss\""));
    }

    @Test
    public void headerSettingRuleNoActionAttribute() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response, message: \"HTTP response headers were set\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"set-header\" for target \"http-response\""));
    }

    @Test
    public void sessionFixationRuleNoActionAttribute() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo(
                "action \"protect\" is missing the action attribute \"regenerate-id\" for target \"http-session\""));
    }

    @Test
    public void sessionFixationRuleDetectActionWithHttpSessionAttribute() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        detect(http-session: regenerate-id, message: \"session fixation detected\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("action type \"detect\" does not support targets and attributes"));
    }

    @Test
    public void sessionFixationRuleDetectAction() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        detect(message: \"session fixation detected\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        detect(message: \"session fixation detected\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void detectingResponseRuleDetectAction() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"detecting HTTP response\"):\n"
            + "        response()\n"
            + "        detect(message: \"http response has been sent\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"detecting HTTP response\"):\n"
            + "        response()\n"
            + "        detect(message: \"http response has been sent\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void headerSettingRuleDetectActionWithHttpResponseAttribute() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"set headers\"):\n"
            + "        response()\n"
            + "        detect(http-response: {set-header: {name: \"value\"}}, message: \"headers have been set\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("action type \"detect\" does not support targets and attributes"));
    }

    @Test
    public void settingHeadersDeclaresRequestAndResponse() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"set header with request and response events\"):\n"
            + "        request()\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {name: \"value\"}}, message: \"headers have been set\")\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("cannot declare both \"request\" and \"response\""));
    }

    @Test
    public void csrfAndInputValidationDeclaredTogether() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF and validate input for http requests\"):\n"
            + "        request()\n"
            + "        csrf(synchronized-tokens)\n"
            + "        validate(parameters: [\"firstname\", \"surname\"], is: \"[a-z]+\")\n"
            + "        detect(message: \"security event\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"validate\" and \"csrf\" cannot be declared together"));
    }

    @Test
    public void csrfAndXssDeclaredTogetherWithRequest() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF and XSS\"):\n"
            + "        request()\n"
            + "        csrf(synchronized-tokens)\n"
            + "        xss(html)\n"
            + "        protect()\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"xss\" and \"csrf\" cannot be declared together"));
    }

    @Test
    public void twoVersionsOfCsrfInSameRule() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"All CSRF features\"):\n"
            + "        request()\n"
            + "        csrf(synchronized-tokens)\n"
            + "        csrf(same-origin)\n"
            + "        protect()\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), startsWith("duplicate RAMPART object \"csrf\" specified in rule"));
    }

    @Test
    public void csrfAndXssWithRequestAndResponseDeclared() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF and XSS\"):\n"
            + "        request()\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        csrf(synchronized-tokens)\n"
            + "        xss(html)\n"
            + "        protect()\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("cannot declare both \"request\" and \"response\""));
    }

    @Test
    public void csrfFeatureWithAllowAction() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF\"):\n"
            + "        request()\n"
            + "        csrf(synchronized-tokens)\n"
            + "        allow()\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("invalid declaration of \"allow\" with declaration of \"csrf\""));
    }

    @Test
    public void openRedirectAndXssWithResponseDeclaration() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Protect agains Open Redirect and XSS\"):\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        open-redirect()\n"
            + "        xss(html)\n"
            + "        protect()\n"
            + "    endhttp\n"
            + "endapp\n";

    InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"xss\" and \"open-redirect\" cannot be declared together"));
    }

    @Test
    public void xssNotSupportedHttpSessionActionTarget() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Validate CSRF and XSS\"):\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        xss(html)\n"
            + "        protect(http-session: regenerate-id)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("declaration \"xss\" does not support action target \"http-session\""));
    }

    @Test
    public void csrfUnsupportedOnVersion1_4() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"Validate CSRF for http requests\"):\n"
            + "        request()\n"
            + "        csrf(synchronized-tokens)\n"
            + "        protect(message: \"CSRF validation failed\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void sessionFixationUnsupportedOnVersion1_4() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void settingHeadersUnsupportedOnVersion1_4() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}},\n"
            + "        message: \"HTTP response headers were set\")\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void xssUnsupportedOnVersion1_4() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"XSS reflected\"):\n"
            + "        response(\"/profile\")\n"
            + "        input(http)\n"
            + "        xss(html)\n"
            + "        detect(message: \"XSS found\", severity: 7)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void openRedirectUnsupportedOnVersion1_4() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http, database)\n"
            + "        open-redirect()\n"
            + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void openRedirectWithOptionsUnsupportedOnVersion2_1() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.1)\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        open-redirect(options: {exclude: subdomains})\n"
            + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Throwable thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"open-redirect\" must be an empty declaration"));
    }

    @Test
    public void openRedirectWithOptionExcludeSubdomains() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        open-redirect(options: {exclude: subdomains})\n"
            + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        open-redirect(options: {exclude: subdomains})\n"
            + "        protect(message: \"open redirect attack occurred\", severity: Medium)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void openRedirectWithShouldExcludeSubdomains() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect(options: {exclude: subdomains})\n"
                + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        RampartHttp rule = (RampartHttp) apps.iterator().next().getRuleIterator().next();

        assertThat(rule.getOpenRedirectConfiguration().shouldExcludeSubdomains(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void openRedirectNoOptionsRampartLatestVersion() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        open-redirect()\n"
            + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        open-redirect()\n"
            + "        protect(message: \"open redirect attack occurred\", severity: Medium)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void openRedirectWithListOfHostsUnsupportedOnVersion2_6() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.6)\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        open-redirect(hosts: [\"www.example.com\", \"www.example.net\"])\n"
            + "        allow(message: \"open redirect is allowed\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Throwable thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(),
                equalTo("invalid parameter \"hosts: [\"www.example.com\", \"www.example.net\"]\" for \"open-redirect\" declaration"));
    }

    @Test
    public void openRedirectAllowWithListOfHostsSupportedRampartLatestVersion() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        open-redirect(hosts: [\"www.example.com\", \"www.examples.net\"])\n"
            + "        allow(message: \"open redirect is allowed\", severity: 5)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"open redirect\"):\n"
            + "        response()\n"
            + "        input(http)\n"
            + "        open-redirect(hosts: [\"www.example.com\", \"www.examples.net\"])\n"
            + "        allow(message: \"open redirect is allowed\", severity: Medium)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void openRedirectAllowWithSingleHost() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect(hosts: \"www.example.com\")\n"
                + "        allow(message: \"open redirect to www.example.com is allowed\", severity: 9)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        open-redirect(hosts: [\"www.example.com\"])\n"
                + "        allow(message: \"open redirect to www.example.com is allowed\", severity: Very-High)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    // This rule gives NO protection, while at the same time it is a duplicate of
    // rule that protects against open redirect to any host, if such rule also exists.
    // So it makes no sense to actually use it.
    @Test
    public void openRedirectAllowWithNoHosts() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect()\n"
                + "        allow(message: \"open redirect is always allowed\", severity: 9)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        open-redirect()\n"
                + "        allow(message: \"open redirect is always allowed\", severity: Very-High)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }
    @Test
    public void openRedirectDetectWithSingleHost() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect(hosts: \"www.example.com\")\n"
                + "        detect(message: \"open redirect to www.example.com is allowed\", severity: 9)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        open-redirect(hosts: [\"www.example.com\"])\n"
                + "        detect(message: \"open redirect to www.example.com is allowed\", severity: Very-High)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    @Test
    public void openRedirectProtectWithSingleHost() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        open-redirect(hosts: \"www.example.com\")\n"
                + "        protect(message: \"open redirect to www.example.com is allowed\", severity: 9)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        response()\n"
                + "        input(http)\n"
                + "        open-redirect(hosts: [\"www.example.com\"])\n"
                + "        protect(message: \"open redirect to www.example.com is allowed\", severity: Very-High)\n"
                + "    endhttp\n"
                + "endapp\n")));
    }

    @Test
    public void headerSettingRuleWithHttpResponseActionTarget() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}})\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}}, severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void headerSettingRuleWithHttpResponseActionTargetAndMessageAndSeverity() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}},\n"
            + "                message: \"HTTP response headers were set\",\n"
            + "                severity: High)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"setting headers on HTTP response\"):\n"
            + "        response()\n"
            + "        protect(http-response: {set-header: {foo: \"bar\", something: 2}},\n"
            + "                message: \"HTTP response headers were set\",\n"
            + "                severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void sessionFixationRuleWithHttpSessionActionTargetAndMessage() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id, message: \"session hijack detected\")\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id, message: \"session hijack detected\", severity: Unknown)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void sessionFixationRuleWithHttpSessionActionTargetAndMessageAndSeverity() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id, message: \"session hijack detected\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Session fixation\"):\n"
            + "        request()\n"
            + "        authenticate(user)\n"
            + "        protect(http-session: regenerate-id, message: \"session hijack detected\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleWithRegex() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule with regex\"):\n"
            + "        request(paths:\"/webapp/index.jsp\")\n"
            + "        validate(parameters: \"email\", is: \"" + EMAIL_REGEX_EXP_VERIFIER + "\")\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule with regex\"):\n"
            + "        request(paths: [\"/webapp/index.jsp\"])\n"
            + "        validate(parameters: [\"email\"], is: [\"" + EMAIL_REGEX_EXP_VERIFIER + "\"])\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleWithRegexReparsingOutput() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule with regex\"):\n"
            + "        request(paths:\"/webapp/index.jsp\")\n"
            + "        validate(parameters: \"email\", is: \"" + EMAIL_REGEX_EXP_VERIFIER + "\")\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assumeFalse(apps.isEmpty());
        apps = new StringRampartAppReader(apps.iterator().next().toString()).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule with regex\"):\n"
            + "        request(paths: [\"/webapp/index.jsp\"])\n"
            + "        validate(parameters: [\"email\"], is: [\"" + EMAIL_REGEX_EXP_VERIFIER + "\"])\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleWithRegexEndsWithBackslash() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"well formed http rule with regex\"):\n"
            + "        request(paths:\"/webapp/index.jsp\")\n"
            + "        validate(parameters: \"email\", is: \"invalid regex\\\\\")\n"
            + "        protect(message: \"log message\", severity: Low)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("\"invalid regex\\\" is an invalid regex matcher"));
    }

    @Test
    public void inputValidationRuleWithOmitsRules() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: path, omits: [\"~\", \"should not include\"])\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: [path], omits: [\"~\", \"should not include\"])\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleWithSingleOmitsRule() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: path, omits: \"~\")\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: [path], omits: [\"~\"])\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleWithOmitsRulesUnknownTarget() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: url, omits: \"~\")\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputValidationRuleWithOmitsRulesInvalidRampartVersion() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.0)\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(request: path, omits: \"~\")\n"
            + "        protect(message: \"detected request containing '~' in the path\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(),
                equalTo("unrecognized parameter \"request: path\" to the \"validate\" declaration"));
    }

    @Test
    public void inputValidationRuleForMethodValidationType() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, is: GET)\n"
            + "        protect(message: \"detected request not matching method type\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, is: [GET])\n"
            + "        protect(message: \"detected request not matching method type\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleForMethodValidationTypeOmitsMatcher() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, omits: \"G*\")\n"
            + "        protect(message: \"detected request not matching method type\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("parameter \"method\" does not support \"omits\""));
    }

    @Test
    public void inputValidationRuleForMethodValidationTypeUnsupportedBefore2_2() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.1)\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, is: GET)\n"
            + "        protect(message: \"detected request not matching method type\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("unrecognized parameter \"method\" to the \"validate\" declaration"));
    }

    @Test
    public void inputValidationRuleForMethodValidationTypeMatcherMultipleValues() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, is: [GET, POST, PUT])\n"
            + "        protect(message: \"detected request not matching method type\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method, is: [GET, POST, PUT])\n"
            + "        protect(message: \"detected request not matching method type\", severity: High)\n"
            + "    endhttp\n"
            + "endapp\n")));
    }

    @Test
    public void inputValidationRuleForMethodValidationTypeNoMatchers() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample HTTP rule app\"):\n"
            + "    requires(version: RAMPART/2.2)\n"
            + "    http(\"Input validation\"):\n"
            + "        request()\n"
            + "        validate(method)\n"
            + "        protect(message: \"detected request not matching method type\", severity: 8)\n"
            + "    endhttp\n"
            + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(),
                equalTo("\"validate\" declaration must contain a key value pair with key \"is\""));
    }


    @Test
    public void inputValidationRuleForMethodValidationTypeAllowAction() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/2.2)\n"
                + "    http(\"Input validation\"):\n"
                + "        request(paths: [\"/path\"])\n"
                + "        validate(method, is: [GET])\n"
                + "        allow(message: \"allowing request path with invalid HTTP method\", severity: High)\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    // This is a test for the configuration that is a valid RAMPART syntax,
    // despite we do not actually implement this in W4J.
    // It should have fail the validation there.
    @Test
    public void setHeaderRuleHavingActionWithStacktraceShouldMakeItAvailable() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Sample HTTP rule app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"Add custom header to HTTP/S response\"):\n"
                + "        response()\n"
                + "        protect(http-response: {set-header: {Content-Security-Policy:"
                + " \"<policy-directive>; <policy-directive>\"}},"
                + " message: \"Setting Content-Security-Policy\","
                + " severity: High,"
                + " stacktrace: \"full\")\n"
                + "    endhttp\n"
                + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    http(\"open redirect\"):\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "        response()\n"
                + "        open-redirect()\n"
                + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
                + "    endhttp\n"
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
                + "    http(\"open redirect\"):\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "        response()\n"
                + "        open-redirect()\n"
                + "        protect(message: \"open redirect attack occurred\", severity: 5)\n"
                + "    endhttp\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputValidationRuleSupportsStacktrace() throws IOException {
        String appText =
                "app(\"HTTP Input Validation mod - with stacktrace\"):\n"
                + "    requires(version: RAMPART/2.5)\n"
                + "    http(\"HTTP single parameter validation\"):\n"
                + "        request(paths: [\"/spiracle/xss.jsp\"])\n"
                + "        validate(parameters: [\"number\"], is: [integer])\n"
                + "        protect(message: \"number parameter was not an integer\", severity: High, stacktrace: \"full\")\n"
                + "    endhttp\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    // based on JP-422 - part one, see regexp encoding
    @Test
    public void inputValidationUsingRegexp() throws IOException {
        String appText =
                "app(\"HTTP Input Validation using regexp\"):\n" +
                "    requires(version: RAMPART/2.7)\n" +
                "    http(\"CVE-2021-2109\"):\n" +
                "      request(paths: [\"/console/consolejndi.portal\"])\n" +
                "      validate(parameters: [\"JNDIBindingPortlethandle\"], is: [\".*((25[0-5]|(2[0-4]|1\\\\d|[1-9]|)\\\\d)\\\\.?\\\\b){4}.*\"])\n" +
                "      protect(message: \"Check the semicolon in the IP address\", severity: Medium)\n" +
                "    endhttp\n" +
                "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));

        RampartHttp rampartHttp = (RampartHttp) apps.iterator().next().getRuleIterator().next();
        assertThat(rampartHttp.getSecurityFeature(), equalTo(RampartHttpFeaturePattern.INPUT_VALIDATION));
        RampartHttpInputValidation inputValidation = rampartHttp.getInputValidation();
        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.PARAMETERS));
            assertThat(inputValidation.getTargets(), RampartListMatcher.containsInAnyOrder(newRampartString("JNDIBindingPortlethandle")));
            assertThat(inputValidation.hasRegexPattern(), equalTo(RampartBoolean.TRUE));
            assertThat(inputValidation.getRegexPattern(), equalTo(newRampartString(".*((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}.*")));
        });
    }

    // based on JP-422 - part two, see regexp encoding
    @Test
    public void inputValidationUsingRegexpWithoutEscaping() throws IOException {
        String appText =
                "app(\"HTTP Input Validation using regexp\"):\n" +
                "    requires(version: RAMPART/2.7)\n" +
                "    http(\"CVE-2021-2109\"):\n" +
                "      request(paths: [\"/console/consolejndi.portal\"])\n" +
                "      validate(parameters: [\"JNDIBindingPortlethandle\"], is: [\".*((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}.*\"])\n" +
                "      protect(message: \"Check the semicolon in the IP address\", severity: Medium)\n" +
                "    endhttp\n" +
                "endapp";
        RampartHttp rampartHttp = (RampartHttp) new StringRampartAppReader(appText).readApps().iterator().next().getRuleIterator().next();
        assertThat(rampartHttp.getSecurityFeature(), equalTo(RampartHttpFeaturePattern.INPUT_VALIDATION));
        RampartHttpInputValidation inputValidation = rampartHttp.getInputValidation();
        assertAll(() -> {
            assertThat(inputValidation.getType(), equalTo(RampartHttpValidationType.PARAMETERS));
            assertThat(inputValidation.getTargets(), RampartListMatcher.containsInAnyOrder(newRampartString("JNDIBindingPortlethandle")));
            assertThat(inputValidation.hasRegexPattern(), equalTo(RampartBoolean.TRUE));
            assertThat(inputValidation.getRegexPattern(), equalTo(newRampartString(".*((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}.*")));
        });
    }

}
