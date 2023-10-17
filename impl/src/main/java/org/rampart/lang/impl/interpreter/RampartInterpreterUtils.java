package org.rampart.lang.impl.interpreter;

import org.rampart.lang.api.*;
import org.rampart.lang.api.core.RampartObjectIterator;

/**
 * Utility class used to search and manipulate RampartObjects
 */
public class RampartInterpreterUtils {

    private RampartInterpreterUtils() {}

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartConstant
     *
     * @param rampartObjectsList List of rampartObjects to be searched
     * @return first RampartConstant in array, null if no RampartConstant is present
     */
    public static RampartConstant findFirstRampartConstant(RampartList rampartObjectsList) {
        if (rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject rampartObject = it.next();
            if (rampartObject instanceof RampartList) {
                RampartConstant candidate = findFirstRampartConstant(((RampartList) rampartObject));
                if (candidate != null) {
                    return candidate;
                }
            }
            if (rampartObject instanceof RampartConstant) {
                return (RampartConstant) rampartObject;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartConstant in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first RampartConstant within this RampartObject, null if no RampartConstant is present
     */
    public static RampartConstant findFirstRampartConstant(RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findFirstRampartConstant(((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartConstant) {
            return (RampartConstant) rampartObject;
        }
        return null;
    }

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartString
     *
     * @param rampartObjectsList List of rampartObjects to be searched
     * @return first RampartString in array, null if no RampartString is present
     */
    public static RampartString findFirstRampartString(RampartList rampartObjectsList) {
        if (rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartString found = findFirstRampartString(it.next());
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartString in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first RampartString within this RampartObject, null if no RampartString is present
     */
    public static RampartString findFirstRampartString(RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findFirstRampartString(((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartString) {
            return (RampartString) rampartObject;
        }
        return null;
    }

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartObject value of the RampartNamedValue
     * with the given key
     *
     * @param key             key of desired RampartNamedValue
     * @param rampartObjectsList List of RampartObjects to be searched
     * @return first value of the RampartNamedValue matching the key in the list, null if it is not present
     */
    public static RampartObject findRampartNamedValue(RampartConstant key, RampartList rampartObjectsList) {
        if (key == null || rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject found = findRampartNamedValue(key, it.next());
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartNamedValue in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first value of the RampartNamedValue with the provided key within this RampartObject, null if the
     * RampartNamedValue is not present
     */
    public static RampartObject findRampartNamedValue(RampartConstant key, RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findRampartNamedValue(key, ((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartNamedValue) {
            if (((RampartNamedValue) rampartObject).getName().equals(key)) {
                return (((RampartNamedValue) rampartObject).getRampartObject());
            }
        }
        return null;
    }

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartInteger
     * @param rampartObjectsList List of rampartObjects to be searched
     * @return first RampartInteger in array, null if no RampartInteger is present
     */
    public static RampartInteger findFirstRampartInteger(RampartList rampartObjectsList) {
        if (rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject rampartObject = it.next();
            if (rampartObject instanceof RampartList) {
                RampartInteger candidate = findFirstRampartInteger(((RampartList) rampartObject));
                if (candidate != null) {
                    return candidate;
                }
            }
            if (rampartObject instanceof RampartInteger) {
                return (RampartInteger) rampartObject;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartInteger in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first RampartInteger within this RampartObject, null if no RampartInteger is present
     */
    public static RampartInteger findFirstRampartInteger(RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findFirstRampartInteger(((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartInteger) {
            return (RampartInteger) rampartObject;
        }
        return null;
    }

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartBoolean
     * @param rampartObjectsList List of rampartObjects to be searched
     * @return first RampartBoolean in array, null if no RampartBoolean is present
     */
    public static RampartBoolean findFirstRampartBoolean(RampartList rampartObjectsList) {
        if (rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject rampartObject = it.next();
            if (rampartObject instanceof RampartList) {
                RampartBoolean candidate = findFirstRampartBoolean(((RampartList) rampartObject));
                if (candidate != null) {
                    return candidate;
                }
            }
            if (rampartObject instanceof RampartBoolean) {
                return (RampartBoolean) rampartObject;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartBoolean in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first RampartBoolean within this RampartObject, null if no RampartBoolean is present
     */
    public static RampartBoolean findFirstRampartBoolean(RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findFirstRampartBoolean(((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartBoolean) {
            return (RampartBoolean) rampartObject;
        }
        return null;
    }

    /**
     * Iterates through the list of RampartObjects to find and return the first RampartFloat
     * @param rampartObjectsList List of rampartObjects to be searched
     * @return first RampartFloat in array, null if no RampartFloat is present
     */
    public static RampartFloat findFirstRampartFloat(RampartList rampartObjectsList) {
        if (rampartObjectsList == null) {
            return null;
        }
        RampartObjectIterator it = rampartObjectsList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject rampartObject = it.next();
            if (rampartObject instanceof RampartList) {
                RampartFloat candidate = findFirstRampartFloat(((RampartList) rampartObject));
                if (candidate != null) {
                    return candidate;
                }
            }
            if (rampartObject instanceof RampartFloat) {
                return (RampartFloat) rampartObject;
            }
        }
        return null;
    }

    /**
     * Searches for the first RampartFloat in this RampartObject
     *
     * @param rampartObject to be searched
     * @return first RampartFloat within this RampartObject, null if no RampartFloat is present
     */
    public static RampartFloat findFirstRampartFloat(RampartObject rampartObject) {
        if (rampartObject instanceof RampartList) {
            return findFirstRampartFloat(((RampartList) rampartObject));
        }
        if (rampartObject instanceof RampartFloat) {
            return (RampartFloat) rampartObject;
        }
        return null;
    }
}
