package org.rampart.lang.java.parser;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.grammar.RampartParser;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.InvalidRampartSyntaxError;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public abstract class MultiRampartAppReader<T extends BufferedReader> {
    private final String HASH_COMMENT = "#";
    private final String DOUBLE_SLASH_COMMENT = "//";

    /**
     * Parser currently being used to parse RAMPART apps.
     */
    protected RampartBufferedParser<T> currentParser;

    /**
     * Map that stores all the apps parsed by this app reader. The keys are the app names
     */
    private final HashMap<RampartString, RampartApp> appList = new HashMap<RampartString, RampartApp>();

    /**
     * Returns the full set of valid RAMPART apps every time readApps is invoked
     * @throws InvalidRampartAppException
     * @throws IOException
     * @return collection of RampartApps
     */
    public Collection<RampartApp> readApps() throws InvalidRampartAppException, IOException {
        while ((currentParser = getNextRampartParser()) != null) {
            if (!currentParser.getBufferedReader().ready()) {
                currentParser = null;
                continue;
            }
            parseRampartApps();
        }
        return appList.values();
    }

    private void parseRampartApps() throws InvalidRampartAppException, IOException {
        while (currentParser.getBufferedReader().ready()) {
            try {
                RampartApp app = new RampartSingleAppVisitor().visitApp(currentParser.app());
                checkForDuplicateAppName(app);
                checkForOverriddenAppVersions(app);
                appList.put(app.getAppName(), app);
            } catch (InvalidRampartSyntaxError iase) {
                String endApp =  RampartParser.VOCABULARY.getLiteralName(RampartParser.ENDAPP);
                String line;
                while ((line = currentParser.getBufferedReader().readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(endApp)) {
                        break;
                    }
                }
                throw iase;
            } catch (InvalidRampartAppException iaae) {
                throw getExceptionForInvalidRampartApp(iaae);
            }
        }
        try {
            currentParser.getBufferedReader().close();
        } catch (Throwable ignore) {}
        currentParser = null;
    }

    private void checkForOverriddenAppVersions(RampartApp lastParsedApp) {
        RampartApp matchingApp = appList.get(lastParsedApp.getAppName());
        if (matchingApp != null && !matchingApp.getAppVersion().equals(lastParsedApp.getAppVersion())) {
            RampartApp keepApp =
                    matchingApp.getAppVersion().isLessThan(lastParsedApp.getAppVersion()) == RampartBoolean.TRUE
                            ? lastParsedApp : matchingApp;
            appList.put(keepApp.getAppName(), keepApp);
            RampartApp overridden = (keepApp != lastParsedApp ? lastParsedApp : matchingApp);
            throw new InvalidRampartAppException("RAMPART mod \"" + overridden.getAppName() + "\" overridden by mod with version \"" + keepApp
                            .getAppVersion() + "\"");
        }
    }

    private void checkForDuplicateAppName(RampartApp lastParsedApp) throws InvalidRampartAppException {
        RampartApp matchingApp = appList.get(lastParsedApp.getAppName());
        if (matchingApp != null && lastParsedApp.getAppVersion().equals(matchingApp.getAppVersion())) {
            throw new InvalidRampartAppException("duplicate mod \"" + lastParsedApp.getAppName() + "\"");
        }
    }

    protected abstract RampartBufferedParser getNextRampartParser() throws IOException;

    protected abstract InvalidRampartAppException getExceptionForInvalidRampartApp(InvalidRampartAppException iaae);

    /**
     * Skips comments ('#', '//') and whitespace until the end of the file. If a valid character is encountered the
     * reader is reset and control is passed on to ANTLR to accurately report on errors if any.
     *
     * @param reader         with data to read from
     * @param skipAheadLimit limit of bytes that the reader can skip and reset back the stream
     * @throws IOException if an I/O issue occurs
     */
    public void skipWhitespaceAndComments(BufferedReader reader, int skipAheadLimit) throws IOException {
        reader.mark(skipAheadLimit);
        if (reader.ready()) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() != 0
                        && !line.startsWith(HASH_COMMENT)
                        && !line.startsWith(DOUBLE_SLASH_COMMENT)) {
                    reader.reset();
                    return;
                }
            }
        }
    }
}