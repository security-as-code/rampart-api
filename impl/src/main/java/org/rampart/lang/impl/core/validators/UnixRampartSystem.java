package org.rampart.lang.impl.core.validators;

/**
 * Based on java.io.UnixFileSystem class
 */
public class UnixRampartSystem extends RampartSystem {

    @Override
    public boolean isAbsolute(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        return path.charAt(0) == '/';
    }

    @Override
    public String normalize(String path) {
        String normalizedPath = null;
        int n = path.length();
        char prevChar = 0;
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if ((prevChar == '/') && (c == '/')) {
                normalizedPath = normalize(path, n, i - 1);
                break;
            }
            prevChar = c;
        }
        if (normalizedPath == null && prevChar == '/') {
            normalizedPath = normalize(path, n, n - 1);
        }

        normalizedPath = (normalizedPath == null ? path : normalizedPath);
        if (!normalizedPath.equals(path)) {
            throw new IllegalArgumentException("double or trailing slash present in the path:");
        }
        return normalizedPath;
    }

    @Override
    public char getFileSeparator() {
        return '/';
    }

    private static String normalize(String pathname, int len, int off) {
        if (len == 0) {
            return pathname;
        }
        int n = len;
        while ((n > 0) && (pathname.charAt(n - 1) == '/')) {
            n--;
        }
        if (n == 0) {
            return "/";
        }
        StringBuilder sb = new StringBuilder(pathname.length());
        if (off > 0) {
            sb.append(pathname, 0, off);
        }
        char prevChar = 0;
        for (int i = off; i < n; i++) {
            char c = pathname.charAt(i);
            if ((prevChar == '/') && (c == '/')) {
                continue;
            }
            sb.append(c);
            prevChar = c;
        }
        return sb.toString();
    }
}
