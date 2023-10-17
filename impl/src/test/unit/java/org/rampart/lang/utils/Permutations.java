package org.rampart.lang.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Permutation generators. */
public final class Permutations {
    /** A pair of elements of the same type. */
    public static final class Pair<T> {
        /** First element in the pair. */
        public final T first;

        /** Second element in the pair. */
        public final T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }
    }


    private Permutations() {
        throw new UnsupportedOperationException();
    }


    /**
     * Returns an iterator over all pairs that consists of the
     * distinct elements of the <code>from</code> array. In other
     * words, it returns all pairs <code>(from[i], from[j])</code>
     * where <code>i != j</code>.
     */
    public static <T> Iterable<Pair<T>> distinctPairs(final T[] from) {
        return new Iterable<Permutations.Pair<T>>() {
            @Override
            public Iterator<Pair<T>> iterator() {
                return new Iterator<Permutations.Pair<T>>() {
                    private int firstIdx = 0;
                    private int secondIdx = 1;

                    @Override
                    public boolean hasNext() {
                        /* Rely on the second index to avoid complex math. */
                        return secondIdx < from.length;
                    }

                    @Override
                    public Pair<T> next() {
                        if (secondIdx >= from.length) {
                            throw new NoSuchElementException();
                        }

                        final Pair<T> ret = new Pair<T>(from[firstIdx], from[secondIdx++]);
                        if (secondIdx >= from.length) {
                            firstIdx += 1;
                            secondIdx = firstIdx + 1;
                        }

                        return ret;
                    }
                };
            }
        };
    }
}
