package org.rampart.lang.impl.socket.parsers.v2;

import static org.rampart.lang.api.socket.RampartSocketType.SERVER;
import static org.rampart.lang.api.constants.RampartSocketConstants.*;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.socket.RampartAddress;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public final class RampartSocketOperationParserTest {
    @Test
    public void unknownOperation() {
        final String message = failParse(newRampartConstant("unknown"));
        assertThat(message, equalTo(
                "one of the \"socket\" rule declarations: \"accept\", \"bind\" and \"connect\" must be provided"));
    }

    @Test
    public void noOperationSpecified() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketOperationParser.parseOperation(Collections.emptyMap()));

        assertThat(thrown.getMessage(), equalTo(
                "one of the \"socket\" rule declarations: \"accept\", \"bind\" and \"connect\" must be provided"));
    }

    @Test
    public void bindOperationEmpty() {
        final String message = failParse(BIND_KEY);

        assertThat(message, equalTo("\"bind\" declaration has no parameters"));
    }

    @Test
    public void bindOperationWithClientIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
            parse(BIND_KEY,
                newRampartNamedValue(CLIENT_KEY, newRampartString("127.0.0.1:8080"))
            );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithClientIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
            parse(BIND_KEY,
                newRampartNamedValue(CLIENT_KEY, newRampartString("fe80::5fd3:c1b0:5624:fb39:8080"))
            );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithInvalidKey() {
        final String message =
            failParse(BIND_KEY,
                newRampartNamedValue(newRampartConstant("endpoint"), newRampartString("127.0.0.1:8080"))
            );

        assertThat(message, equalTo("unrecognized parameter key \"endpoint\" to the \"bind\" declaration"));
    }

    @Test
    public void bindOperationWithInvalidParameterType() {
        final String message = failParse(BIND_KEY, newRampartString("127.0.0.1:8080"));
        assertThat(message, equalTo("invalid parameter \"127.0.0.1:8080\" for \"bind\" declaration - only name value pairs allowed"));
    }

    @Test
    public void bindOperationWithClientWildcardIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
            parse(BIND_KEY,
                newRampartNamedValue(CLIENT_KEY, newRampartString("0.0.0.0:8080"))
            );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithClientWildcardIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString(":::8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0:0:0:0:0:0:0:0")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithClientIpv4AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("127.0.0.1:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithClientIpv6AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithClientWildcardIpv4AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("0.0.0.0:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithClientWildcardIpv6AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("0:0:0:0:0:0:0:0:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0:0:0:0:0:0:0:0")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithClientIpv4AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("127.0.0.1:80-8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithClientIpv6AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("FE80::202:B3FF:FE1E:8329:80-8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:202:b3ff:fe1e:8329")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("127.0.0.1:8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("fe80::5fd3:c1b0:5624:fb39:8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerWildcardIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("0.0.0.0:8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerWildcardIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString(":::8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0:0:0:0:0:0:0:0")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerIpv4AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("127.0.0.1:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithServerIpv6AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithServerWildcardIpv4AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("0.0.0.0:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithServerWildcardIpv6AddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("0:0:0:0:0:0:0:0:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0:0:0:0:0:0:0:0")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithServerIpv4AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("127.0.0.1:80-8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithServerIpv6AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("FE80::202:B3FF:FE1E:8329:80-8080"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:202:b3ff:fe1e:8329")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void bindOperationWithClientAndServerWildcardIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation =
                parse(BIND_KEY,
                        newRampartNamedValue(CLIENT_KEY, newRampartString("0.0.0.0:0")),
                        newRampartNamedValue(SERVER_KEY, newRampartString("0.0.0.0:0"))
                );

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(BIND_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));

            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(0)));

            rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("0.0.0.0")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(0)));
        });
    }

    @Test
    public void bindOperationWithDuplicateClientKey() {
        final String message =
            failParse(BIND_KEY,
                newRampartNamedValue(CLIENT_KEY, newRampartString("0.0.0.0:0")),
                newRampartNamedValue(CLIENT_KEY, newRampartString("127.0.0.1:0"))
            );


        assertThat(message, equalTo("duplicate socket type specified \"client\" for \"bind\" declaration"));
    }

    @Test
    public void bindOperationWithDuplicateServerKey() {
        final String message =
                failParse(BIND_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("0.0.0.0:0")),
                    newRampartNamedValue(SERVER_KEY, newRampartString("127.0.0.1:0"))
                );


        assertThat(message, equalTo("duplicate socket type specified \"server\" for \"bind\" declaration"));
    }

    @Test
    public void acceptOperationEmpty() {
        final String message = failParse(ACCEPT_KEY);

        assertThat(message, equalTo(
                "\"accept\" declaration only supports a single parameter"));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("127.0.0.1:8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void acceptOperationWithIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("fe80::5fd3:c1b0:5624:fb39:8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void acceptOperationWithInvalidKey() {
        final String message =
            failParse(ACCEPT_KEY,
                newRampartNamedValue(newRampartConstant("endpoint"), newRampartString("127.0.0.1:8080"))
            );

        assertThat(message, equalTo("parameter to \"accept\" declaration must be a string literal"));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("127.0.0.1:80-8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
            assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(SERVER), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void acceptOperationWithServerKeyValue() {
        final String message =
                failParse(ACCEPT_KEY,
                    newRampartNamedValue(SERVER_KEY, newRampartString("127.0.0.1:80"))
                );

        assertThat(message, equalTo("parameter to \"accept\" declaration must be a string literal"));
    }

    @Test
    public void connectOperationEmpty() {
        final String message = failParse(CONNECT_KEY);

        assertThat(message, equalTo(
                "\"connect\" declaration only supports a single parameter"));
    }

    @Test
    public void connectOperationWithIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(CONNECT_KEY, newRampartString("127.0.0.1:8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(CONNECT_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void connectOperationWithIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(CONNECT_KEY, newRampartString("fe80::5fd3:c1b0:5624:fb39:8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(CONNECT_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("fe80:0:0:0:5fd3:c1b0:5624:fb39")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void connectOperationWithInvalidKey() {
        final String message =
            failParse(CONNECT_KEY,
                newRampartNamedValue(newRampartConstant("endpoint"), newRampartString("127.0.0.1:8080"))
            );

        assertThat(message, equalTo("parameter to \"connect\" declaration must be a string literal"));
    }

    @Test
    public void connectOperationWithIpv4AddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(CONNECT_KEY, newRampartString("127.0.0.1:80-8080"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(CONNECT_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.getIpAddress(), equalTo(newRampartString("127.0.0.1")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(80)));
            assertThat(operation.getTargetToPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(8080)));
        });
    }

    @Test
    public void connectOperationWithClientKeyValue() {
        final String message =
                failParse(CONNECT_KEY,
                    newRampartNamedValue(CLIENT_KEY, newRampartString("127.0.0.1:80"))
                );

        assertThat(message, equalTo("parameter to \"connect\" declaration must be a string literal"));
    }

    @Test
    public void connectAndAcceptOperationsInvalidSetup() {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(CONNECT_KEY.toString(), newRampartList(newRampartString("127.0.0.1:80")));
        symbolTable.put(ACCEPT_KEY.toString(), newRampartList(newRampartString("127.0.0.1:80")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketOperationParser.parseOperation(symbolTable));

        assertThat(thrown.getMessage(), equalTo("detected declaration of \"connect\" but \"accept\" is already defined"));
    }

    @Test
    public void acceptOperationWithPortRangeMissingStartPort() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:-8080"));
        assertThat(message, equalTo("invalid start port \"\""));
    }

    @Test
    public void acceptOperationWithPortRangeMissingEndPort() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:80-"));
        assertThat(message, equalTo("invalid end port \"\""));
    }

    @Test
    public void acceptOperationWithNoPorts() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:"));
        assertThat(message, equalTo("invalid address \"127.0.0.1:\""));
    }

    @Test
    public void acceptOperationWithPortRangeMissingStartAndEndPorts() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:-"));
        assertThat(message, equalTo("invalid start port \"\""));
    }

    @Test
    public void acceptOperationWithPortNotNumber() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:87*7"));
        assertThat(message, equalTo("invalid start port \"87*7\""));
    }

    @Test
    public void acceptOperationWithPortRangeEndPortNotNumber() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:80-87*7"));
        assertThat(message, equalTo("invalid end port \"87*7\""));
    }

    @Test
    public void acceptOperationWithPortRangeColonUsed() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:80:877"));
        assertThat(message, equalTo("invalid ip address in \"127.0.0.1:80:877\""));
    }

    @Test
    public void acceptOperationWithIncorrectIpv4AddressThatCouldBeDomain() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("127.0.0.300:80"));

        assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
        assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
        // '300' is weird, and unlikely but still a valid root domain in LAN,
        // so we will pass it as if it was a FQDN to the Agent
        RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
        assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.TRUE));
        assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.FALSE));
        assertThat(rampartAddress.getHostname(), equalTo(newRampartString("127.0.0.300")));
        assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(80)));
    }

    @Test
    public void acceptOperationWithInvalidIpv4Address() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("127.0.0.24hours:80"));

        assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
        assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
        // '24hours' is weird, and unlikely but still a valid root domain in LAN,
        // so we will pass it as if it was a FQDN to the Agent
        RampartAddress rampartAddress = operation.getTargetAddress(SERVER);
        assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.TRUE));
        assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.FALSE));
        assertThat(rampartAddress.getHostname(), equalTo(newRampartString("127.0.0.24hours")));
        assertThat(operation.getTargetFromPort(SERVER), equalTo(newRampartInteger(80)));
    }

    @Test
    public void acceptOperationWithIpv6AddressWithDot() {
        final String message = failParse(ACCEPT_KEY, newRampartString(":.1:80"));
        assertThat(message, equalTo("invalid ip address in \":.1:80\""));
    }

    @Test
    public void acceptOperationWithInvalidIpv6Address() {
        final String message = failParse(ACCEPT_KEY, newRampartString("2001:fffff:85a3::8a2e:0370:7334:80"));
        assertThat(message, equalTo("invalid ip address in \"2001:fffff:85a3::8a2e:0370:7334:80\""));
    }

    @Test
    public void acceptOperationWithAddressWithNoPort() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1"));
        assertThat(message, equalTo("invalid address \"127.0.0.1\""));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortAboveRange() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:100000"));
        assertThat(message, equalTo("invalid start port \"100000\""));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortAtEdgeOfRange() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:65536"));
        assertThat(message, equalTo("invalid start port \"65536\""));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortNegative() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:-1"));
        assertThat(message, equalTo("invalid start port \"\""));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortRangeNegativePort() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:80--1"));
        assertThat(message, equalTo("invalid end port \"-1\""));
    }

    @Test
    public void acceptOperationWithIpv4AddressAndPortPortRangeEndBiggerThanStart() {
        final String message = failParse(ACCEPT_KEY, newRampartString("127.0.0.1:8080-80"));
        assertThat(message, equalTo("invalid combination for port range \"8080-80\""));
    }

    @Test
    public void acceptOperationWithFqdnAddressAndPortRange() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(ACCEPT_KEY, newRampartString("localhost:80-8080"));

        RampartSocketType type = RampartSocketType.SERVER;
        assertThat(operation.getOperationName(), equalTo(ACCEPT_KEY));
        assertThat(operation.onServerSocket(), equalTo(RampartBoolean.TRUE));
        RampartAddress rampartAddress = operation.getTargetAddress(type);
        assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.TRUE));
        assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.FALSE));
        assertThat(rampartAddress.getHostname(), equalTo(newRampartString("localhost")));
        assertThat(operation.getTargetFromPort(type), equalTo(newRampartInteger(80)));
        assertThat(operation.getTargetToPort(type), equalTo(newRampartInteger(8080)));
    }

    @Test
    public void connectOperationWithFqdnAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketOperation operation = parse(CONNECT_KEY, newRampartString("service.com:443"));

        assertAll(() -> {
            assertThat(operation.getOperationName(), equalTo(CONNECT_KEY));
            assertThat(operation.onClientSocket(), equalTo(RampartBoolean.TRUE));
            RampartAddress rampartAddress = operation.getTargetAddress(RampartSocketType.CLIENT);
            assertThat(rampartAddress.hasHostname(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartAddress.hasIpAddress(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartAddress.getHostname(), equalTo(newRampartString("service.com")));
            assertThat(operation.getTargetFromPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(443)));
            assertThat(operation.getTargetToPort(RampartSocketType.CLIENT), equalTo(newRampartInteger(443)));
        });
    }


    private static RampartSocketOperation parse(RampartConstant key, RampartObject... values) throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(key.toString(), newRampartList(values));
        return RampartSocketOperationParser.parseOperation(symbolTable);
    }

    /** Ensures the parsing fails and returns the message. */
    private static String failParse(RampartConstant key, RampartObject... values) {
        final InvalidRampartRuleException exception =
                assertThrows(InvalidRampartRuleException.class, () -> parse(key, values));
        return exception.getMessage();
    }
}
