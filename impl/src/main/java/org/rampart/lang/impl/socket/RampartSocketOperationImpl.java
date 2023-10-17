package org.rampart.lang.impl.socket;

import static org.rampart.lang.api.constants.RampartSocketConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;

import java.util.List;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.impl.utils.NetworkValidationUtils;
import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartSocketOperationImpl implements RampartSocketOperation {

    private final RampartConstant operationName;
    private final List<RampartSocketType> socketTypes;
    private final List<NetworkAddress> addresses;
    private final String toStringValue;
    private final int hashCode;

    public RampartSocketOperationImpl(RampartConstant operationName, List<RampartSocketType> socketTypes, List<NetworkAddress> addresses) {
        this.operationName = operationName;
        this.socketTypes = socketTypes;
        this.addresses = addresses;
        this.toStringValue = createStringRepresentation();
        this.hashCode = ObjectUtils.hash(operationName, socketTypes, addresses);
    }

    // @Override
    public RampartAddress getTargetAddress(RampartSocketType type) {
        return getTargetNetworkAddress(type).getRampartAddress();
    }

    // @Override
    @Deprecated // since 2.6.0, RAMPART-331, use getTargetAddress
    public RampartString getTargetIpAddress(RampartSocketType type) {
        RampartAddress rampartAddress = getTargetNetworkAddress(type).getRampartAddress();
        if (RampartPrimitives.toJavaBoolean(rampartAddress.hasIpAddress())) {
            return rampartAddress.getIpAddress();
        } else if (RampartPrimitives.toJavaBoolean(rampartAddress.hasHostname())) {
            return newRampartString(NetworkValidationUtils.getNormalizedIpAddress(
                    rampartAddress.getHostname().toString()));
        }
        // our coding error
        throw new IllegalStateException("RampartAddress has to have either hostname or IP address");
    }

    // @Override
    public RampartInteger getTargetFromPort(RampartSocketType type) {
        return getTargetNetworkAddress(type).getFromPort();
    }

    // @Override
    public RampartInteger getTargetToPort(RampartSocketType type) {
        return getTargetNetworkAddress(type).getToPort();
    }

    private NetworkAddress getTargetNetworkAddress(RampartSocketType type) {
        for (int i = 0; i < socketTypes.size(); i++) {
            if (socketTypes.get(i) == type) {
                return addresses.get(i);
            }
        }
        throw new IllegalArgumentException("unsupported socket type [" + type + "]");
    }

    // @Override
    public RampartBoolean onClientSocket() {
        for (RampartSocketType socket : socketTypes) {
            if (socket == RampartSocketType.CLIENT) {
                return RampartBoolean.TRUE;
            }
        }
        return RampartBoolean.FALSE;
    }

    // @Override
    public RampartBoolean onServerSocket() {
        for (RampartSocketType socket : socketTypes) {
            if (socket == RampartSocketType.SERVER) {
                return RampartBoolean.TRUE;
            }
        }
        return RampartBoolean.FALSE;
    }

    // @Override
    public RampartConstant getOperationName() {
        return operationName;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartSocketOperationImpl)) {
            return false;
        }
        RampartSocketOperationImpl otherSocketOperation = (RampartSocketOperationImpl) other;
        return ObjectUtils.equals(operationName, otherSocketOperation.operationName)
                && ObjectUtils.equals(socketTypes, otherSocketOperation.socketTypes)
                && ObjectUtils.equals(addresses, otherSocketOperation.addresses);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private String createStringRepresentation() {
        StringBuilder builder = new StringBuilder(operationName.toString()).append("(");
        if (operationName.equals(BIND_KEY)) {
            String delim = "";
            if (onClientSocket() == RampartBoolean.TRUE) {
                builder.append(CLIENT_KEY).append(": ").append(getTargetNetworkAddress(RampartSocketType.CLIENT));
                delim = ", ";
            }
            if (onServerSocket() == RampartBoolean.TRUE) {
                builder.append(delim).append(SERVER_KEY).append(": ").append(getTargetNetworkAddress(RampartSocketType.SERVER));
            }
        } else if (operationName.equals(ACCEPT_KEY)
                || operationName.equals(CONNECT_KEY)) {
            builder.append(addresses.get(0));
        }
        return builder.append(")").toString();
    }

}
