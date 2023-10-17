package org.rampart.lang.impl.socket.parsers.v2;

import static org.rampart.lang.api.constants.RampartSocketConstants.*;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.impl.socket.RampartAddressHostnameImpl;
import org.rampart.lang.impl.socket.RampartAddressIpImpl;
import org.rampart.lang.impl.socket.RampartSocketOperationImpl;
import org.rampart.lang.impl.socket.NetworkAddress;
import org.rampart.lang.java.RampartPrimitives;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.core.RampartAction;
import org.rampart.lang.api.core.RampartActionTarget;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.core.RampartActionImpl;
import org.rampart.lang.impl.core.RampartActionWithAttributeImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public final class RampartSocketCrossValidation2_1Test {

    private final static RampartString LOOPBACK_IP_ADDRESS = newRampartString("127.0.0.1");
    private final static RampartString DEFAULT_ROUTE_IP_ADDRESS = newRampartString("0.0.0.0");

    @Test
    public void bindOperationWithProtectActionNoTargets() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(newOp(BIND_KEY), noAttrAction(RampartActionType.PROTECT));
    }

    @Test
    public void acceptOperationWithProtectActionNoTargets() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(newOp(ACCEPT_KEY), noAttrAction(RampartActionType.PROTECT));
    }

    @Test
    public void connectOperationWithProtectActionNoTargets() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(newOp(CONNECT_KEY), noAttrAction(RampartActionType.PROTECT));
    }

    @Test
    public void bindOperationWithProtectActionConnectionTarget() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketParser2_1.crossValidate(
                        newOp(BIND_KEY), attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
                      )
        );

        assertThat(thrown.getMessage(), equalTo("\"bind\" is not supported with action target \"connection\""));
    }

    @Test
    public void connectOperationWithProtectActionConnectionTarget() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketParser2_1.crossValidate(
                        newOp(CONNECT_KEY), attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
                      )
        );

        assertThat(thrown.getMessage(), equalTo("\"connect\" is not supported with action target \"connection\""));
    }

    @Test
    public void bindOperationWithProtectActionIpv4ClientAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newClientOp(BIND_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void bindOperationWithProtectActionIpv4ServerAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(BIND_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void bindOperationWithProtectActionWildcardIpv4ClientAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newClientOp(BIND_KEY, new RampartAddressIpImpl(DEFAULT_ROUTE_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void bindOperationWithProtectActionIpv4ClientAddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newClientOp(BIND_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 0),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void bindOperationWithProtectActionWildcardIpv4ServerAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(BIND_KEY, new RampartAddressIpImpl(DEFAULT_ROUTE_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void bindOperationWithProtectActionIpv4ServerAddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(BIND_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 0),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void connectOperationWithProtectActionIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(CONNECT_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void connectOperationWithProtectActionWildcardIpv4ClientAddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newClientOp(CONNECT_KEY, new RampartAddressIpImpl(DEFAULT_ROUTE_IP_ADDRESS), 80),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void connectOperationWithProtectActionIpv4ClientAddressAndWildcardPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newClientOp(CONNECT_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 0),
                noAttrAction(RampartActionType.PROTECT)
        );
    }

    @Test
    public void acceptOperationWithProtectActionConnectionTargetIpv4AddressAndSinglePort() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketParser2_1.crossValidate(
                        newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 80),
                        attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
                      )
        );

        assertThat(thrown.getMessage(), equalTo("cannot configure action target \"connection\" with ip address \"127.0.0.1\""));
    }

    @Test
    public void acceptOperationWithProtectActionWildcardIpv4AddressAndSinglePort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(DEFAULT_ROUTE_IP_ADDRESS), 80),
                attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
        );
    }

    @Test
    public void acceptOperationWithProtectActionWildcardIpv6AddressAndSinglePort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(newRampartString("0:0:0:0:0:0:0:0")), 80),
                attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
        );
    }

    @Test
    public void acceptOperationWithProtectActionIpv4AddressAndWildcardPort() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartSocketParser2_1.crossValidate(
                        newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(LOOPBACK_IP_ADDRESS), 0),
                        attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
                      )
        );

        assertThat(thrown.getMessage(), equalTo("cannot configure action target \"connection\" with ip address \"127.0.0.1\""));
    }

    @Test
    public void acceptOperationWithProtectActionWildcardIpv4AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(DEFAULT_ROUTE_IP_ADDRESS), 0),
                attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
        );
    }

    @Test
    public void acceptOperationWithProtectActionWildcardIpv6AddressAndPort() throws InvalidRampartRuleException {
        RampartSocketParser2_1.crossValidate(
                newServerOp(ACCEPT_KEY, new RampartAddressIpImpl(newRampartString("0:0:0:0:0:0:0:0")), 0),
                attrAction(RampartActionType.PROTECT, RampartActionTarget.CONNECTION)
        );
    }


    /** Creates a simple operation. */
    private static RampartSocketOperation newOp(RampartConstant operationName) {
        final RampartAddress addr = new RampartAddressHostnameImpl(RampartPrimitives.newRampartString("globalhost"));
        return new RampartSocketOperationImpl(operationName, Collections.emptyList(),
                Arrays.asList(new NetworkAddress(addr, 1, 2))
        );
    }


    /** Creates a simple server operation. */
    private static RampartSocketOperation newServerOp(RampartConstant operationName, RampartAddress address, int fromPort) {
        return new RampartSocketOperationImpl(operationName,
                Arrays.asList(RampartSocketType.SERVER),
                Arrays.asList(new NetworkAddress(address, fromPort, fromPort))
        );
    }


    /** Creates a simple client operation. */
    private static RampartSocketOperation newClientOp(RampartConstant operationName, RampartAddress address, int fromPort) {
        return new RampartSocketOperationImpl(operationName,
                Arrays.asList(RampartSocketType.CLIENT),
                Arrays.asList(new NetworkAddress(address, fromPort, fromPort))
        );
    }

    /** Creates a simple no-action operation. */
    private static RampartAction noAttrAction(RampartActionType actionType) {
        return new RampartActionImpl(actionType, null, RampartSeverity.UNKNOWN, RampartBoolean.FALSE, null);
    }

    /** Creates a simple no-action operation. */
    private static RampartAction attrAction(RampartActionType actionType, RampartActionTarget target) {
        return new RampartActionWithAttributeImpl(noAttrAction(actionType), target, null, RampartList.EMPTY);
    }

}
