package org.rampart.lang.api.socket;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;

public interface RampartSocketOperation extends RampartObject {
    RampartConstant getOperationName();
    RampartBoolean onClientSocket();
    RampartBoolean onServerSocket();
    RampartAddress getTargetAddress(RampartSocketType type);
    @Deprecated // since 2.5.0
    RampartString getTargetIpAddress(RampartSocketType type);
    RampartInteger getTargetFromPort(RampartSocketType type);
    RampartInteger getTargetToPort(RampartSocketType type);
}
