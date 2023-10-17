package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.socket.RampartAddressIpImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartSocketConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartSocketOperationBuilderTest {
    private static final RampartString LOOPBACK = newRampartString("127.0.0.1");

    private RampartSocketOperationBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartSocketOperationBuilder();
    }

    @Test
    public void createSocketBindOperationClientAndServerDefaultFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                       .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK))
                       .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK))
                       .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(client: \"127.0.0.1:0\", server: \"127.0.0.1:0\")"));
    }

    @Test
    public void createSocketBindOperationClientAndServerFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                        .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(client: \"127.0.0.1:2-23\", server: \"127.0.0.1:2-23\")"));
    }

    @Test
    public void createSocketBindOperationClient() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                        .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(client: \"127.0.0.1:0\")"));
    }

    @Test
    public void createSocketBindOperationClientFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                        .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(client: \"127.0.0.1:2-23\")"));
    }

    @Test
    public void createSocketBindOperationServer() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                        .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(server: \"127.0.0.1:0\")"));
    }

    @Test
    public void createSocketBindOperationServerFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(BIND_KEY)
                        .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "bind(server: \"127.0.0.1:2-23\")"));
    }

    @Test
    public void createSocketAcceptOperation() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(ACCEPT_KEY)
                        .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "accept(\"127.0.0.1:0\")"));
    }

    @Test
    public void createSocketAcceptOperationFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(ACCEPT_KEY)
                        .addNetworkAddress(RampartSocketType.SERVER, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "accept(\"127.0.0.1:2-23\")"));
    }

    @Test
    public void createSocketConnectOperation() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(CONNECT_KEY)
                        .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "connect(\"127.0.0.1:0\")"));
    }

    @Test
    public void createSocketConnectOperationFromToPort() {
        RampartSocketOperation socketOperation =
                builder.addOperationName(CONNECT_KEY)
                        .addNetworkAddress(RampartSocketType.CLIENT, new RampartAddressIpImpl(LOOPBACK), newRampartInteger(2), newRampartInteger(23))
                        .createRampartObject();
        assertThat(socketOperation.toString(), equalTo(
                "connect(\"127.0.0.1:2-23\")"));
    }
}
