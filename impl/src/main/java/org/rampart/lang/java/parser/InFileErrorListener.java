package org.rampart.lang.java.parser;

import org.rampart.lang.java.InvalidRampartSyntaxError;
import org.rampart.antlr.v4.runtime.BaseErrorListener;
import org.rampart.antlr.v4.runtime.RecognitionException;
import org.rampart.antlr.v4.runtime.Recognizer;

import java.io.File;

/**
 * Class to customize how ANTLR behaves when it comes across a syntax error when parsing.
 */
public class InFileErrorListener extends BaseErrorListener {

    private final File file;
    InFileErrorListener(File file) {
        this.file = file;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        throw new InvalidRampartSyntaxError("in file \"" + file + "\" at line " + line + ", "
                // In ANTLR column number starts at 0, so increment
                + "col " + ++charPositionInLine + ": " + msg, file.getAbsolutePath());
    }
}
