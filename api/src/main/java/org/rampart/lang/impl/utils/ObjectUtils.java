package org.rampart.lang.impl.utils;

public final class ObjectUtils {

    /**
     * Returns hashCode for argument, which could be {@code null}.
     * @param obj an object
     * @return hashcode for that object.
     */
    public static int hash(final Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    /**
     * Returns hashCode for argument, which could be {@code null}.
     * @param objects list of objects
     * @return hashcode for that object.
     */
    public static int hash(final Object... objects) {
        int hash = 1;
        if (objects != null) {
            for (final Object object : objects) {
                final int tmpHash = hash(object);
                hash = hash * 31 + tmpHash;
            }
        }
        return hash;
    }

    /**
     * Compares two objects for equality, where either one or both
     * objects may be {@code null}.
     *
     * <pre>
     * ObjectUtils.equals(null, null)                  = true
     * ObjectUtils.equals(null, "")                    = false
     * ObjectUtils.equals("", null)                    = false
     * ObjectUtils.equals("", "")                      = true
     * ObjectUtils.equals(Boolean.TRUE, null)          = false
     * ObjectUtils.equals(Boolean.TRUE, "true")        = false
     * ObjectUtils.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtils.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     *
     * @param left  he first object, may be {@code null}
     * @param right the second object, may be {@code null}
     * @return {@code true} if the values of both objects are the same
     */
    public static boolean equals(final Object left, final Object right) {
        return (left == right) || (left != null && left.equals(right));
    }

}
