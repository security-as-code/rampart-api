package org.rampart.lang.java.parser;

import org.rampart.lang.api.RampartString;
import org.rampart.lang.grammar.RampartParser;
import org.rampart.antlr.v4.runtime.TokenStream;

import java.io.BufferedReader;

public class RampartBufferedParser<T extends BufferedReader> extends RampartParser {
    private final T reader;
    private RampartString fileName;

    RampartBufferedParser(T reader, TokenStream input) {
        super(input);
        this.reader = reader;
    }

    RampartBufferedParser(T reader, TokenStream input, RampartString fileName) {
        super(input);
        this.reader = reader;
        this.fileName = fileName;
    }

    public T getBufferedReader() {
        return reader;
    }

    public RampartString getFileName() {
        return fileName;
    }
}
