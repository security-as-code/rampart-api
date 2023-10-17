import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import org.rampart.lang.java.InvalidRampartSyntaxError;
import matchers.RampartAppMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartCommonRuleIntegrationWithLegacyRampartAppReaderTest {
    private static final String VALID_APP =
            "app(\"Sample well formed app\"):\n"
                    + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "     filesystem(\"Sample filesystem rule\"):\n"
                    + "        read(\"/etc/shadow\")\n"
                    + "        protect(message: \"log Message\", severity: 8)\n"
                    + "     endfilesystem\n"
                    + "endapp";

    private static final String INVALID_RAMPART_VERSION_TYPE_APP =
            "app(\"Sample well formed app\"):\n"
                    + "     requires(version: \"RAMPART/2.0\")\n"
                    + "     filesystem(\"Sample filesystem rule\"):\n"
                    + "        read(\"/etc/shadow\")\n"
                    + "        protect(message: \"log Message\", severity: 8)\n"
                    + "     endfilesystem\n"
                    + "endapp";

    private static final String APP_WITH_SYNTACTICALLY_INVALID_RULE =
            "app(\"Sample well formed app\"):\n"
                    + "     requires(version: \"RAMPART/1.4\")\n"
                    + "     bob:\n"
                    + "     enbob\n"
                    + "endapp";

    private static final String APP_WITHOUT_ENDAPP =
            "app(\"App which is missing endapp\"):\n"
                + "    requires(version: \"RAMPART/1.4\")\n"
                + "    patch(\"well formed patch\"):\n"
                + "        function(\"com/foo/bar.main()V\")\n"
                + "        entry()\n"
                + "        code(language: \"java\"):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "         endcode\n"
                + "    endpatch";

    private static final String MIXED_RULES_APP =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        write(\"/etc/shadow\")\n"
                + "        protect(message: \"write log Message\", severity: 4)\n"
                + "    endfilesystem\n"
                + "    filesystem(\"Another sample filesystem rule\"):\n"
                + "        read(\"/etc/passwd\")\n"
                + "        protect(message: \"read log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "    http(\"Sample http input validation rule\"):\n"
                + "        request()\n"
                + "        validate(parameters: \"sid\", is: integer)\n"
                + "        detect(message: \"log message\", severity: 3)\n"
                + "    endhttp\n"
                + "    library(\"Sample library rule\"):\n"
                + "        load(\"some.so\")\n"
                + "        detect(message: \"log message\", severity: 3)\n"
                + "    endlibrary\n"
                + "    process(\"Sample process rule\"):\n"
                + "        execute(\"/bin/ls\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endprocess\n"
                + "    sql(\"controls for SQL operations\"):\n" 
                + "        vendor(sybase)\n"
                + "        input(http, database, deserialization)\n"
                + "        injection(failed-attempt)\n"
                + "        protect(message: \"denying sql injections\", severity: 5)\n"
                + "    endsql\n"
                + "    marshal(\"Deserialization controls\"):\n"
                + "        deserialize(java)\n"
                + "        rce()\n"
                + "        protect(message: \"attack found\", severity: 5)\n"
                + "    endmarshal\n"
                + "    socket(\"Blocking server binds on all interfaces and all ports\"):\n"
                + "        bind(server: \"0.0.0.0:0\")\n"
                + "        protect(message: \"port binding blocked\", severity: 8)\n"
                + "    endsocket\n"
                + "    dns(\"Detecting address resolution for rampart.org\"):\n"
                + "        lookup(\"rampart.org\")\n"
                + "        protect(message: \"dns lookup occurred for rampart.org\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

    private static final String DUPLICATE_RULE_NAME_APP =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        write(\"/etc/shadow\")\n"
                + "        protect(message: \"write log Message\", severity: 4)\n"
                + "    endfilesystem\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/passwd\")\n"
                + "        protect(message: \"read log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String DUPLICATE_RULE_NAME_ACROSS_DIFFERENT_APPS =
            "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp\n"
                + "app(\"Another Sample well formed app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

    private static final String APP_WITH_UNKNOWN_RULE =
            "app(\"App with no valid rules\"):\n"
                + "    requires(version: \"RAMPART/1.1\")\n"
                + "    bob():"
                + "    endbob"
                + "    endapp";

    private static final String APP_WITH_MULTIPLE_UNKNOWN_RULES =
            "app(\"App with no valid rules\"):\n"
                + "    requires(version: \"RAMPART/1.1\")\n"
                + "    bob():"
                + "    endbob"
                + "    alice():"
                + "    endalice"
                + "    endapp";

    private static final String APP_WITH_VALID_AND_UNKNOWN_RULES =
            "app(\"App with no valid rules\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "    alice():"
                + "    endalice"
                + "    endapp";

    @Test
    public void missingEndAppThrowsException() {
        assertThrows(InvalidRampartSyntaxError.class,
                () -> new RampartAppReader(new BufferedReader(
                    new StringReader(APP_WITHOUT_ENDAPP)), APP_WITHOUT_ENDAPP.length())
                    .readApp());
    }

    @Test
    public void appWithMixedRulesIsParsedSuccessfully() throws IOException {
        new RampartAppReader(new BufferedReader(
                new StringReader(MIXED_RULES_APP)), MIXED_RULES_APP.length())
                .readApp();
    }

    @Test
    public void appWithValidAndInvalidIsParsedSuccessfully() throws IOException {
        new RampartAppReader(new BufferedReader(new StringReader(APP_WITH_VALID_AND_UNKNOWN_RULES)),
                APP_WITH_VALID_AND_UNKNOWN_RULES.length()).readApp();
    }

    @Test
    public void appWithWithDuplicateRuleNamesThrowsException() {
        String ruleName = "Sample filesystem rule";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(
                        new BufferedReader(new StringReader(DUPLICATE_RULE_NAME_APP)), DUPLICATE_RULE_NAME_APP.length())
                        .readApp());

        assertThat(thrown.getMessage(),
                equalTo("duplicate rule name within the same app. Offending rule name \"" + ruleName + "\""));
    }

    @Test
    public void duplicateRuleNamesAcrossDifferentAppsIsParsedSuccessfully() {
        RampartAppReader reader = new RampartAppReader(new BufferedReader(
                        new StringReader(DUPLICATE_RULE_NAME_ACROSS_DIFFERENT_APPS)),
                        DUPLICATE_RULE_NAME_ACROSS_DIFFERENT_APPS.length());

        //Invoke readApp twice to read both apps and to prove an exception is not thrown.
        assertDoesNotThrow(() -> {
            reader.readApp();
            reader.readApp();
        });
    }

    @Test
    public void appWithNoValidRulesThrowsException() {
        String appName = "App with no valid rules";
        RampartAppReader reader = new RampartAppReader(new BufferedReader(
                new StringReader(APP_WITH_UNKNOWN_RULE)), APP_WITH_UNKNOWN_RULE.length());

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, reader::readApp);

        assertThat(thrown.getMessage(), equalTo("app \"" + appName + "\" does not contain any valid rules"));
    }

    @Test
    public void appWithOnlyInvalidRulesThrowsException() {
        String appName = "App with no valid rules";
        RampartAppReader reader = new RampartAppReader(new BufferedReader(
                new StringReader(APP_WITH_MULTIPLE_UNKNOWN_RULES)), APP_WITH_MULTIPLE_UNKNOWN_RULES.length());

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, reader::readApp);

        assertThat(thrown.getMessage(), equalTo("app \"" + appName + "\" does not contain any valid rules"));
    }

    @Test
    public void appNameWithDoubleQuoteInName() throws InvalidRampartAppException, IOException {
        String appNameWithDoubleQuote =
                  "app(\"App with \\\" double quote\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithDoubleQuote)),
                appNameWithDoubleQuote.length()).readApp();

        assertThat("App name is incorrect, should contain a double quote",
                app.getAppName().contains(newRampartString("App with \" double quote")),
                equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void appNameWithDoubleQuoteInNameToString() throws InvalidRampartAppException, IOException {
        String appNameWithDoubleQuote =
                  "app(\"App with \\\" double quote\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithDoubleQuote)),
                appNameWithDoubleQuote.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"App with \\\" double quote\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: High)\n"
              + "    endfilesystem\n"
              + "endapp"));
    }

    @Test
    public void appNameWithEscapedBackslashAndDoubleQuoteInNameToString() throws InvalidRampartAppException, IOException {
        String appNameWithDoubleQuote =
                  "app(\"App with escaped \\\\ backslash and \\\" double quote\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithDoubleQuote)),
                appNameWithDoubleQuote.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"App with escaped \\\\ backslash and \\\" double quote\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: High)\n"
              + "    endfilesystem\n"
              + "endapp"));
    }

    @Test
    public void invalidAppNameWithMissingDoubleQuote() {
        String appNameWithMissingDoubleQuote =
                // the closing double quote is escaped, so the is no double quote to finish app name string
                  "app(\"App with invalid escaped double quote \\\"):\n"
                + "    requires(version: \"RAMPART/1.4\")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appNameWithMissingDoubleQuote)),
                    appNameWithMissingDoubleQuote.length()).readApp());
    }

    @Test
    public void invalidAppNameWithBackslashAndMissingDoubleQuote() {
        String appNameWithBackslashAndMissingDoubleQuote =
                  "app(\"App with invalid escaped double quote\\\\\\\"):\n"
                + "    requires(version: \"RAMPART/1.4\")\n"
                + "    filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endfilesystem\n"
                + "endapp";

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appNameWithBackslashAndMissingDoubleQuote)),
                    appNameWithBackslashAndMissingDoubleQuote.length()).readApp());
    }

    @Test
    public void appNameEndsWithSingleBackslash() throws InvalidRampartAppException, IOException {
        String appNameWithEndingBackslash =
                "app(\"App name with ending slash \\ \"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithEndingBackslash)),
              appNameWithEndingBackslash.length()).readApp();
      assertThat("App name should end with backslash character",
              app.getAppName().toString().endsWith("App name with ending slash \\ "), equalTo(true));
    }

    @Test
    public void appNameEndsWithEscpapedBackslash() throws InvalidRampartAppException, IOException {
        String appNameEndingWithEscapedBackslash =
                "app(\"App name ending with escaped backslash\\\\\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameEndingWithEscapedBackslash)),
              appNameEndingWithEscapedBackslash.length()).readApp();
      assertThat("App name should end with a backslash character",
              app.getAppName().toString().endsWith("App name ending with escaped backslash\\"), equalTo(true));
    }

    @Test
    public void appNameEndsWithBackslashAndDoubleQuote() throws InvalidRampartAppException, IOException {
        String appNameWithEndingEscapedBackslashAndQuote =
                "app(\"App with ending backslash and double-quote\\\"\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithEndingEscapedBackslashAndQuote)),
              appNameWithEndingEscapedBackslashAndQuote.length()).readApp();
      assertThat("App name should end with double quote character for toString method",
              app.getAppName().toString().endsWith("App with ending backslash and double-quote\""), equalTo(true));
    }

    @Test
    public void appNameEndsWithBackslashAndDoubleQuoteToString() throws InvalidRampartAppException, IOException {
        String appNameWithEndingEscapedBackslashAndQuote =
                "app(\"App with ending slash and quote\\\"\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithEndingEscapedBackslashAndQuote)),
              appNameWithEndingEscapedBackslashAndQuote.length()).readApp();

      assertThat(app, RampartAppMatcher.equalTo(
              "app(\"App with ending slash and quote\\\"\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: High)\n"
              + "    endfilesystem\n"
              + "endapp"));
    }

    @Test
    public void appNameNotEscapedBackslashesAreEscapedWhenFormatted() throws InvalidRampartAppException, IOException {
        String appNameWithSingleBackslashes =
                "app(\"App with \\ slashes\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithSingleBackslashes)),
              appNameWithSingleBackslashes.length()).readApp();

      assertThat(app, RampartAppMatcher.equalTo(
              "app(\"App with \\\\ slashes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp"));
    }

    @Test
    public void appNameNotEscapedAndEscapedBackslashes() throws InvalidRampartAppException, IOException {
        String appNameWithNotEscapedAndEscapedBackslashes =
                "app(\"App \\ with \\ mixed \\\\ slashes\"):\n"
              + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
              + "    filesystem(\"Sample filesystem rule\"):\n"
              + "        read(\"/etc/shadow\")\n"
              + "        protect(message: \"log Message\", severity: 8)\n"
              + "    endfilesystem\n"
              + "endapp";

      RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appNameWithNotEscapedAndEscapedBackslashes)),
              appNameWithNotEscapedAndEscapedBackslashes.length()).readApp();

      assertThat("App name with backslashes should be allowed",
              app.getAppName().toString().endsWith("App \\ with \\ mixed \\ slashes"), equalTo(true));
    }

    @Test
    public void invalidAppIsSkippedWhenLastAppIsValid() throws InvalidRampartAppException, IOException {
        String payload = VALID_APP + "\n" +  APP_WITH_SYNTACTICALLY_INVALID_RULE;
        BufferedReader reader = new BufferedReader(new StringReader(payload));
        RampartAppReader appReader = new RampartAppReader(reader, payload.length());
        appReader.readApp();
        try {
            appReader.readApp();
        } catch(InvalidRampartSyntaxError e) {
            appReader.skipUntilEndApp();
        }
        assertThat("No more bytes should be readable", reader.read(), equalTo(-1));
    }

    @Test
    public void ruleWithTargetOSListSetFieldAppropriately() throws InvalidRampartAppException, IOException {
        String appWithRuleSpecifyingTargetOSList =
                "app(\"App with no valid rules\"):\n"
                    + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
                    + "        read(\"/etc/shadow\")\n"
                    + "        protect(message: \"log Message\", severity: 8)\n"
                    + "     endfilesystem\n"
                    + "     endapp";
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithRuleSpecifyingTargetOSList)),
                appWithRuleSpecifyingTargetOSList.length()).readApp();
        RampartRule rule = app.getRuleIterator().next();
        assertThat(rule.getTargetOSList(), equalTo(newRampartList(AIX_KEY, LINUX_KEY, SOLARIS_KEY)));
    }

    @Test
    public void targetOSListConvertsToString() throws InvalidRampartAppException, IOException {
        String appWithRuleSpecifyingTargetOSList =
                "app(\"App with no valid rules\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "     endfilesystem\n"
                        + "     endapp";
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithRuleSpecifyingTargetOSList)),
                appWithRuleSpecifyingTargetOSList.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"App with no valid rules\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "     endfilesystem\n"
                        + "     endapp"));
    }

    @Test
    public void ruleWithInvalidTargetOSListType() throws InvalidRampartAppException {
        String appWithRuleTargetOSList =
                "app(\"App with no valid rules\"):\n"
                    + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                    + "     filesystem(\"Sample filesystem rule\", os: [\"aix\"]):\n"
                    + "        read(\"/etc/shadow\")\n"
                    + "        protect(message: \"log Message\", severity: 8)\n"
                    + "     endfilesystem\n"
                    + "     endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appWithRuleTargetOSList)),
                    appWithRuleTargetOSList.length()).readApp());
    }

    @Test
    public void ruleWithVersionLessThan1_5ReturnsAnyforTargetOsListWhenNotSet() throws InvalidRampartAppException, IOException {
        String version1_1App =
                "app(\"version 1.1 app without os declaration\"):\n"
                        + "    requires(version: \"RAMPART/1.1\")\n"
                        + "    patch(\"well formed patch\"):\n"
                        + "        function(\"com/foo/bar.main()V\")\n"
                        + "        entry()\n"
                        + "        code(language: \"java\"):\n"
                        + "            public void patch(JavaFrame frame){\n"
                        + "                // patch code\n"
                        + "            }\n"
                        + "         endcode\n"
                        + "    endpatch"
                        + "    endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(version1_1App)),
                version1_1App.length()).readApp();
        RampartRule rule = app.getRuleIterator().next();
        assertThat(rule.getTargetOSList(), equalTo(newRampartList(ANY_KEY)));
    }

    @Test
    public void ruleAnyTargetOsToStringConversion() throws InvalidRampartAppException, IOException {
        String appWithRuleNoTargetOs =
                "app(\"App with no valid rules\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     filesystem(\"Sample filesystem rule\", os: [any]):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "     endfilesystem\n"
                        + "     endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithRuleNoTargetOs)),
                appWithRuleNoTargetOs.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"App with no valid rules\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "     endfilesystem\n"
                        + "     endapp"));
    }

    @Test
    public void appWithMixedRulesAndVersionLessThan1_5IgnoresLibraryRule()
            throws InvalidRampartAppException, IOException {
        List<RampartRule> ruleList = new ArrayList<>();
        String version1_1AppWithLibraryRule =
                "app(\"version 1.1 app with a process rule\"):\n"

                        + "    requires(version: \"RAMPART/1.1\")\n"
                        + "    patch(\"well formed patch\"):\n"
                        + "        function(\"com/foo/bar.main()V\")\n"
                        + "        entry()\n"
                        + "        code(language: \"java\"):\n"
                        + "            public void patch(JavaFrame frame){\n"
                        + "                // patch code\n"
                        + "            }\n"
                        + "         endcode\n"
                        + "    endpatch"
                        + "    library(\"Sample library rule\"):\n"
                        + "        load(\"some.so\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "    endlibrary\n"
                        + "    endapp";
        RampartRuleIterator ruleIterator = new RampartAppReader(new BufferedReader(
                new StringReader(version1_1AppWithLibraryRule)),
                version1_1AppWithLibraryRule.length()).readApp().getRuleIterator();

        while (ruleIterator.hasNext() == RampartBoolean.TRUE) {
            ruleList.add(ruleIterator.next());
        }

        assertAll(() -> {
            assertThat(ruleList.size(), equalTo(1));
            assertThat(ruleList.get(0).getRuleName(), equalTo(newRampartString("well formed patch")));
        });
    }

    @Test
    public void appWithMixedRulesAndVersionLessThan1_5IgnoresProcessRule()
            throws InvalidRampartAppException, IOException {
        List<RampartRule> ruleList = new ArrayList<>();

        String version1_1AppWithProcessRule =
                "app(\"version 1.1 app with a process rule\"):\n"
                + "    requires(version: \"RAMPART/1.1\")\n"
                + "    patch(\"well formed patch\"):\n"
                + "        function(\"com/foo/bar.main()V\")\n"
                + "        entry()\n"
                + "        code(language: \"java\"):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "         endcode\n"
                + "    endpatch"
                + "    process(\"Sample process rule\"):\n"
                + "        execute(\"/bin/ls\")\n"
                + "        protect(message: \"log Message\", severity: 8)\n"
                + "    endprocess\n"
                + "    endapp";
        RampartRuleIterator ruleIterator = new RampartAppReader(new BufferedReader(
                new StringReader(version1_1AppWithProcessRule)),
                version1_1AppWithProcessRule.length()).readApp()
                .getRuleIterator();

        while (ruleIterator.hasNext() == RampartBoolean.TRUE) {
            ruleList.add(ruleIterator.next());
        }

        assertAll(() -> {
            assertThat(ruleList.size(), equalTo(1));
            assertThat(ruleList.get(0).getRuleName(), equalTo(newRampartString("well formed patch")));
        });
    }

    @Test
    public void appWithInvalidRampartVersionTypeFor2_0Version() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(INVALID_RAMPART_VERSION_TYPE_APP)),
                INVALID_RAMPART_VERSION_TYPE_APP.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"Sample well formed app\"):\n"
                + "     requires(version: RAMPART/2.0)\n"
                + "     filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "     endfilesystem\n"
                + "endapp"));
    }

    @Test
    public void appNoVersionDefaults1_0() throws InvalidRampartAppException, IOException {
        String appWithDefaultVersion =
                "app(\"App example\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "     endfilesystem\n"
                        + "     endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithDefaultVersion)),
                appWithDefaultVersion.length()).readApp();
        assertThat(app.getAppVersion(), equalTo(newRampartInteger(1)));
    }

    @Test
        public void appVersionDeclaration() throws InvalidRampartAppException, IOException {
        String appWithVersion =
                "app(\"App example\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     version(3)"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: 8)\n"
                        + "     endfilesystem\n"
                        + "     endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithVersion)),
                appWithVersion.length()).readApp();
        assertThat(app.getAppVersion(), equalTo(newRampartInteger(3)));
    }

    @Test
    public void appVersionDeclarationConversionToString() throws InvalidRampartAppException, IOException {
        String appWithVersion =
                "app(\"App example\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     version(3)"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "     endfilesystem\n"
                        + "     endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appWithVersion)),
                appWithVersion.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(appWithVersion));
    }
}
