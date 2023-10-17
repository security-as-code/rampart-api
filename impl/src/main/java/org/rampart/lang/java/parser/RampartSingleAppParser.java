package org.rampart.lang.java.parser;

import org.rampart.lang.grammar.RampartParser;
import org.rampart.antlr.v4.runtime.RecognitionException;
import org.rampart.antlr.v4.runtime.Token;
import org.rampart.antlr.v4.runtime.TokenStream;

class RampartSingleAppParser extends RampartParser {

    RampartSingleAppParser(TokenStream input) {
        super(input);
    }

    @Override
    public Token match(int ttype) throws RecognitionException {
        if (ttype == RampartParser.ENDAPP) {
            return getCurrentToken();
        }
        return super.match(ttype);
    }
}
