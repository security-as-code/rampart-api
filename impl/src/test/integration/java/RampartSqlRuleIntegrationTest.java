import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import java.io.IOException;
import java.util.Collection;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.sql.RampartSql;
import org.rampart.lang.java.parser.StringRampartAppReader;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.java.InvalidRampartAppException;
import matchers.RampartAppMatcher;

public class RampartSqlRuleIntegrationTest {

    @Test
    public void validSqlRuleMultipleInputsInjectionAttempts() throws IOException {
        String appText =
            "app(\"Sample SQL rule app 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http, database, deserialization)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: Medium)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void validSqlRuleSingleInputInjectionExploits() throws IOException {
        String appText =
            "app(\"Sample SQL rule app 2\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     sql(\"controls for SQL operations\"):\n"
            + "         vendor(mysql, options: [ansi-quotes, no-backslash-escapes])\n"
            + "         input(http)\n"
            + "         injection(successful-attempt)\n"
            + "         protect(message: \"denying sql injections\", severity: Very-High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void validSqlRuleAllVendorsValidateDefaultsToInjectionExploits() throws IOException {
        String sqlRuleAppAllVendorsDefaultInjection =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(database, deserialization)\n"
            + "        injection()\n"
            + "        detect(message: \"denying sql injections\", severity: 10)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppAllVendorsDefaultInjection).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(database, deserialization)\n"
                        + "        injection(successful-attempt)\n"
                        + "        detect(message: \"denying sql injections\", severity: Very-High)\n"
                        + "     endsql\n"
                    + "endapp\n")));
    }

    @Test
    public void validSqlRuleAllValuesWithDefaults() throws InvalidRampartAppException, IOException {
        String sqlRuleAppAllDefaults =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection()\n"
            + "        protect()\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppAllDefaults).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(http)\n"
            + "        injection(successful-attempt)\n"
            + "        protect(severity: Unknown)\n"
            + "     endsql\n"
            + "endapp\n")));
    }

    @Test
    public void validSqlRuleAllDefaultValuesPermitQueryProvidedConfig() throws InvalidRampartAppException, IOException {
        String sqlRuleAppAllConfigPermit =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection(permit: query-provided)\n"
            + "        protect()\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppAllConfigPermit).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(http)\n"
            + "        injection(successful-attempt, permit: query-provided)\n"
            + "        protect(severity: Unknown)\n"
            + "     endsql\n"
            + "endapp\n")));
    }

    @Test
    public void validSqlRuleWithQueryProvidedShouldPermitQueryProvided() throws InvalidRampartAppException, IOException {
        String sqlRuleAppAllConfigPermit =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection(permit: query-provided)\n"
            + "        protect()\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppAllConfigPermit).readApps();

        RampartSql rule = (RampartSql) apps.iterator().next().getRuleIterator().next();
        assertThat(rule.getSqlInjectionType().shouldPermitQueryProvided(), equalTo(RampartBoolean.TRUE));
    }

    @Test
    public void invalidSqlRuleWithInvalidDatabaseConfiguration() throws InvalidRampartAppException {
        String sqlRuleAppInvalidDBConfig =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(oracle, options: [ansi-quotes, no-backslash-escapes])\n"
            + "        injection()\n"
            + "        protect(message: \"hello world!\", severity: 8)\n"
            + "     endsql\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(sqlRuleAppInvalidDBConfig).readApps());
    }

    @Test
    public void invalidSqlRuleWithUnknowInjectionConfiguration() throws InvalidRampartAppException {
        String sqlRuleAppUnknownActionConfig =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection(query: confusing)\n"
            + "        protect(message: \"hello world!\", severity: 8)\n"
            + "     endsql\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(sqlRuleAppUnknownActionConfig).readApps());
    }

    @Test
    public void validSqlRuleFailedAttemptsInjectionWithPermitConfiguration() throws InvalidRampartAppException, IOException {
        String sqlRuleAppFailedAttemptPermitConfig =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection(failed-attempt, permit: query-provided)\n"
            + "        protect()\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppFailedAttemptPermitConfig).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(http)\n"
            + "        injection(failed-attempt, permit: query-provided)\n"
            + "        protect(severity: Unknown)\n"
            + "     endsql\n"
            + "endapp\n")));
    }

    @Test
    public void validSqlRuleDefaultValuesWithAllowAction() throws InvalidRampartAppException, IOException {
        String sqlRuleAppAllDefaultsAllowAction =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        injection()\n"
            + "        allow()\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(sqlRuleAppAllDefaultsAllowAction).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(http)\n"
            + "        injection(successful-attempt)\n"
            + "        allow(severity: Unknown)\n"
            + "     endsql\n"
            + "endapp\n")));
    }

    @Test
    public void validMultipleSqlAppsDefined() throws InvalidRampartAppException, IOException {
        String sqlRuleAppMultipleInputsFailedAttempt =
            "app(\"Sample SQL rule app 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http, database, deserialization)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: Medium)\n"
            + "     endsql\n"
            + "endapp\n";
        String sqlRuleAppSingleInputSuccessfulAttempt =
            "app(\"Sample SQL rule app 2\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     sql(\"controls for SQL operations\"):\n"
            + "         vendor(mysql, options: [ansi-quotes, no-backslash-escapes])\n"
            + "         input(http)\n"
            + "         injection(successful-attempt)\n"
            + "         protect(message: \"denying sql injections\", severity: Very-High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(
                sqlRuleAppMultipleInputsFailedAttempt + "\n" + sqlRuleAppSingleInputSuccessfulAttempt).readApps();

        assertThat(apps, contains(
                RampartAppMatcher.equalTo(sqlRuleAppMultipleInputsFailedAttempt),
                RampartAppMatcher.equalTo(sqlRuleAppSingleInputSuccessfulAttempt)));
    }

    @Test
    public void invalidSqlRuleWithDuplicateVendorDeclaration() throws InvalidRampartAppException {
        String sqlRuleAppMultipleVendors =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(mysql, options: [ansi-quotes, no-backslash-escapes])\n"
            + "        vendor(oracle)\n"
            + "        input(database, deserialization, http)\n"
            + "        injection()\n"
            + "        protect()\n"
            + "     endsql\n"
            + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(sqlRuleAppMultipleVendors).readApps());
    }

    @Test
    public void singleRuleToStringImplementation() throws IOException {
        String appText =
            "app(\"Sample SQL rule app 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http, database, deserialization)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: Medium)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void multipleRuleToStringImplementation() throws IOException {
        String appText =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations 1\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: Medium)\n"
            + "     endsql\n"
            + "    sql(\"controls for SQL operation 2\"):\n"
            + "        vendor(mysql)\n"
            + "        input(http)\n"
            + "        injection(successful-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleToStringImplementationWithOptionsAndConfig() throws IOException {
        String appText =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(mysql, options: [ansi-quotes, no-backslash-escapes])\n"
            + "        input(deserialization, http)\n"
            + "        injection(successful-attempt, permit: query-provided)\n"
            + "        protect(message: \"hello world!\", severity: High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleToStringImplementationSuccessfulAndFailedAttempts() throws IOException {
        String appText =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(deserialization)\n"
            + "        injection(successful-attempt, failed-attempt)\n"
            + "        detect(message: \"denying sql injections\", severity: Very-High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleSendErrorProtectiveActionSuccessfulAttempts() throws IOException {
        String appText =
            "app(\"Sample SQL rule app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(any)\n"
            + "        input(http)\n"
            + "        injection(successful-attempt)\n"
            + "        protect(http-response: {new-response: {code: 400}},\n"
            + "             message: \"denying sql injections\", severity: Very-High)\n"
            + "     endsql\n"
            + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleSendErrorProtectiveActionFailedAttempts() throws IOException {
        String appText =
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(http)\n"
                        + "        injection(failed-attempt)\n"
                        + "        protect(http-response: {new-response: {code: 400}},\n"
                        + "             message: \"denying sql injections\", severity: Very-High)\n"
                        + "     endsql\n"
                        + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleSendErrorProtectiveActionSuccessfulAndFailedAttempts() throws IOException {
        String appText =
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(http)\n"
                        + "        injection(successful-attempt, failed-attempt)\n"
                        + "        protect(http-response: {new-response: {code: 400}},\n"
                        + "             message: \"denying sql injections\", severity: Very-High)\n"
                        + "     endsql\n"
                        + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();

        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void singleRuleSendErrorAllowActionSuccessfulAndFailedAttempts() {
        String appText =
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(http)\n"
                        + "        injection(successful-attempt, failed-attempt)\n"
                        + "        allow(http-response: {new-response: {code: 400}},\n"
                        + "             message: \"denying sql injections\", severity: Very-High)\n"
                        + "     endsql\n"
                        + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void singleRuleSendErrorProtectiveActionSuccessfulAndFailedAttempts2_3Version() {
        String appText =
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/2.3)\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(http)\n"
                        + "        injection(successful-attempt, failed-attempt)\n"
                        + "        protect(http-response: {new-response: {code: 400}},\n"
                        + "             message: \"denying sql injections\", severity: Very-High)\n"
                        + "     endsql\n"
                        + "endapp\n";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void validSqlRuleActionWithStacktrace() throws IOException {
        String appText =
                "app(\"Sample SQL rule app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        vendor(any)\n"
                        + "        input(database, deserialization)\n"
                        + "        injection(successful-attempt)\n"
                        + "        detect(message: \"denying sql injections\", severity: Very-High, stacktrace: \"full\")\n"
                        + "     endsql\n"
                        + "endapp\n";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        vendor(any)\n"
                        + "        input(database, deserialization)\n"
                        + "        injection()\n"
                        + "        detect(message: \"denying sql injections\", severity: 10)\n"
                        + "     endsql\n"
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
                        + "    sql(\"controls for SQL operations\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        vendor(any)\n"
                        + "        input(database, deserialization)\n"
                        + "        injection()\n"
                        + "        detect(message: \"denying sql injections\", severity: 10)\n"
                        + "     endsql\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }
}
