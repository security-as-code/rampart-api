package org.rampart.lang.api.core;

import static org.rampart.lang.api.core.RampartActionAttribute.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

import java.util.NoSuchElementException;

public enum RampartActionTarget implements RampartObject {

    HTTP_SESSION("http-session", REGENERATE_ID.getName()),
    HTTP_RESPONSE("http-response", SET_HEADER.getName(), NEW_RESPONSE.getName()),
    CONNECTION("connection", SECURE.getName(), UPGRADE_TLS.getName());

    private final RampartConstant name;
    private final RampartList supportedAttributes;

    RampartActionTarget(final String name, final RampartConstant... attributes) {
        this.name = new RampartConstant() {
            @Override
            public String toString() {
                return name;
            }
        };

        this.supportedAttributes = new RampartList() {
            @Override
            public RampartObjectIterator getObjectIterator() {
                return new RampartObjectIterator() {
                    private int cursor = 0;

                    // @Override
                    public RampartBoolean hasNext() {
                        return cursor != attributes.length ? RampartBoolean.TRUE : RampartBoolean.FALSE;
                    }

                    // @Override
                    public RampartObject next() {
                        if (cursor >= attributes.length) {
                            throw new NoSuchElementException();
                        }
                        return attributes[cursor++];
                    }
                };
            }
        };
    }

    public RampartConstant getName() {
        return name;
    }

    public RampartList getSupportedActionAttributes() {
        return supportedAttributes;
    }

    public static RampartActionTarget fromConstant(RampartConstant target) {
        for (RampartActionTarget actionTarget : RampartActionTarget.values()) {
            if (actionTarget.name.equals(target)) {
                return actionTarget;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
