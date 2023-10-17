package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class HttpTypeValidatorTest {

    private HashMap<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
    }
    @Test
    public void missingHttpTypeDeclaration() {
        assertThrows(InvalidRampartRuleException.class, () -> new HttpTypeValidator(symbolTable).validateHttpType());
    }

    @Test
    public void validateHttpTypeRequestDeclarationNoPaths() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        new HttpTypeValidator(symbolTable).validateHttpType();
    }

    @Test
    public void validateHttpTypeResponseDeclarationNoPaths() throws InvalidRampartRuleException {
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        new HttpTypeValidator(symbolTable).validateHttpType();
    }

    @Test
    public void validateHttpTypeBothRequestAndResponseDeclarations() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(
                newRampartNamedValue(PATHS_KEY, newRampartString("/webapp/index.jsp"))));
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);

        assertThrows(InvalidRampartRuleException.class, () -> new HttpTypeValidator(symbolTable).validateHttpType());
    }

    @Test
    public void validateUriPathsRequestDeclarationNoPaths() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        assertThat(new HttpTypeValidator(symbolTable).validateUriPaths(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void validateUriPathsResponseDeclarationNoPaths() throws InvalidRampartRuleException {
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        assertThat(new HttpTypeValidator(symbolTable).validateUriPaths(), equalTo(RampartList.EMPTY));
    }

    @Test
    public void pathsListWithValidUri() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(),
                newRampartList(newRampartNamedValue(PATHS_KEY, newRampartList(newRampartString("/webapp/index.jsp")))));
        new HttpTypeValidator(symbolTable).validateUriPaths();
    }

    @Test
    public void singleValidPathUri() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(),
                newRampartList(newRampartNamedValue(PATHS_KEY, newRampartString("/webapp/index.jsp"))));
        new HttpTypeValidator(symbolTable).validateUriPaths();
    }

    @Test
    public void requestDeclarationSingleRampartStringAsPathUri() {
        symbolTable.put(REQUEST_KEY.toString(),
                newRampartList(newRampartString("/webapp/index.jsp")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("invalid parameter \"/webapp/index.jsp\" passed to \"request\" declaration"));
    }

    @Test
    public void responseDeclarationSingleRampartStringAsPathUri() {
        symbolTable.put(RESPONSE_KEY.toString(),
                newRampartList(newRampartString("/webapp/index.jsp")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("invalid parameter \"/webapp/index.jsp\" passed to \"response\" declaration"));
    }

    @Test
    public void requestDeclarationInvalidRampartNamedValueUriPath() {
        symbolTable.put(REQUEST_KEY.toString(),
                newRampartList(newRampartNamedValue(URI_KEY, newRampartString("/webapp/index.jsp"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("invalid parameter \"uri: \"/webapp/index.jsp\"\" passed to \"request\" declaration"));
    }

    @Test
    public void invalidUriValueTypeWithValidUri() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(
                PATHS_KEY, newRampartList(newRampartString("/webapp/index.jsp"), newRampartInteger(1)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("\"" + PATHS_KEY + "\" declaration list entries must be quoted string literals"));
    }

    @Test
    public void invalidUriPath() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY, newRampartList(
                newRampartString("/webapp/index.jsp"),
                newRampartString("/^^")))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(), equalTo("\"/^^\" is not a valid relative URI"));
    }

    @Test
    public void absoluteUriPath() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("https://localhost:8080/webapp/index.jsp"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("\"https://localhost:8080/webapp/index.jsp\" is not a valid relative URI"));
    }

    @Test
    public void uriPathWithQuery() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("/webapp/index.jsp?name=value"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(), equalTo("\"/webapp/index.jsp?name=value\" is not a valid relative URI"));
    }

    @Test
    public void uriWithQueryNoPath() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("?name=value"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(), equalTo("\"?name=value\" is not a valid relative URI"));
    }

    @Test
    public void uriPathWithFragment() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("/webapp/index.jsp#stuff"))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(), equalTo("\"/webapp/index.jsp#stuff\" is not a valid relative URI"));
    }

    @Test
    public void emptyUri() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString(""))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(), equalTo("\"\" is not a valid relative URI"));
    }

    @Test
    public void validLeftWildcardedUri() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("*.jsp"))));
        new HttpTypeValidator(symbolTable).validateUriPaths();
    }

    @Test
    public void validRightWildcardedUri() throws InvalidRampartRuleException {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY,
                newRampartString("/myServlet/*"))));
        new HttpTypeValidator(symbolTable).validateUriPaths();
    }

    @Test
    public void emptyPathsList() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY, RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("\"" + PATHS_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void invalidPathsValueType() {
        symbolTable.put(REQUEST_KEY.toString(), newRampartList(newRampartNamedValue(PATHS_KEY, newRampartInteger(3))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpTypeValidator(symbolTable).validateUriPaths());

        assertThat(thrown.getMessage(),
                equalTo("\"paths\" parameter must be followed by a string literal or a list of string literals"));
    }

}
