package org.rampart.lang.java.parser;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;

import org.rampart.lang.grammar.RampartLexer;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.antlr.v4.runtime.CommonTokenFactory;
import org.rampart.antlr.v4.runtime.CommonTokenStream;
import org.rampart.antlr.v4.runtime.DefaultErrorStrategy;
import org.rampart.antlr.v4.runtime.UnbufferedCharStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collection;
import java.util.LinkedList;

public class RampartSourceRampartAppReader extends MultiRampartAppReader<InputStreamBufferedReader> {

    private final LinkedList<RampartSource> sourceQueue;

    public RampartSourceRampartAppReader(Collection<RampartSource> rampartSources) {
        this.sourceQueue = new LinkedList<RampartSource>(rampartSources);
    }

    @Override
    protected RampartBufferedParser getNextRampartParser() throws IOException {
        if (currentParser != null) {
            return currentParser;
        }

        if (!sourceQueue.isEmpty()) {
            RampartSource rampartSource = sourceQueue.removeFirst();
            InputStream content = rampartSource.getInputStream();

            if (content == null) {
                return null;
            }

            InputStreamBufferedReader reader = new InputStreamBufferedReader(rampartSource, content);
            skipWhitespaceAndComments(reader, (int) rampartSource.getSize() + 1);
            currentParser = buildRampartParserForReader(reader);
        }
        return currentParser;
    }

    protected InvalidRampartAppException getExceptionForInvalidRampartApp(InvalidRampartAppException iaae) {
        String filename = currentParser.getBufferedReader().getFile().getAbsoluteFilePath();
        return new InvalidRampartAppException(
                newRampartString(filename),
                "in file \"" + filename + "\": " + iaae.getMessage(),
                iaae);
    }

    private static RampartBufferedParser buildRampartParserForReader(InputStreamBufferedReader reader) {
        RampartLexer lexer = new RampartLexer(new UnbufferedCharStream(reader));
        lexer.removeErrorListeners();
        RampartSourceErrorListener errorListener = new RampartSourceErrorListener(reader.getFile());
        lexer.addErrorListener(errorListener);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        RampartBufferedParser parser = new RampartBufferedParser(reader, new CommonTokenStream(lexer), newRampartString(reader.getFile().getAbsoluteFilePath()));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new DefaultErrorStrategy());
        return parser;
    }

}