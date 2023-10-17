package org.rampart.lang.impl.http.validators.v2.utils;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.utils.UriUtils;

public class RampartHttpValidatorUtils {

    public static boolean isValidListOfNonEmptyHosts(RampartObject obj) {
        if (!(obj instanceof RampartList)
                || ((RampartList) obj).isEmpty() == RampartBoolean.TRUE) {
            return false;
        }
        RampartObjectIterator it = ((RampartList) obj).getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject entry = it.next();
            if (!(entry instanceof RampartString)
                    || !UriUtils.isValidHost(entry.toString())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidListOfNonEmptyUris(RampartObject obj) {
        if (!(obj instanceof RampartList)
                || ((RampartList) obj).isEmpty() == RampartBoolean.TRUE) {
            return false;
        }
        RampartObjectIterator it = ((RampartList) obj).getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject entry = it.next();
            if (!(entry instanceof RampartString)
                    || !UriUtils.isValidUriPath(entry.toString())) {
                return false;
            }
        }
        return true;
    }

}
