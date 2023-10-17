package org.rampart.lang.java.parser;

import org.rampart.lang.java.InvalidRampartSyntaxError;
import org.rampart.antlr.v4.runtime.BaseErrorListener;
import org.rampart.antlr.v4.runtime.RecognitionException;
import org.rampart.antlr.v4.runtime.Recognizer;

public class RampartSourceErrorListener extends BaseErrorListener {

    private final RampartSource rampartSource;
    RampartSourceErrorListener(RampartSource rampartSource) {
        this.rampartSource = rampartSource;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        throw new InvalidRampartSyntaxError("in file \"" + rampartSource.getAbsoluteFilePath() + "\" at line " + line + ", "
                // In ANTLR column number starts at 0, so increment
                + "col " + ++charPositionInLine + ": " + msg, rampartSource.getAbsoluteFilePath());
    }
}