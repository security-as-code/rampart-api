import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRuleIterator;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.InvalidRampartSyntaxError;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;

public class RampartCommonRuleIntegrationTest {

    @Test
    public void missingEndAppThrowsException() {
        String appText =
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

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void appWithMixedRulesIsParsedSuccessfully() {
        String appText =
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

        assertDoesNotThrow(
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void appWithValidAndInvalidRuleIsParsedSuccessfully() {
        String appText =
            "app(\"App with undefined rule\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "    alice():\n"
            + "    endalice\n"
            + "endapp";

        assertDoesNotThrow(
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void duplicateRuleNamesAcrossDifferentAppsIsParsedSuccessfully() {
        String appText =
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

        StringRampartAppReader reader = new StringRampartAppReader(appText);

        //Invoke readApp twice to read both apps and to prove an exception is not thrown.
        assertDoesNotThrow(() -> {
            reader.readApps();
            reader.readApps();
        });
    }

    @Test
    public void appWithNoValidRulesThrowsException() {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    bob():\n"
            + "    endbob\n"
            + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), endsWith("app \"App with no valid rules\" does not contain any valid rules"));
    }

    @Test
    public void appWithOnlyInvalidRulesThrowsException() {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    bob():\n"
            + "    endbob\n"
            + "    alice():\n"
            + "    endalice\n"
            + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), endsWith("app \"App with no valid rules\" does not contain any valid rules"));
    }

    @Test
    public void appNameWithDoubleQuotesInName() throws Exception {
        String appText =
            "app(\"App with \\\"double quotes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat("App name does not contain any backslash for toString method",
                    apps.iterator().next().getAppName().contains(newRampartString("App with \"double quotes")),
                    equalTo(RampartBoolean.TRUE));
        });
    }

    @Test
    public void appNameWithDoubleQuotesInNameToString() throws Exception {
        String appText =
            "app(\"App with \\\"double quotes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App with \\\"double quotes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appNameWithEscapedBackslashAndDoublequoteInNameToString() throws Exception {
        String appText =
            "app(\"App with \\\\\\\"double quote\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App with \\\\\\\"double quote\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void invalidAppNameWithEscapedDoubleQuotes() {
        String appText =
            "app(\"App with invalid escaped double quotes\\\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidAppNameWithEscapedBackslashAndEscapedDoubleQuotes() {
        String appText =
            "app(\"App with invalid escaped double quotes\\\\\\\"):\n"
            + "    requires(version: \"RAMPART/1.4\")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void appNameEndsWithSingleBackslash() throws Exception {
        String appText =
            "app(\"App with ending slashes\\ \"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat("App name ends with backslash character",
                    apps.iterator().next().getAppName().toString().endsWith("App with ending slashes\\ "),
                    equalTo(true));
        });
    }

    @Test
    public void appNameEndsWithTwoBackslashes() throws Exception {
        String appText =
            "app(\"App ending with two backslashes\\\\\\\\\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat("App name ends with two backslash characters",
                    apps.iterator().next().getAppName().toString().endsWith("App ending with two backslashes\\\\"),
                    equalTo(true));
        });
    }

    @Test
    public void appNameEndsWithBackslashAndDoubleQuotes() throws Exception {
        String appText =
            "app(\"App with ending slash and quote\\\"\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat("App name ends with double quotes character for toString method",
                    apps.iterator().next().getAppName().toString().endsWith("App with ending slash and quote\""),
                    equalTo(true));
        });
    }

    @Test
    public void appNameEndsWithBackslashAndDoubleQuotesToString() throws Exception {
        String appText =
            "app(\"App with ending slash and quote\\\"\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App with ending slash and quote\\\"\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appNameNotEscapedBackslashesAreEscapedWhenFormatted() throws Exception {
        String appText =
            "app(\"App \\ with \\ slashes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App \\\\ with \\\\ slashes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appNameWithNotEscapedBackslashesAndEscapedBackslash() throws Exception {
        String appText =
            "app(\"App with not escaped \\ and escaped \\\\ slashes\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat("Only escaped backslashes are not removed",
                    apps.iterator().next().getAppName().toString().endsWith("App with not escaped \\ and escaped \\ slashes"),
                    equalTo(true));
        });
    }

    @Test
    public void ruleWithTargetOSListSetFieldAppropriately() throws Exception {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            RampartApp app = apps.iterator().next();
            RampartRuleIterator it = app.getRuleIterator();
            assertThat(it.next().getTargetOSList(), equalTo(newRampartList(AIX_KEY, LINUX_KEY, SOLARIS_KEY)));
            assertThat("app has more than one rule", it.hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void targetOSListConvertsToString() throws Exception {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\", os: [aix, linux, solaris]):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void ruleWithInvalidTargetOSListType() {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\", os: [\"aix\"]):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void ruleWithVersionLessThan1_5ReturnsAnyForTargetOsListWhenNotSet() throws Exception {
        String appText =
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
            + "    endpatch\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            RampartApp app = apps.iterator().next();
            RampartRuleIterator it = app.getRuleIterator();
            assertThat(it.next().getTargetOSList(), equalTo(newRampartList(ANY_KEY)));
            assertThat("app has more than one rule", it.hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void ruleAnyTargetOsToStringConversion() throws Exception {
        String appText =
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\", os: [any]):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App with no valid rules\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appWithMixedRulesAndVersionLessThan1_5IgnoresLibraryRule()
            throws InvalidRampartAppException, IOException {
        String appText =
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

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            RampartApp app = apps.iterator().next();
            RampartRuleIterator it = app.getRuleIterator();
            assertThat(it.next().getRuleName(), equalTo(newRampartString("well formed patch")));
            assertThat("app has more than one rule", it.hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void appWithMixedRulesAndVersionLessThan1_5IgnoresProcessRule()
            throws InvalidRampartAppException, IOException {
        String appText =
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
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            RampartApp app = apps.iterator().next();
            RampartRuleIterator it = app.getRuleIterator();
            assertThat(it.next().getRuleName(), equalTo(newRampartString("well formed patch")));
            assertThat("app has more than one rule", it.hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void appWithInvalidRampartVersionTypeFor2_0Version() throws InvalidRampartAppException {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "     requires(version: \"RAMPART/2.0\")\n"
            +"      version(2.0)\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void appNoVersionDefaults1_0() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next().getAppVersion(), equalTo(newRampartInteger(1)));
        });
    }

    @Test
    public void appVersionDeclaration() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next().getAppVersion(), equalTo(newRampartInteger(3)));
        });
    }

    @Test
    public void appVersionDeclarationConversionToString() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appWithSanitizationRuleNoMessagePresentUpTo2_7() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Test app\"):\n"
                + "    requires(version: RAMPART/2.7)\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe)\n"
                + "        protect(severity: High)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void appWithSanitizationRuleNoMessagePresent() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe, logging: on)\n"
            + "        protect(severity: High)\n"
            + "    endsanitization\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void appWithSanitizationRuleWithOptionalIgnoreNoMessagePresentUpTo2_7() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/2.7)\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        ignore(payload: [\"*/*;q=0.8\"])"
            + "        protect(severity: 7)\n"
            + "    endsanitization\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
           "app(\"Test app\"):\n"
           + "    requires(version: RAMPART/2.7)\n"
           + "    sanitization(\"Test Sanitization\"):\n"
           + "        request()\n"
           + "        undetermined(values: safe)\n"
           + "        ignore(payload: [\"*/*;q=0.8\"])"
           + "        protect(severity: High)\n"
           + "    endsanitization\n"
           + "endapp")));
    }

    @Test
    public void appWithSanitizationRuleWithOptionalLoggingOn() throws IOException {
        String appText =
                "app(\"Test app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        ignore(payload: [\"*/*;q=0.8\"])"
                + "        protect(severity: 7)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
                "app(\"Test app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        ignore(payload: [\"*/*;q=0.8\"])"
                + "        protect(severity: High)\n"
                + "    endsanitization\n"
                + "endapp")));
    }

    @Test
    public void appWithSanitizationRuleUpTo2_7() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/2.7)\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        protect(message: \"log message\", severity: 7)\n"
            + "    endsanitization\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/2.7)\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        protect(message: \"log message\", severity: High)\n"
            + "    endsanitization\n"
            + "endapp")));
    }

    @Test
    public void appWithSanitizationRule() throws InvalidRampartAppException, IOException {
        String appText =
                "app(\"Test app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        protect(message: \"log message\", severity: 7)\n"
                + "    endsanitization\n"
                + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
                "app(\"Test app\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    sanitization(\"Test Sanitization\"):\n"
                + "        request()\n"
                + "        undetermined(values: safe, logging: on)\n"
                + "        protect(message: \"log message\", severity: High)\n"
                + "    endsanitization\n"
                + "endapp")));
    }

    @Test
    public void appWithSanitizationRuleWithOptionalIgnoreUpTo2_8() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/2.7)\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        ignore(payload: [\"*/*;q=0.8\"])"
            + "        protect(message: \"log message\", severity: 7)\n"
            + "    endsanitization\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
           "app(\"Test app\"):\n"
           + "    requires(version: RAMPART/2.7)\n"
           + "    sanitization(\"Test Sanitization\"):\n"
           + "        request()\n"
           + "        undetermined(values: safe)\n"
           + "        ignore(payload: [\"*/*;q=0.8\"])"
           + "        protect(message: \"log message\", severity: High)\n"
           + "    endsanitization\n"
           + "endapp")));
    }

    @Test
    public void appWithSanitizationRuleWithOptionalIgnore() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        ignore(payload: [\"*/*;q=0.8\"])"
            + "        protect(message: \"log message\", severity: 7)\n"
            + "    endsanitization\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"Test app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sanitization(\"Test Sanitization\"):\n"
            + "        request()\n"
            + "        undetermined(values: safe)\n"
            + "        ignore(payload: [\"*/*;q=0.8\"])"
            + "        protect(message: \"log message\", severity: High)\n"
            + "    endsanitization\n"
            + "endapp")));
    }

    @Test
    public void twoAppsWithCommentsInBetween() throws InvalidRampartAppException, IOException {
        String app1 =
            "app(\"App1 example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp\n";

        String app2 =
            "app(\"App2 example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    patch(\"Sample patch rule\"):\n"
            + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
            + "        entry()\n"
            + "        code(language: java):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(
            app1
            + "##### This is a comment #####\n"
            + app2
        ).readApps();

        assertAll(() -> {
            assertThat(rampartApps.size(), equalTo(2));
            assertThat(rampartApps, contains(
                    RampartAppMatcher.equalTo(app1),
                    RampartAppMatcher.equalTo(app2)));
        });
    }

    @Test
    public void appVersionDeclarationOrderDoesNotMatter() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     version(3)"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appDeclarationsBeforeRules() throws InvalidRampartAppException {
        String appText =
            "app(\"App example\"):\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "     version(3)"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "endapp";

        assertThrows(InvalidRampartSyntaxError.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void metadataWithSpecialCharacters() throws IOException {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    marshal(\"Deserialization controls\"):\n"
            + "        metadata(\n"
            + "            withbackslashes: \"not escaped backslash: \\ & escaped backslash: \\\\ & doublequote: \\\" & 2 doublequotes: \\\"\\\" & equals: = .\")\n"
            + "        deserialize(java)\n"
            + "        rce()\n"
            + "        protect(message: \"attack found\", severity: High, stacktrace: \"full\")\n"
            + "    endmarshal\n"
            + "endapp";

        RampartMetadata ruleMetadata = new StringRampartAppReader(appText)
                .readApps().iterator().next()
                .getRuleIterator().next()
                .getMetadata();
        RampartObject value = ruleMetadata.get(newRampartConstant("withbackslashes"));
        assertThat(value, equalTo(newRampartString("not escaped backslash: \\ & escaped backslash: \\ & doublequote: \" & 2 doublequotes: \"\" & equals: = .")));
    }

    @Test
    public void syntaxErrorToIncludeRuleName() {
        String appText =
            "app(\"broken marshal app\"):"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")"
            + "    marshal(\"broken\"):"
            + "        deserialize(java)\n"
            + "        rce()\n"
            + "        shouldnotbeallowed(message:\"\")\n"
            + "    endmarshal\n"
            + "endapp";

        Exception x = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
        assertThat(x.getMessage(), equalTo("\"shouldnotbeallowed\" is not a recognized declaration in rule \"broken\""));
    }
}
