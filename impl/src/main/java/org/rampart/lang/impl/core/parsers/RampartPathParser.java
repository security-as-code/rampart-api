package org.rampart.lang.impl.core.parsers;

import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.toJavaInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.RampartSystem;
import org.rampart.lang.impl.core.validators.UnixRampartSystem;
import org.rampart.lang.impl.core.validators.WindowsRampartSystem;

/**
 * Parser for paths in RAMPART which may contain wildcards.
 *
 * Eg.
 *   "/etc/shadow", "/etc/passwd", "/bin/*"
 */
public final class RampartPathParser {
    private static final String WILDCARD = "*";

    // Can't normalize or canonicalize a Windows path containing a wildcard, so replace
    // it with something relatively unique. We can do this indiscriminately, as it will
    // work for either Linux or Windows, and saves on multiple windows checks.
    private static final String WINDOWS_WILDCARD = "__RAMPART_" + System.nanoTime() + "_WILDCARD__";

    public static RampartList parsePaths(
            final Map<String, RampartList> visitorSymbolTable,
            RampartConstant pathKey,
            RampartList targetOSList) throws InvalidRampartRuleException {
        final RampartList pathList = visitorSymbolTable.get(pathKey.toString());
        final RampartSystem rampartSystem = findFileSystem(targetOSList);
        final List<RampartString> validatedPathList = parsePathListEntries(pathKey, rampartSystem, pathList);
        return newRampartList(validatedPathList.toArray(new RampartString[validatedPathList.size()]));
    }


    private static List<RampartString> parsePathListEntries(RampartConstant pathKey, RampartSystem rampartSystem, RampartList pathList)
            throws InvalidRampartRuleException {
        if (pathList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException("\"" + pathKey + "\" declaration must not be empty");
        }
        List<RampartString> validatedPathList = new ArrayList<RampartString>();
        RampartObjectIterator it = pathList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validatedPathList.add(parsePathListEntry(pathKey, rampartSystem, it.next()));
        }
        return validatedPathList;
    }


    private static RampartString parsePathListEntry(RampartConstant pathKey, RampartSystem rampartSystem, RampartObject entry)
            throws InvalidRampartRuleException {
        if (!(entry instanceof RampartString)) {
            throw new InvalidRampartRuleException("all entries in \"" + pathKey + "\" declaration must be quoted strings");
        }
        RampartString entryString = (RampartString) entry;
        if (toJavaInt(entryString.length()) == 0) {
            throw new InvalidRampartRuleException("path entry in \"" + pathKey + "\" declaration must not be empty");
        }
        return parsePathParameter(rampartSystem, entryString.toString());
    }


    // W4J validation logic as used by legacy file rule.
    private static RampartString parsePathParameter(RampartSystem rampartSystem, String pathParameter)
            throws InvalidRampartRuleException {
        if (rampartSystem == null) {
            // Could not figure out the filesystem for which this RAMPART rule is applied to, validating paths is pointless
            return newRampartString(pathParameter);
        }

        ensurePathContainsAtMostOneWildcard(pathParameter);

        // Can't normalize or canonicalize a Windows path containing a wildcard, so replace
        // it with something relatively unique. We can do this indiscriminately, as it will
        // work for either Linux or Windows, and saves on multiple windows checks.
        String path = pathParameter.replace(WILDCARD, WINDOWS_WILDCARD);

        // clean up path - get rid of duplicate slashes, and trailing slashes.
        // or, in the case of windows, make all slashes backslashes.
        String filepath;
        try {
            filepath = rampartSystem.normalize(path);
        } catch (IllegalArgumentException e) {
            throw new InvalidRampartRuleException(e.getMessage() + " `" + pathParameter + "`");
        }

        ensurePathIsNormalized(rampartSystem, filepath);
        return newRampartString(filepath.replace(WINDOWS_WILDCARD, WILDCARD));
    }


    /**
     * Ensures path only contains, at most, one wildcard character.
     * This is all that is supported by the file rule backend.
     * @param filePath the path to be validated
     * @throws InvalidRampartRuleException if the path contains more than 1 wildcard character
     */
    private static void ensurePathContainsAtMostOneWildcard(String filePath) throws InvalidRampartRuleException {
        int wildcardIndex = filePath.indexOf(WILDCARD);
        if (wildcardIndex != -1 && wildcardIndex != filePath.lastIndexOf(WILDCARD)) {
            throw new InvalidRampartRuleException(
                    "file path: \"" + filePath + "\" contains more than one wildcard character");
        }
    }


    /**
     * Ensures path contains at most one dot character which is used to separate
     * the filename from its extension.
     * @param filePath path to be tested
     * @throws InvalidRampartRuleException
     *  when file path contains an illegal character sequence or the path is relative
     */
    private static void ensurePathIsNormalized(RampartSystem rampartSystem, String filePath) throws InvalidRampartRuleException {
        String fileSeparator = String.valueOf(rampartSystem.getFileSeparator());
        String relativePathStart = "." + fileSeparator;
        if (filePath.contains(fileSeparator)) {
            if (rampartSystem.isAbsolute(filePath)) {
                if (filePath.contains(relativePathStart)) {
                    throw new InvalidRampartRuleException(
                            "filepath: \"" + filePath + "\" contains an illegal character sequence: \""
                                    + relativePathStart + "\"");
                }
            } else {
                throw new InvalidRampartRuleException("cannot be a relative path: \"" + filePath + "\"");
            }
        }
    }


    /** Finds file system that is applicable to the given OS list. */
    public static RampartSystem findFileSystem(RampartList targetOSList) {
        if (targetOSList.contains(RampartGeneralConstants.ANY_KEY) == RampartBoolean.FALSE) {
            if (targetOSList.contains(RampartGeneralConstants.WINDOWS_KEY) == RampartBoolean.TRUE
                    && toJavaInt(targetOSList.size()) == 1) {
                return new WindowsRampartSystem();
            } else if (targetOSList.contains(RampartGeneralConstants.WINDOWS_KEY) == RampartBoolean.FALSE) {
                return new UnixRampartSystem();
            }
        }
        return null;
    }
}
