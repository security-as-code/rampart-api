import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.dns.RampartDns;
import org.rampart.lang.api.core.RampartInput;
import org.rampart.lang.java.parser.StringRampartAppReader;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import matchers.RampartAppMatcher;

public class RampartDnsRuleIntegrationTest {

    @Test
    public void validRuleWithTwoRules() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Allowing address resolution for rampart.org\"):\n"
                + "        lookup(\"rampart.org\")\n"
                + "        allow(message: \"dns lookup occurred for rampart.org\", severity: 8)\n"
                + "    enddns\n"

                + "    dns(\"Blocking address resolution completely\"):\n"
                + "        lookup(any)\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Allowing address resolution for rampart.org\"):\n"
                        + "        lookup(\"rampart.org\")\n"
                        + "        allow(message: \"dns lookup occurred for rampart.org\", severity: High)\n"
                        + "    enddns\n"

                        + "    dns(\"Blocking address resolution completely\"):\n"
                        + "        lookup(any)\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }

    @Test
    public void validRuleForAnyLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution completely\"):\n"
                + "        lookup(any)\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Blocking address resolution completely\"):\n"
                        + "        lookup(any)\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }

    @Test
    public void validRuleForHostnameLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"qa-01.rampart.lan\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Blocking address resolution\"):\n"
                        + "        lookup(\"qa-01.rampart.lan\")\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }

    @Test
    public void validRuleForIpv4AddressLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"10.0.1.10\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Blocking address resolution\"):\n"
                        + "        lookup(\"10.0.1.10\")\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }

    @Test
    public void validRuleForIpv4WildcardAddressLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"0.0.0.0\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo("app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(any)\n"
                + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                + "    enddns\n"
                + "endapp"));
    }

    @Test
    public void validRuleForIpv6AddressLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"fe80::5fd3:c1b0:5624:fb39\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Blocking address resolution\"):\n"
                        + "        lookup(\"fe80:0:0:0:5fd3:c1b0:5624:fb39\")\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }

    @Test
    public void validRuleForIpv6WildcardAddressLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"::\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(any)\n"
                + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                + "    enddns\n"
                + "endapp"));
    }

    @Test
    public void validRuleForIpv6FullWildcardAddressLookup() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"0:0:0:0:0:0:0:0\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"dns controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    dns(\"Blocking address resolution\"):\n"
                        + "        lookup(any)\n"
                        + "        protect(message: \"dns lookup blocked\", severity: High)\n"
                        + "    enddns\n"
                        + "endapp"));
    }
        @Test
    public void noLookupDeclaration() {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());

        assertThat(thrown.getMessage(), equalTo("missing \"lookup\" mandatory declaration"));
    }

    @Test
    public void ruleInvalidHostname() {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"asd/2&as\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());

        assertThat(thrown.getMessage(), equalTo("invalid hostname \"asd/2&as\" in \"lookup\" declaration"));
    }

    @Test
    public void ruleInvalidIpAddress() {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(\"400:100.0.1\")\n"
                + "        protect(message: \"dns lookup blocked\", severity: 8)\n"
                + "    enddns\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());

        assertThat(thrown.getMessage(), equalTo("invalid hostname \"400:100.0.1\" in \"lookup\" declaration"));
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText = "app(\"dns controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(any)\n"
                + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
                + "    enddns\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        metadata(\n"
            + "            foo: \"bar\")\n"
            + "        lookup(any)\n"
            + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
            + "    enddns\n"
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
            + "    dns(\"Blocking address resolution\"):\n"
            + "        metadata(\n"
            + "            foo: \"bar\")\n"
            + "        lookup(any)\n"
            + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
            + "    enddns\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void taintingInputsInRule2_8() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/2.8)\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        lookup(any)\n"
            + "        input(http)\n"
            + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
            + "    enddns\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void taintingInputsInRule2_9() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/2.9)\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        lookup(any)\n"
            + "        input(http)\n"
            + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
            + "    enddns\n"
            + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartList inputs = ((RampartDns) apps.iterator().next().getRuleIterator().next()).getDataInputs();
        assertThat(inputs.getFirst(), equalTo(RampartInput.HTTP));
        assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void apiFilterAnyClauseIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(any)\n"
                + "        api(any)\n"
                + "        protect(severity: High)\n"
                + "    enddns\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void apiFilterUrlPatternIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    dns(\"Blocking address resolution\"):\n"
                + "        lookup(any)\n"
                + "        api(\"/api/v1\", \"/api/v2/*\")\n"
                + "        protect(severity: High)\n"
                + "    enddns\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void apiFilterInRule2_8() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/2.8)\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        lookup(any)\n"
            + "        api(any)\n"
            + "        protect(message: \"dns lookup blocked\", severity: High, stacktrace: \"full\")\n"
            + "    enddns\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputWithAllowActionIsNotValid() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/2.9)\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        lookup(any)\n"
            + "        input(http)\n"
            + "        allow(message: \"dns lookup allowed\", severity: High)\n"
            + "    enddns\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void apiWithAllowActionIsNotValid() throws Exception {
        String appText =
            "app(\"app with metadata\"):\n"
            + "    requires(version: RAMPART/2.9)\n"
            + "    dns(\"Blocking address resolution\"):\n"
            + "        lookup(any)\n"
            + "        api(\"/api\")\n"
            + "        allow(message: \"dns lookup allowed\", severity: High)\n"
            + "    enddns\n"
            + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }
}
