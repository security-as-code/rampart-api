package org.rampart.lang.impl.filesystem.parsers.v2;

import static org.rampart.lang.api.constants.RampartFileSystemConstants.*;

import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class RampartTraversalParserTest {
    @Test
    public void noTraversalDeclaration() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        RampartTraversalParser.parseTraversalOptions(symbolTable);
        assertThat(RampartTraversalParser.parseTraversalOptions(symbolTable), is(empty()));
    }

    @Test
    public void noKeys() throws InvalidRampartRuleException {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        assertThat(RampartTraversalParser.parseTraversalOptions(symbolTable), is(empty()));
    }

    @Test
    public void emptyTraversalDeclaration() throws InvalidRampartRuleException {
        assertThat(RampartTraversalParser.parseTraversalOptions(syms()),
                containsInAnyOrder(RELATIVE_KEY, ABSOLUTE_KEY));
    }

    @Test
    public void relativeTraversal() throws InvalidRampartRuleException {
        assertThat(RampartTraversalParser.parseTraversalOptions(syms(RELATIVE_KEY)),
                contains(RELATIVE_KEY));
    }

    @Test
    public void absoluteTraversal() throws InvalidRampartRuleException {
        assertThat(RampartTraversalParser.parseTraversalOptions(syms(ABSOLUTE_KEY)), contains(ABSOLUTE_KEY));
    }

    @Test
    public void relativeAndAbsoluteTraversalDeclared() {
        final String message = failOn(ABSOLUTE_KEY, RELATIVE_KEY);
        assertThat(message, equalTo("traversal declaration for RAMPART  \"" + FILESYSTEM_KEY
                + "\" rule can only contain the \"" + ABSOLUTE_KEY + "\" or the \"" + RELATIVE_KEY + "\" constants"));
    }

    @Test
    public void invalidTraversalParameters() {
        final String message = failOn(newRampartConstant("miscelaneous"), newRampartConstant("foreign"));
        assertThat(message, equalTo("unrecognized constant \"miscelaneous\" parameter for \""
                + TRAVERSAL_KEY + "\" in \"" + FILESYSTEM_KEY + "\" RAMPART rule"));
    }

    @Test
    public void mixedValidInvalidTraversalParameters() {
        final String message = failOn(ABSOLUTE_KEY, newRampartConstant("foreign"));
        assertThat(message, equalTo("unrecognized constant \"foreign\" parameter for \"" + TRAVERSAL_KEY
                + "\" in \"" + FILESYSTEM_KEY + "\" RAMPART rule"));
    }

    @Test
    public void invalidTraversalType() {
        final String message = failOn(newRampartString("relative"));
        assertThat(message,
                equalTo("\"" + TRAVERSAL_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void invalidTraversalFormat() {
        final String message = failOn(newRampartInteger(1));
        assertThat(message,
                equalTo("\"" + TRAVERSAL_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void invalidTraversalFormatMixedValidTraversal() {
        final String message = failOn(newRampartInteger(1), RELATIVE_KEY);
        assertThat(message, equalTo("\"" + TRAVERSAL_KEY + "\" declaration list entries must be constants"));
    }


    /** Creates a new symbol table with the given content. */
    static Map<String, RampartList> syms(RampartObject...values) {
        final Map<String, RampartList> symbolTable = new HashMap<>();
        symbolTable.put(TRAVERSAL_KEY.toString(), newRampartList(values));
        return symbolTable;
    }

    /** Gets a failure message for parsing the given values. */
    static String failOn(RampartObject...values) {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> RampartTraversalParser.parseTraversalOptions(syms(values)));
        return thrown.getMessage();
    }
}
