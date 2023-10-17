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

import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.java.parser.StringRampartAppReader;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.RampartAppReader;
import matchers.RampartAppMatcher;

public class RampartSocketRuleIntegrationTest {

    @Test
    public void validRampartRuleWithTwoRules() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Upgrade TLS connections\"):\n"
                + "        accept(\"0.0.0.0:80\")\n"
                + "        protect(connection: upgrade-tls, message: \"TLS connection upgraded\", severity: 8)\n"
                + "    endsocket\n"

                + "    socket(\"Force TCP connections to use TLS\"):\n"
                + "        accept(\"0.0.0.0:80\")\n"
                + "        protect(connection: secure, message: \"forced TLS on every connection\", severity: 10)\n"
                + "    endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Upgrade TLS connections\"):\n"
                        + "        accept(\"0.0.0.0:80\")\n"
                        + "        protect(connection: upgrade-tls, message: \"TLS connection upgraded\", severity: High)\n"
                        + "    endsocket\n"

                        + "    socket(\"Force TCP connections to use TLS\"):\n"
                        + "        accept(\"0.0.0.0:80\")\n"
                        + "        protect(connection: secure, message: \"forced TLS on every connection\", severity: Very-High)\n"
                        + "    endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForClientBind() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Blocking client binds on all interfaces and all ports\"):\n"
                + "        bind(client: \"0.0.0.0:0\")\n"
                + "        protect(message: \"port binding blocked\", severity: 8)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Blocking client binds on all interfaces and all ports\"):\n"
                        + "        bind(client: \"0.0.0.0:0\")\n"
                        + "        protect(message: \"port binding blocked\", severity: High)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForServerBind() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Blocking server binds on all interfaces and all ports\"):\n"
                + "        bind(server: \"0.0.0.0:0\")\n"
                + "        protect(message: \"port binding blocked\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Blocking server binds on all interfaces and all ports\"):\n"
                        + "        bind(server: \"0.0.0.0:0\")\n"
                        + "        protect(message: \"port binding blocked\", severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForClientAndServerBind() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Blocking client and server binds on all interfaces and all ports\"):\n"
                + "        bind(client: \"0.0.0.0:0\", server: \"0.0.0.0:0\")\n"
                + "        protect(message: \"port binding blocked\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Blocking client and server binds on all interfaces and all ports\"):\n"
                        + "        bind(client: \"0.0.0.0:0\", server: \"0.0.0.0:0\")\n"
                        + "        protect(message: \"port binding blocked\", severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForAccept() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    socket(\"Blocking server accept from 192.168.10.2 interface, all ports\"):\n"
                + "        accept(\"192.168.10.2:0\")\n"
                + "        protect(message: \"accept blocked\", severity: 3)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    socket(\"Blocking server accept from 192.168.10.2 interface, all ports\"):\n"
                        + "        accept(\"192.168.10.2:0\")\n"
                        + "        protect(message: \"accept blocked\", severity: Low)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForAcceptProtectedUsingFqdn() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    socket(\"Blocking server accept from server.company.com, all ports\"):\n"
                + "        accept(\"server.company.com:0\")\n"
                + "        protect(message: \"accept blocked\", severity: 3)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    socket(\"Blocking server accept from server.company.com, all ports\"):\n"
                        + "        accept(\"server.company.com:0\")\n"
                        + "        protect(message: \"accept blocked\", severity: Low)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForConnect() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Blocking client connect on local interface 192.168.10.2 for all ports\"):\n"
                + "        connect(\"192.168.10.2:0\")\n"
                + "        protect(message: \"connect blocked\", severity: 3)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Blocking client connect on local interface 192.168.10.2 for all ports\"):\n"
                        + "        connect(\"192.168.10.2:0\")\n"
                        + "        protect(message: \"connect blocked\", severity: Low)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForConnectProtectedUsingFqdn() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    socket(\"Blocking client connect to rest.example.com for all ports\"):\n"
                + "        connect(\"rest.example.com:0\")\n"
                + "        protect(message: \"connect blocked\", severity: 3)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    socket(\"Blocking client connect to rest.example.com for all ports\"):\n"
                        + "        connect(\"rest.example.com:0\")\n"
                        + "        protect(message: \"connect blocked\", severity: Low)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleForClientBindPortRange() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Blocking client bind on 192.168.10.2 interface, ports 80 to 8080\"):\n"
                + "        bind(client: \"192.168.10.2:80-8080\")\n"
                + "        protect(message: \"port binding blocked\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Blocking client bind on 192.168.10.2 interface, ports 80 to 8080\"):\n"
                        + "        bind(client: \"192.168.10.2:80-8080\")\n"
                        + "        protect(message: \"port binding blocked\", severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleTlsUpgrade() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"upgrade TLS connections\"):\n"
                + "        accept(\"0.0.0.0:80\")\n"
                + "        protect(connection: upgrade-tls, message: \"latest TLS protocol version enforced\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"upgrade TLS connections\"):\n"
                        + "        accept(\"0.0.0.0:80\")\n"
                        + "        protect(connection: upgrade-tls, message: \"latest TLS protocol version enforced\", severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void validRampartRuleSecureSockets() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Force TCP connections to use TLS\"):\n"
                + "        accept(\"0.0.0.0:80\")\n"
                + "        protect(connection: secure, message: \"forced TLS on every connection\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"Force TCP connections to use TLS\"):\n"
                        + "        accept(\"0.0.0.0:80\")\n"
                        + "        protect(connection: secure, message: \"forced TLS on every connection\", severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void invalidRampartRuleMissingSocketOperation() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        protect()\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(),
                equalTo("one of the \"socket\" rule declarations: \"accept\", \"bind\" and \"connect\" must be provided"));

    }

    @Test
    public void invalidRampartRuleConnectOperationWithServerSocketType() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        connect(server: \"0.0.0.0:0\")\n"
                + "        protect()\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(), equalTo("parameter to \"connect\" declaration must be a string literal"));
    }

    @Test
    public void invalidRampartRuleTlsUpgradeWithSpecificAddress() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect(connection: upgrade-tls)\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(),
                equalTo("cannot configure action target \"connection\" with ip address \"10.0.0.1\""));
    }

    @Test
    public void invalidRampartRuleSecureSocketsWithSpecificAddress() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect(connection: secure)\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(),
                equalTo("cannot configure action target \"connection\" with ip address \"10.0.0.1\""));
    }

    @Test
    public void socketRuleNotSupportedVersion2_0() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.0)\n"

                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect()\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(), equalTo("app \"socket controls\" does not contain any valid rules"));
    }

    @Test
    public void ipv6AddressServerAcceptingConnections() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        accept(\":::0\")\n"
                + "        protect(connection: secure)\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(
                "app(\"socket controls\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                        + "    socket(\"blabla\"):\n"
                        + "        accept(\"0:0:0:0:0:0:0:0:0\")\n"
                        + "        protect(connection: secure, severity: Unknown)\n"
                        + "     endsocket\n"
                        + "endapp"));
    }

    @Test
    public void ipv6SpecificAddressWithActionTargetNotSupported() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"blabla\"):\n"
                + "        accept(\"2001:db8::1:0:0:1:8080\")\n"
                + "        protect(connection: secure)\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(),
                equalTo("cannot configure action target \"connection\" with ip address \"2001:db8:0:0:1:0:0:1\""));
    }

    @Test
    public void validRuleActionWithStacktrace() throws IOException {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    socket(\"Blocking server accept on 192.168.10.2 interface, all ports\"):\n"
                + "        accept(\"192.168.10.2:0\")\n"
                + "        protect(message: \"accept blocked\", severity: Low, stacktrace: \"full\")\n"
                + "     endsocket\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void socketSecureRuleShouldRejectHostname() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"

                + "    socket(\"Force TCP connections to use TLS\"):\n"
                + "        accept(\"example.com:80\")\n"
                + "        protect(connection: secure, message: \"forced TLS on every connection\")\n"
                + "     endsocket\n"
                + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
        assertThat(thrown.getMessage(),
                equalTo("cannot configure action target \"connection\" using hostname, value provided \"example.com\""));
    }

    @Test
    public void metadataInRule() throws Exception {
        String appText =
                "app(\"app with metadata\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    socket(\"blabla\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        accept(\"10.0.0.1:0\")\n"
                        + "        protect()\n"
                        + "     endsocket\n"
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
                        + "    socket(\"blabla\"):\n"
                        + "        metadata(\n"
                        + "            foo: \"bar\")\n"
                        + "        accept(\"10.0.0.1:0\")\n"
                        + "        protect()\n"
                        + "     endsocket\n"
                        + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidConnectionAttributeValue2_2() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.2)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect(connection: do-something)\n"
                + "     endsocket\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidConnectionAttribute2_2() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.2)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect(disconnection: upgrade-tls)\n"
                + "     endsocket\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidProtectStacktraceField2_2() {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.2)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"10.0.0.1:0\")\n"
                + "        protect(connection: upgrade-tls, stacktrace:\"every second\")\n"
                + "     endsocket\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void validProtectStacktraceField2_5() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.5)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        protect(connection: upgrade-tls, severity: Unknown, stacktrace:\"every second\")\n"
                + "     endsocket\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void invalidTaintSourceAttribute2_8() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        input(http)\n"
                + "        protect(connection: upgrade-tls)\n"
                + "     endsocket\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void validTaintSourceAttribute2_9() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        input(http)\n"
                + "        protect(severity: High)\n"
                + "     endsocket\n"
                + "endapp";
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)),
                appText.length()).readApp();
        assertThat(app, RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void apiFilterAnyClauseIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        api(any)\n"
                + "        protect(severity: High)\n"
                + "    endsocket\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void apiFilterUrlPatternIsSupported() throws IOException {
        String appText =
                "app(\"app\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        api(\"/api/v1\", \"/api/v2/*\")\n"
                + "        protect(severity: High)\n"
                + "    endsocket\n"
                + "endapp";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void invalidApiFilterAttribute2_8() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.8)\n"
                + "    socket(\"blabla\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        api(any)\n"
                + "        protect(connection: upgrade-tls)\n"
                + "     endsocket\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void inputWithAllowActionIsNotValid() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"accept\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        input(http)\n"
                + "        allow()\n"
                + "     endsocket\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void apiWithAllowActionIsNotValid() throws Exception {
        String appText = "app(\"socket controls\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"accept\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        api(\"/api\")\n"
                + "        allow()\n"
                + "     endsocket\n"
                + "endapp";
        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void invalidApiClauseInTcpToSslRule() {
        String appText = "app(\"Mod for socket_tcp_to_ssl\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"socket_tcp_to_ssl\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        api(any)\n"
                + "        protect(connection: secure, message: \"tcp to ssl\")\n"
                + "    endsocket\n"
                + "endapp";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo(
                "Invalid rule configuration, api clause can not be used together with action target \"connection\"."));
    }

    @Test
    public void invalidTaintInputClauseInTlsUpgradeRule() {
        String appText = "app(\"Mod for upgrade-tls\"):\n"
                + "    requires(version: RAMPART/2.9)\n"
                + "    socket(\"Upgrade TLS connections for connections\"):\n"
                + "        accept(\"0.0.0.0:0\")\n"
                + "        input(http)\n"
                + "        protect(connection: upgrade-tls, message: \"TLS connection upgraded\")\n"
                + "    endsocket\n"
                + "endapp";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo(
                "Invalid rule configuration, taint inputs can not be used together with action target \"connection\"."));
    }

}
