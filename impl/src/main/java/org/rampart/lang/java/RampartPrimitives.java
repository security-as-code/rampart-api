package org.rampart.lang.java;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.api.core.RampartSeverity;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public final class RampartPrimitives {
    private final static RampartInteger[] cachedPositiveIntegers = generateIntegerCache(1, 10);
    private final static RampartInteger[] cachedNegativeIntegers = generateIntegerCache(-1, 1);

    private static RampartInteger[] generateIntegerCache(final int multiplier, int maximum) {
        ArrayList<RampartInteger> list = new ArrayList<RampartInteger>();
        for(int i = 0; i <= maximum; i++) {
            final int finalI = i;
            list.add(new RampartInteger() {
                @Override
                public String toString() {
                    return String.valueOf(multiplier * finalI);
                }
            });
        }
        return list.toArray(new RampartInteger[list.size()]);
    }

    public static RampartFloat newRampartFloat(final float f) {
        return new RampartFloat() {
            @Override
            public String toString() {
                return String.valueOf(f);
            }
        };
    }

    public static RampartInteger newRampartInteger(final int i) {
        if (i <= 0 && cachedNegativeIntegers.length < i * -1) {
            return cachedNegativeIntegers[i * -1];
        } else if (i >= 0 && i < cachedPositiveIntegers.length) {
            return cachedPositiveIntegers[i];
        }
        return new RampartInteger() {
            @Override
            public String toString() {
                return String.valueOf(i);
            }
        };
    }

    public static RampartString newRampartString(final String s) {
        if (s == null) {
            return null;
        }
        return new RampartString() {
            @Override
            public String toString() {
                return s;
            }
        };
    }

    public static RampartConstant newRampartConstant(final String s) {
        if (s == null) {
            return null;
        }
        return new RampartConstant() {
            @Override
            public String toString() {
                return s;
            }
        };
    }

    public static RampartNamedValue newRampartNamedValue(final RampartConstant key, final RampartObject value) {
        if (key == null) {
            return null;
        }
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
        if (rampartObjects == null) {
            return null;
        }
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

    public static int toJavaInt(RampartInteger integer) {
        return Integer.parseInt(integer.toString());
    }

    public static boolean toJavaBoolean(RampartBoolean bool) {
        return bool == RampartBoolean.TRUE;
    }

    /**
     * Creates an RampartSeverity instance from an integer value as per CEF spec:
     * https://www.secef.net/wp-content/uploads/sites/10/2017/04/CommonEventFormatv23.pdf
     *
     * Low: 0-3
     * Medium: 4-6
     * High: 7-8
     * Very-High: 9-10
     * Unknown: Anything else
     * @param severity integer value to create RampartSeverity from
     * @return RampartSeverity representing the integer value passed
     */
    public static RampartSeverity toRampartSeverity(RampartInteger severity) {
        if (severity != null) {
            if (severity == newRampartInteger(0)
                    || severity == newRampartInteger(1)
                    || severity == newRampartInteger(2)
                    || severity == newRampartInteger(3)) {
                return RampartSeverity.LOW;
            } else if (severity == newRampartInteger(4)
                    || severity == newRampartInteger(5)
                    || severity == newRampartInteger(6)) {
                return RampartSeverity.MEDIUM;
            } else if (severity == newRampartInteger(7)
                    || severity == newRampartInteger(8)) {
                return RampartSeverity.HIGH;
            } else if (severity == newRampartInteger(9)
                    || severity == newRampartInteger(10)) {
                return RampartSeverity.VERY_HIGH;
            }
        }
        return RampartSeverity.UNKNOWN;
    }

    public static RampartVersion newRampartVersion(int major, int minor) {
        return RampartVersionImpl.valueOf(major, minor);
    }
}
