package org.rampart.lang.api;

import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.utils.ObjectUtils;

import java.util.*;

/**
 * WARNING: This class MUST be immutable or it will affect the hashcode computation
 */
public abstract class RampartList implements RampartObject {
    private final ArrayList<RampartObject> rampartObjects;
    private final int hashCode;
    private final String toStringValue;

    {
        this.rampartObjects = new ArrayList<RampartObject>();
        RampartObjectIterator it = getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            this.rampartObjects.add(it.next());
        }
        this.hashCode = rampartObjects.hashCode();
        this.toStringValue = createToStringValue();
    }

    public static final RampartList EMPTY = new RampartList() {
        @Override
        public RampartObjectIterator getObjectIterator() {
            return new RampartObjectIterator() {
                public RampartBoolean hasNext() {
                    return RampartBoolean.FALSE;
                }

                public RampartObject next() {
                    throw new UnsupportedOperationException("this is an empty RampartList");
                }
            };
        }
    };

    public abstract RampartObjectIterator getObjectIterator();

    public RampartObject getFirst() {
        if (rampartObjects.isEmpty()) {
            return null;
        }
        return rampartObjects.get(0);
    }

    public RampartObject getLast() {
        if (rampartObjects.isEmpty()) {
            return null;
        }
        return rampartObjects.get(rampartObjects.size() - 1);
    }

    public RampartInteger size() {
        return new RampartInteger() {
            @Override
            public String toString() {
                return String.valueOf(rampartObjects.size());
            }
        };
    }

    public RampartList addAll(RampartList other) {
        final ArrayList<RampartObject> newArray = new ArrayList<RampartObject>(rampartObjects);
        newArray.addAll(other.rampartObjects);
        return new RampartList() {
            public RampartObjectIterator getObjectIterator() {
                return new RampartObjectIterator() {
                    private int cursor = 0;

                    // @Override
                    public RampartBoolean hasNext() {
                        return cursor != newArray.size() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
                    }

                    // @Override
                    public RampartObject next() {
                        if (cursor >= newArray.size()) {
                            throw new NoSuchElementException();
                        }
                        return newArray.get(cursor++);
                    }
                };
            }
        };
    }

    public RampartBoolean contains(RampartObject other) {
        for (RampartObject obj : rampartObjects) {
            if (obj == other || obj.equals(other)) {
                return RampartBoolean.TRUE;
            }
        }
        return RampartBoolean.FALSE;
    }

    /**
     * Checks if each one of the RampartObjects in `list` are present in the current RampartList (this).
     *
     * @param list to check each element against this RampartList
     * @return RampartBoolean.TRUE if all concealed RampartObjects of `list` are present in this RampartList,
     *         RampartBoolean.FALSE otherwise
     */
    public RampartBoolean containsAll(RampartList list) {
        if (list.rampartObjects.isEmpty() || rampartObjects.isEmpty()) {
            return RampartBoolean.FALSE;
        }
        for (RampartObject obj : list.rampartObjects) {
            if (!rampartObjects.contains(obj)) {
                return RampartBoolean.FALSE;
            }
        }
        return RampartBoolean.TRUE;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof RampartList)) {
            return false;
        }
        RampartList otherList = (RampartList) other;
        return ObjectUtils.equals(rampartObjects, otherList.rampartObjects);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public RampartBoolean isEmpty() {
        return rampartObjects.isEmpty() ? RampartBoolean.TRUE : RampartBoolean.FALSE;
    }

    private String createToStringValue() {
        StringBuilder builder = new StringBuilder("[");
        if (rampartObjects.isEmpty()) {
            return builder.append(']').toString();
        }
        String delimiter = "";
        for (RampartObject value : rampartObjects) {
            if (value instanceof RampartString) {
                value = ((RampartString) value).formatted();
            }
            builder.append(delimiter).append(value);
            delimiter = ", ";
        }
        return builder.append(']').toString();
    }
}
