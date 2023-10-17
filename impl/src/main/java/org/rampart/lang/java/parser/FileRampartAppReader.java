package org.rampart.lang.java.parser;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import org.rampart.lang.grammar.RampartLexer;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.antlr.v4.runtime.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class FileRampartAppReader extends MultiRampartAppReader<FileBufferedReader> {
    /**
     * Queue of files to read RAMPART apps from.
     */
    private final LinkedList<File> fileQueue;

    public FileRampartAppReader(Collection<File> files) {
        this.fileQueue = new LinkedList<File>(files);
    }

    protected RampartBufferedParser getNextRampartParser() throws IOException {
        if (currentParser != null) {
            return currentParser;
        }
        if (!fileQueue.isEmpty()) {
            File file = fileQueue.removeFirst();
            FileBufferedReader reader = new FileBufferedReader(file);
            skipWhitespaceAndComments(reader, (int) file.length());
            currentParser = buildRampartParserForReader(reader);
        }
        return currentParser;
    }

    protected InvalidRampartAppException getExceptionForInvalidRampartApp(InvalidRampartAppException iaae) {
        String filename = currentParser.getBufferedReader().getFile().getAbsolutePath();
        return new InvalidRampartAppException(
                newRampartString(filename),
                "in file \"" + filename + "\": " + iaae.getMessage(),
                iaae);
    }

    private static RampartBufferedParser<FileBufferedReader> buildRampartParserForReader(FileBufferedReader reader) {
        RampartLexer lexer = new RampartLexer(new UnbufferedCharStream(reader));
        lexer.removeErrorListeners();
        InFileErrorListener errorListener = new InFileErrorListener(reader.getFile());
        lexer.addErrorListener(errorListener);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        RampartBufferedParser<FileBufferedReader> parser = new RampartBufferedParser<FileBufferedReader>(reader, new CommonTokenStream(lexer),
                newRampartString(reader.getFile().getAbsolutePath()));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new DefaultErrorStrategy());
        return parser;
    }

}