package org.rampart.lang.api;

import org.rampart.lang.impl.utils.ObjectUtils;

public class RampartString implements RampartObject {
    private final String rampartString;

    { rampartString = toString(); }

    /* java/lang/String API wrapper functions */

    public RampartString substring(final RampartInteger beginIndex) {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.substring(Integer.parseInt(beginIndex.toString()));
            }
        };
    }

    public RampartString substring(final RampartInteger beginIndex, final RampartInteger endIndex) {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.substring(
                        Integer.parseInt(beginIndex.toString()),
                        Integer.parseInt(endIndex.toString()));
            }
        };
    }

    public RampartInteger indexOf(RampartInteger ch) {
        final int i = Integer.parseInt(ch.toString());
        return new RampartInteger() {
            @Override
            public String toString() {
                return String.valueOf(rampartString.indexOf(i));
            }
        };
    }

    public RampartInteger length() {
        return new RampartInteger() {
            @Override
            public String toString() {
                return String.valueOf(rampartString.length());
            }
        };
    }

    public RampartBoolean startsWith(RampartString prefix) {
        return rampartString.startsWith(prefix.toString()) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartBoolean endsWith(RampartString suffix) {
        return rampartString.endsWith(suffix.rampartString) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartString toLowerCase() {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.toLowerCase();
            }
        };
    }

    public RampartString toUpperCase() {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.toUpperCase();
            }
        };
    }

    public RampartString trim() {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.trim();
            }
        };
    }

    public RampartBoolean contains(RampartString substring) {
        return rampartString.contains(substring.rampartString) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartBoolean equalsIgnoreCase(RampartString other) {
        return rampartString.equalsIgnoreCase(other.rampartString) ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    public RampartString replace(final RampartString target, final RampartString replacement) {
        return new RampartString() {
            @Override
            public String toString() {
                return rampartString.replace(target.rampartString, replacement.rampartString);
            }
        };
    }

    public RampartBoolean isEmpty() {
        return rampartString.length() == 0 ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    /**
     * Use this method when serializing an RampartString, it deals with proper escaping and formatting.
     *
     * @return a newRampartString that is suitable for serializing
     */
    public RampartString formatted() {
        return new RampartString() {
            @Override
            public String toString() {
                return "\""
                        // Need to escape all escaped double-quote characters and backslash characters
                        + rampartString
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                        + "\"";
            }
        };
    }

    @Override
    public int hashCode() {
        return rampartString.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartString)) {
            return false;
        }
        RampartString otherString = (RampartString) other;
        return ObjectUtils.equals(rampartString, otherString.rampartString);
    }
}
