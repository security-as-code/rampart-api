package org.rampart.lang.java.parser;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.grammar.RampartLexer;
import org.rampart.lang.grammar.RampartParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.antlr.v4.runtime.CommonTokenFactory;
import org.rampart.antlr.v4.runtime.CommonTokenStream;
import org.rampart.antlr.v4.runtime.UnbufferedCharStream;

/**
 * Class to abstract the Rampart lexing, parsing, visiting and validating process.
 *
 * This class should not be used anymore as it's marked for removal since RAMPART 2.3. Please use
 * @see MultiRampartAppReader instead.
 */
@Deprecated
public class RampartAppReader {
    private final HashMap<RampartString, RampartApp> rampartApps;
    private final BufferedReader reader;
    private final int maximumAppLength;
    // this is the app that is being parsed, if null it means that an app hasn't finished parsing. Useful to know when
    // to skip ahead syntactically invalid RAMPART apps.
    private RampartApp latestParsedApp;

    public RampartAppReader(BufferedReader reader, int maximumAppLength) {
        this.rampartApps = new HashMap<RampartString, RampartApp>();
        this.reader = reader;
        this.maximumAppLength = maximumAppLength;
    }

    /**
     * Reads one `app` from the reader
     *
     * @return the read RampartApp from the reader
     * @throws InvalidRampartAppException in the case this is an invalid `app` or there's an invalid
     *         `rule` inside the `app`
     * @throws IOException if there's something wrong with the reader's marker
     */
    public RampartApp readApp() throws InvalidRampartAppException, IOException {
        latestParsedApp = null;
        reader.mark(maximumAppLength);
        RampartLexer lexer = new RampartLexer(new UnbufferedCharStream(reader));
        InputStreamErrorListener errorListener = new InputStreamErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        RampartParser parser = new RampartSingleAppParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        latestParsedApp = new RampartSingleAppVisitor().visitApp(parser.app());
        checkForDuplicateAppName();
        rampartApps.put(latestParsedApp.getAppName(), latestParsedApp);
        return latestParsedApp;
    }

    /**
     * Resets the reader to the start of the app previously set in `readApp` and skips the until it
     * finds the `endapp` token.
     * 
     * @throws IOException if there's something wrong with the marker or if an I/O error has occurred
     */
    public void skipUntilEndApp() throws IOException {
        if (latestParsedApp != null) {
            latestParsedApp = null;
            // We have already progressed the marker beyond the end of latestParsedApp's definition
            // in the rules file by successfully parsing it. No need to skip again.
            return;
        }
        reader.reset();
        String line;
        String endApp = RampartParser.VOCABULARY.getLiteralName(RampartParser.ENDAPP);
        while ((line = reader.readLine()) != null) {
            if (line.trim().equalsIgnoreCase(endApp)) {
                break;
            }
        }
    }

    /**
     * Checks if we have previously parsed an RampartApp from the rules file which has the same name
     * as the `latestParsedApp`
     * @throws InvalidRampartAppException when the `latestParsedApp` has the same name as a previously
     * parsed app
     */
    private void checkForDuplicateAppName() throws InvalidRampartAppException {
        if (latestParsedApp != null
                && rampartApps.containsKey(latestParsedApp.getAppName())
                && latestParsedApp.getAppVersion().equals(
                        rampartApps.get(latestParsedApp.getAppName()).getAppVersion())) {
            throw new InvalidRampartAppException(
                    "duplicate app \"" + latestParsedApp.getAppName() + "\" detected of same version " + latestParsedApp.getAppVersion());
        }
    }
}
