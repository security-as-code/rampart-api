package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.api.socket.RampartSocketType;

public class RampartSocketConstants extends RampartGeneralConstants {

    public static final RampartConstant SOCKET_KEY = RampartRuleType.SOCKET.getName();
    public static final RampartConstant ACCEPT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "accept";
        }
    };
    public static final RampartConstant BIND_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "bind";
        }
    };
    public static final RampartConstant CONNECT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "connect";
        }
    };
    public static final RampartConstant CLIENT_KEY = RampartSocketType.CLIENT.getName();
    public static final RampartConstant SERVER_KEY = RampartSocketType.SERVER.getName();
    public static final RampartString IPV4_WILDCARD = new RampartString() {
        @Override
        public String toString() {
            return "0.0.0.0";
        }
    };
    public static final RampartString IPV6_WILDCARD = new RampartString() {
        @Override
        public String toString() {
            return "0:0:0:0:0:0:0:0";
        }
    };
}
