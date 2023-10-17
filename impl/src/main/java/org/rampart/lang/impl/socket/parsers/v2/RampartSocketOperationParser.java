package org.rampart.lang.impl.socket.parsers.v2;

import static org.rampart.lang.api.constants.RampartSocketConstants.ACCEPT_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.BIND_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.CONNECT_KEY;
import static org.rampart.lang.api.constants.RampartSocketConstants.SOCKET_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartNamedValue;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.socket.RampartAddressHostnameImpl;
import org.rampart.lang.impl.socket.RampartAddressIpImpl;
import org.rampart.lang.impl.socket.RampartSocketOperationImpl;
import org.rampart.lang.impl.socket.NetworkAddress;
import org.rampart.lang.impl.utils.NetworkValidationUtils;
import org.rampart.lang.java.RampartPrimitives;

/** Parser for the socket operations. */
public final class RampartSocketOperationParser {
    private RampartSocketOperationParser() {
        throw new UnsupportedOperationException();
    }

    /** Keys supported by this parser. */
    public static final RampartConstant[] SUPPORTED_KEYS = {ACCEPT_KEY, BIND_KEY, CONNECT_KEY};


    public static RampartSocketOperation parseOperation(Map<String, RampartList> visitorSymbolTable)
            throws InvalidRampartRuleException {
        RampartList operationList;
        RampartSocketOperation operation = null;

        for (RampartConstant operationName : SUPPORTED_KEYS) {
            if ((operationList = visitorSymbolTable.get(operationName.toString())) != null) {
                if (operation != null) {
                    throw new InvalidRampartRuleException("detected declaration of \"" + operationName + "\" but \""
                            + operation.getOperationName() + "\" is already defined");
                }

                if (BIND_KEY.equals(operationName)) {
                    operation = getSocketOperation(operationList, operationName);
                } else if (ACCEPT_KEY.equals(operationName)) {
                    operation = getSocketOperation(operationList, operationName, RampartSocketType.SERVER);
                } else if (CONNECT_KEY.equals(operationName)) {
                    operation = getSocketOperation(operationList, operationName, RampartSocketType.CLIENT);
                }
            }
        }
        if (operation == null) {
            throw new InvalidRampartRuleException("one of the \"" + SOCKET_KEY + "\" rule declarations: \"" + ACCEPT_KEY
                    + "\", \"" + BIND_KEY + "\" and \"" + CONNECT_KEY + "\" must be provided");
        }
        return operation;
    }

    private static RampartSocketOperation getSocketOperation(RampartList operationList, RampartConstant operationName,
                                                             RampartSocketType socketType) throws InvalidRampartRuleException {
        RampartList list = operationList;
        if (RampartPrimitives.toJavaInt(list.size()) != 1) {
            throw new InvalidRampartRuleException(
                    "\"" + operationName + "\" declaration only supports a single parameter");
        }
        return new RampartSocketOperationImpl(
                operationName,
                Arrays.asList(socketType),
                Arrays.asList(getNetworkAddress(list.getFirst(), operationName)));
    }


    private static RampartSocketOperation getSocketOperation(RampartList operationList, RampartConstant operationName)
            throws InvalidRampartRuleException {
        HashMap<RampartSocketType, NetworkAddress> socketSpecs = new HashMap<RampartSocketType, NetworkAddress>();
        RampartObjectIterator it = operationList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject parameter = it.next();
            if (!(parameter instanceof RampartNamedValue)) {
                throw new InvalidRampartRuleException("invalid parameter \"" + parameter + "\" for \"" + operationName
                        + "\" declaration - only name value pairs allowed");
            }
            RampartNamedValue socketSpec = (RampartNamedValue) parameter;
            RampartSocketType socketType = RampartSocketType.fromConstant(socketSpec.getName());
            if (socketType == null) {
                throw new InvalidRampartRuleException("unrecognized parameter key \"" + socketSpec.getName()
                        + "\" to the \"" + operationName + "\" declaration");
            }
            if (socketSpecs.containsKey(socketType)) {
                throw new InvalidRampartRuleException("duplicate socket type specified \"" + socketSpec.getName()
                        + "\" for \"" + operationName + "\" declaration");
            }
            socketSpecs.put(socketType, getNetworkAddress(socketSpec.getRampartObject(), operationName));
        }
        if (socketSpecs.isEmpty()) {
            throw new InvalidRampartRuleException("\"" + operationName + "\" declaration has no parameters");
        }
        ArrayList<RampartSocketType> types = new ArrayList<RampartSocketType>(socketSpecs.size());
        ArrayList<NetworkAddress> addresses = new ArrayList<NetworkAddress>(socketSpecs.size());
        for (RampartSocketType type : socketSpecs.keySet()) {
            types.add(type);
            addresses.add(socketSpecs.get(type));
        }
        return new RampartSocketOperationImpl(operationName, types, addresses);
    }


    private static NetworkAddress getNetworkAddress(RampartObject addressAndPort, RampartConstant operationName)
            throws InvalidRampartRuleException {
        if (!(addressAndPort instanceof RampartString)) {
            throw new InvalidRampartRuleException(
                    "parameter to \"" + operationName + "\" declaration must be a string literal");
        }
        int[] portRange = NetworkValidationUtils.validatePortRange(addressAndPort.toString());
        String hostPart = NetworkValidationUtils.getHostPart(addressAndPort.toString());
        if (hostPart == null) {
            throw new InvalidRampartRuleException("invalid host in address \"" + addressAndPort + "\"");
        }
        if (NetworkAddress.isIPv4(hostPart)) {
            return new NetworkAddress(
                    new RampartAddressIpImpl(newRampartString(hostPart)),
                    portRange[0],
                    portRange[1]);
        } else if (NetworkAddress.isMostLikelyIPv6(hostPart)) {
            String ip  = NetworkValidationUtils.getNormalizedIpAddress(hostPart);
            if (ip == null) {
                throw new InvalidRampartRuleException("invalid ip address in \"" + addressAndPort + "\"");
            }
            return new NetworkAddress(
                    new RampartAddressIpImpl(newRampartString(ip)),
                    portRange[0],
                    portRange[1]);
        } else {
            // it is not legal for the hostname to contain: ":" where as this is very likely to be invalid IPv6
            if (hostPart.contains(":")) {
                throw new InvalidRampartRuleException("invalid ip address in \"" + addressAndPort + "\"");
            }
            return new NetworkAddress(
                    new RampartAddressHostnameImpl(newRampartString(hostPart)),
                    portRange[0],
                    portRange[1]);
        }
    }

}
