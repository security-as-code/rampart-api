package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.socket.RampartAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartAddressBuilderTest {
    private RampartAddressBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartAddressBuilder();
    }

    @Test
    public void createIPv4RampartAddress() {
        RampartAddress address = builder.addAddress(newRampartString("127.0.0.1")).createRampartObject();
        assertThat(address.hasIpAddress(), equalTo(RampartBoolean.TRUE));
        assertThat(address.hasHostname(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void createIPv6RampartAddress() {
        RampartAddress address = builder.addAddress(newRampartString("2001:0db8:85a3:0000:0000:8a2e:0370:7334"))
                                     .createRampartObject();
        assertThat(address.hasIpAddress(), equalTo(RampartBoolean.TRUE));
        assertThat(address.hasHostname(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void createIPv6ShortenedRampartAddress() {
        RampartAddress address = builder.addAddress(newRampartString("::")).createRampartObject();
        assertThat(address.hasIpAddress(), equalTo(RampartBoolean.TRUE));
        assertThat(address.hasHostname(), equalTo(RampartBoolean.FALSE));
    }

    @Test
    public void createHostnameRampartAddress() {
        RampartAddress address = builder.addAddress(newRampartString("rampart.org")).createRampartObject();
        assertThat(address.hasHostname(), equalTo(RampartBoolean.TRUE));
        assertThat(address.hasIpAddress(), equalTo(RampartBoolean.FALSE));
    }
}
