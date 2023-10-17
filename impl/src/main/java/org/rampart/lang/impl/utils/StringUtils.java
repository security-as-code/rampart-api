package org.rampart.lang.impl.utils;

public class StringUtils {

    /**
     * Method based on apache commons implementation to tell if a String is only comprised of whitespace.
     * If there are any characters than whitespace this method will return true. Null references and empty Strings
     * will return true.
     * @param value
     * @return
     */
    public static boolean isBlank(CharSequence value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
