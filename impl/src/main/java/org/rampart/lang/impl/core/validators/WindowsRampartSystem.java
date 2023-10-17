package org.rampart.lang.impl.core.validators;

/**
 * Based on java.io.WinNTFileSystem class
 */
public class WindowsRampartSystem extends RampartSystem {

    @Override
    public boolean isAbsolute(String path) {
        // Only \\foo and z:\foo are absolute, so length needs to be bigger than 1
        if (path == null || path.length() < 2) {
            return false;
        }
        return path.startsWith("\\\\")
                || (path.length() >= 3 && isDriveLetter(path.charAt(0)) && path.startsWith(":\\", 1));
    }

    @Override
    public String normalize(String path) {
        String normalizedPath = null;
        int n = path.length();
        char prev = 0;
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if (c == '/') {
                normalizedPath = normalize(path, n, (prev == getFileSeparator()) ? i - 1 : i);
                break;
            } else if ((c == getFileSeparator()) && (prev == getFileSeparator()) && (i > 1)) {
                normalizedPath = normalize(path, n, i - 1);
                break;
            } else if ((c == ':') && (i > 1)) {
                normalizedPath = normalize(path, n, 0);
                break;
            }
            prev = c;
        }
        if (normalizedPath == null && prev == getFileSeparator()) {
            normalizedPath = normalize(path, n, n - 1);
        }

        normalizedPath = (normalizedPath == null ? path : normalizedPath);
        if (!normalizedPath.equals(path)) {
            throw new IllegalArgumentException("only backslash separator is supported in path:");
        }
        return normalizedPath;
    }

    private static boolean isDriveLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }

    @Override
    public char getFileSeparator() {
        return '\\';
    }

    private String normalize(String path, int len, int off) {
        if (len == 0) {
            return path;
        }
        if (off < 3) {
            off = 0;   /* Avoid fencepost cases with UNC pathnames */
        }
        int src;
        StringBuffer sb = new StringBuffer(len);

        if (off == 0) {
            /* Complete normalization, including prefix */
            src = normalizePrefix(path, len, sb);
        } else {
            /* Partial normalization */
            src = off;
            sb.append(path.substring(0, off));
        }

        /* Remove redundant slashes from the remainder of the path, forcing all
           slashes into the preferred slash */
        while (src < len) {
            char c = path.charAt(src++);
            if (isSlash(c)) {
                while ((src < len) && isSlash(path.charAt(src)))
                    src++;
                if (src == len) {
                    /* Check for trailing separator */
                    int sn = sb.length();
                    if ((sn == 2) && (sb.charAt(1) == ':')) {
                        /* "z:\\" */
                        sb.append(getFileSeparator());
                        break;
                    }
                    if (sn == 0) {
                        /* "\\" */
                        sb.append(getFileSeparator());
                        break;
                    }
                    if ((sn == 1) && (isSlash(sb.charAt(0)))) {
                        /* "\\\\" is not collapsed to "\\" because "\\\\" marks
                           the beginning of a UNC pathname.  Even though it is
                           not, by itself, a valid UNC pathname, we leave it as
                           is in order to be consistent with the win32 APIs,
                           which treat this case as an invalid UNC pathname
                           rather than as an alias for the root directory of
                           the current drive. */
                        sb.append(getFileSeparator());
                        break;
                    }
                    /* Path does not denote a root directory, so do not append
                       trailing slash */
                    break;
                } else {
                    sb.append(getFileSeparator());
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private int normalizePrefix(String path, int len, StringBuffer sb) {
        int src = 0;
        while ((src < len) && isSlash(path.charAt(src))) {
            src++;
        }
        char c;
        if ((len - src >= 2) && isDriveLetter(c = path.charAt(src)) && path.charAt(src + 1) == ':') {
            /* Remove leading slashes if followed by drive specifier.
               This hack is necessary to support file URLs containing drive
               specifiers (e.g., "file://c:/path").  As a side effect,
               "/c:/path" can be used as an alternative to "c:/path". */
            sb.append(c);
            sb.append(':');
            src += 2;
        } else {
            src = 0;
            if ((len >= 2) && isSlash(path.charAt(0)) && isSlash(path.charAt(1))) {
                /* UNC pathname: Retain first slash; leave src pointed at
                   second slash so that further slashes will be collapsed
                   into the second slash.  The result will be a pathname
                   beginning with "\\\\" followed (most likely) by a host
                   name. */
                src = 1;
                sb.append(getFileSeparator());
            }
        }
        return src;
    }

    private static boolean isSlash(char c) {
        return (c == '\\') || (c == '/');
    }
}
