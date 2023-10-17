package org.rampart.lang.java.builder;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.socket.RampartAddress;
import org.rampart.lang.api.socket.RampartSocketOperation;
import org.rampart.lang.api.socket.RampartSocketType;
import org.rampart.lang.impl.socket.RampartSocketOperationImpl;
import org.rampart.lang.impl.socket.NetworkAddress;

import java.util.ArrayList;

import static org.rampart.lang.java.RampartPrimitives.newRampartInteger;
import static org.rampart.lang.java.RampartPrimitives.toJavaInt;

public class RampartSocketOperationBuilder implements RampartObjectBuilder<RampartSocketOperation> {
    private RampartConstant operationName;
    private ArrayList<RampartSocketType> socketTypes = new ArrayList<RampartSocketType>();
    private ArrayList<NetworkAddress> addresses = new ArrayList<NetworkAddress>();

    public RampartSocketOperation createRampartObject() {
        return new RampartSocketOperationImpl(operationName, socketTypes, addresses);
    }

    public RampartSocketOperationBuilder addOperationName(RampartConstant operationName) {
        this.operationName = operationName;
        return this;
    }

    public RampartSocketOperationBuilder addNetworkAddress(
            RampartSocketType socketType,
            RampartAddress address,
            RampartInteger fromPort,
            RampartInteger toPort) {
        this.socketTypes.add(socketType);
        this.addresses.add(
                new NetworkAddress(
                        address,
                        toJavaInt(fromPort),
                        toJavaInt(toPort)));
        return this;
    }

    public RampartSocketOperationBuilder addNetworkAddress(
            RampartSocketType socketType,
            RampartAddress address,
            RampartInteger port) {
        return addNetworkAddress(socketType, address, port, port);
    }

    public RampartSocketOperationBuilder addNetworkAddress(
            RampartSocketType socketType,
            RampartAddress address) {
        return addNetworkAddress(socketType, address, newRampartInteger(0), newRampartInteger(0));
    }
}
