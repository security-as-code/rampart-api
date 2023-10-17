package org.rampart.lang.api.utils;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;

import java.util.NoSuchElementException;

public class RampartUtils {
    public static RampartString newRampartString(final String s) {
        return new RampartString() {
            @Override
            public String toString() {
                return s;
            }
        };
    }

    public static RampartConstant newRampartConstant(final String s) {
        return new RampartConstant() {
            @Override
            public String toString() {
                return s;
            }
        };
    }

    public static RampartNamedValue newRampartNamedValue(final RampartConstant key, final RampartObject value) {
        return new RampartNamedValue() {
            @Override
            public RampartConstant getName() {
                return key;
            }

            @Override
            public RampartObject getRampartObject() {
                return value;
            }
        };
    }

    public static RampartList newRampartList(final RampartObject... rampartObjects) {
        return new RampartList() {
            @Override
            public RampartObjectIterator getObjectIterator() {
                return new RampartObjectIterator() {
                    private int cursor = 0;

                    // @Override
                    public RampartBoolean hasNext() {
                        return cursor != rampartObjects.length ? RampartBoolean.TRUE : RampartBoolean.FALSE;
                    }

                    // @Override
                    public RampartObject next() {
                        if (cursor >= rampartObjects.length) {
                            throw new NoSuchElementException();
                        }
                        return rampartObjects[cursor++];
                    }
                };
            }
        };
    }
}
