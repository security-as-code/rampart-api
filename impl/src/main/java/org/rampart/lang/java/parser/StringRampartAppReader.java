package org.rampart.lang.java.parser;

import org.rampart.lang.grammar.RampartLexer;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.antlr.v4.runtime.CommonTokenFactory;
import org.rampart.antlr.v4.runtime.CommonTokenStream;
import org.rampart.antlr.v4.runtime.DefaultErrorStrategy;
import org.rampart.antlr.v4.runtime.UnbufferedCharStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringRampartAppReader extends MultiRampartAppReader<BufferedReader> {

    private String content;

    /**
     * Creates a new StringRampartAppReader capable of parsing RAMPART apps from an in-memory representation.
     * This API uses whatever default String encoding scheme is set for the JVM.
     *
     * @param content with RAMPART apps ready to be parsed
     */
    public StringRampartAppReader(String content) {
        this.content = content;
    }

    @Override
    protected RampartBufferedParser getNextRampartParser() throws IOException {
        if (currentParser != null) {
            return currentParser;
        }
        if (content == null) {
            return null;
        }
        // Do not use StringReader here, as it has a bad implementation for `BufferedReader.ready()` that makes the
        // AppReader to go fetch for more bytes at the end of the stream
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
        BufferedReader reader = new BufferedReader(isr);
        skipWhitespaceAndComments(reader, content.length() + 1);
        currentParser = buildRampartParserForReader(reader);
        // clear out content as this has been incorporated into the parser
        content = null;
        return currentParser;
    }

    protected InvalidRampartAppException getExceptionForInvalidRampartApp(InvalidRampartAppException iaae) {
        return iaae;
    }

    private static RampartBufferedParser buildRampartParserForReader(BufferedReader reader) {
        RampartLexer lexer = new RampartLexer(new UnbufferedCharStream(reader));
        lexer.removeErrorListeners();
        InputStreamErrorListener errorListener = new InputStreamErrorListener();
        lexer.addErrorListener(errorListener);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        RampartBufferedParser parser = new RampartBufferedParser(reader, new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new DefaultErrorStrategy());
        return parser;
    }

}
