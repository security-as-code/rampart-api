package org.rampart.lang.impl.socket;

import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.socket.NetworkAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NetworkAddressTest {

    @Test
    public void isMostLikelyIPv6Positive() {
        for (String ip6 : new String[] {
                "::",
                "::0",
                "0::",
                "0::0"}) {
            assertThat("ip6 " + ip6 + " should be valid",
                    NetworkAddress.isMostLikelyIPv6(ip6), equalTo(true));
        }
    }

    @Test
    public void isMostLikelyIPv6Negative() {
        for (String ip6 : new String[] {
                ": :"}) {
            assertThat("ip6 " + ip6 + " should be invalid",
                    NetworkAddress.isMostLikelyIPv6(ip6), equalTo(false));
        }
    }
}
