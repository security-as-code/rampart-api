package org.rampart.lang.impl.dns.parsers.v2;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.java.RampartPrimitives;

public class DnsParserValidatorTest {

    @Test
    public void missingLookupDeclaration() {
        final String message = getFailureMessage(null);
        assertThat(message, equalTo("missing \"lookup\" mandatory declaration"));
    }

    @Test
    public void emptyLookupDeclaration() {
        final String message = getFailureMessage(RampartList.EMPTY);
        assertThat(message, equalTo("hostname is not specified in \"lookup\" declaration"));
    }

    @Test
    public void lookupValidIpv4Address() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("127.0.0.1"));
    }

    @Test
    public void lookupValidIpv6Address() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("fe80::5fd3:c1b0:5624:fb39"));
    }

    @Test
    public void lookupWithInvalidParameterType() {
        final String message =
            getFailureMessage(newRampartList(
                newRampartNamedValue(newRampartConstant("address"), newRampartString("fe80::5fd3:c1b0:5624:fb39"))
            ));
        assertThat(message, equalTo(
                "unrecognized parameter \"address: \"fe80::5fd3:c1b0:5624:fb39\"\" to the \"lookup\" declaration"));
    }

    @Test
    public void lookupWildcardIpv4Address() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("0.0.0.0"));
    }

    @Test
    public void lookupShortWildcardIpv6Address() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("::"));
    }

    @Test
    public void lookupLongerWildcardIpv6Address() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("0:0:0:0:0:0:0:0"));
    }

    @Test
    public void lookupIpv6AddressUpperCase() throws InvalidRampartRuleException {
        checkSuccess(newRampartString("FE80::202:B3FF:FE1E:8329"));
    }

    @Test
    public void lookupDuplicateHostname() {
        final String message =
                getFailureMessage(newRampartList(
                    newRampartString("rampart.org"),
                    newRampartString("qa-01.rampart.lan")
                ));

        assertThat(message,
                equalTo("duplicate parameter \"qa-01.rampart.lan\" detected for \"lookup\" declaration"));
    }

    @Test
    public void lookupInvalidIpv4Address() {
        final String message = getFailureMessage(newRampartList(newRampartString("127.0.0.3&0")));
        assertThat(message, equalTo("invalid hostname \"127.0.0.3&0\" in \"lookup\" declaration"));
    }

    @Test
    public void lookupInvalidIpv6Address() {
        final String message = getFailureMessage(newRampartList(newRampartString(":.1")));
        assertThat(message, equalTo("invalid hostname \":.1\" in \"lookup\" declaration"));
    }

    @Test
    public void lookupValidHostnames() throws InvalidRampartRuleException {
        for (String hostname : new String[] {
                "1",
                "a",
                "a.",
                "ie",
                "com",
                "com.",
                "local",
                "dev123",
                "qadb-01",
                "rampart.org",
                "office.rampart.org",
                "ftp-3.wikipedia.org",
                "abcdefghijklmnopqrstuv-1234567890",
                "abcdefghijklmnopqrstuv1234567890.net",
                "me.too",
                "10.1.0.100"}) {
            checkSuccess(newRampartString(hostname));
        }
    }

    @Test
    public void lookupInvalidHostnames() {
        for (String hostname : new String[] {
                "",
                ".",
                ".a",
                "..",
                ".com",
                // too long
                "abcdefghijklmnopqrstuv1234567890abcdefghijklmnopqrstuv1234567890",
                " me.too ",
                "*.com",
                "example*.net"}) {
            final String message = getFailureMessage(newRampartList(newRampartString(hostname)));
            assertThat(message, equalTo("invalid hostname \"" + hostname + "\" in \"lookup\" declaration"));
        }
    }

    /**
     * Checks that parsing of the given input(s) is successfull.
     * @param inputs inputs to pass to the parser.
     */
    public void checkSuccess(RampartObject... inputs) throws InvalidRampartRuleException {
        RampartDnsLookupParser.parseTarget(RampartPrimitives.newRampartList(inputs));
    }


    /**
     * Attempts parsing of the <code>input</code>, ensures it fails
     * and returns the exception's message.
     * @param input input to parse.
     * @return failure message.
     */
    public String getFailureMessage(RampartList input) {
        final InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartDnsLookupParser.parseTarget(input));
        return thrown.getMessage();
    }
}
