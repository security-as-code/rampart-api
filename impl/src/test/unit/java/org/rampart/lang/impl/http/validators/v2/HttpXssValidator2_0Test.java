package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import matchers.RampartListMatcher;

public class HttpXssValidator2_0Test {

    protected Map<String, RampartList> visitorSymbolTable;

    protected HttpXssValidator2_0 getValidator(Map<String, RampartList> visitorSymbolTable) {
        return new HttpXssValidator2_0(visitorSymbolTable);
    }

    @BeforeEach
    public void setUp() {
        visitorSymbolTable = new HashMap<>();
    }

    @Test
    public void xssIsNotMandatory() throws InvalidRampartRuleException {
        assertThat(getValidator(visitorSymbolTable).validateXssConfiguration(), is(nullValue()));
    }

    @Test
    public void emptyXssDeclaration() {
        visitorSymbolTable.put(XSS_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("missing declaration of \"xss\" key"));
    }

    @Test
    public void xssDeclarationWithHtmlKeyGetsDefaultOption() throws InvalidRampartRuleException {
        visitorSymbolTable.put(XSS_KEY.toString(), newRampartList(HTML_KEY));
        RampartXss xss = getValidator(visitorSymbolTable).validateXssConfiguration();
        assertThat(xss.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(POLICY_KEY, LOOSE_KEY)));
    }

    @Test
    public void xssDeclarationWithDuplicateHtmlKey() {
        visitorSymbolTable.put(XSS_KEY.toString(), newRampartList(HTML_KEY, HTML_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("duplicate \"xss\" parameter specified \"html\""));
    }

    @Test
    public void xssDeclarationWithInvalidKey() {
        visitorSymbolTable.put(XSS_KEY.toString(), newRampartList(newRampartConstant("russian-style")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"russian-style\" for \"xss\" declaration"));
    }

    @Test
    public void xssDeclarationWithInvalidKeyType() {
        visitorSymbolTable.put(XSS_KEY.toString(), newRampartList(newRampartInteger(2)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"2\" for \"xss\" declaration"));
    }

    @Test
    public void xssDeclarationWithHtmlAndInvalidKey() {
        visitorSymbolTable.put(XSS_KEY.toString(), newRampartList(HTML_KEY, newRampartConstant("russian-style")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"russian-style\" for \"xss\" declaration"));
    }

    @Test
    public void xssDeclarationWithInvalidNameForOptions() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(newRampartConstant("more-options"), RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"more-options: []\" for \"xss\" declaration"));
    }

    @Test
    public void xssDeclarationHtmlKeyEmptyOptions() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("\"options\" parameter must be followed by a non empty list"));
    }

    @Test
    public void xssDeclarationHtmlKeyUnsupportedOption() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(
                                newRampartConstant("undefined"),
                                newRampartConstant("unsupported"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("option \"undefined\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionStrict() throws InvalidRampartRuleException {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(POLICY_KEY, STRICT_KEY)))));
        RampartXss xss = getValidator(visitorSymbolTable).validateXssConfiguration();
        assertThat(xss.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(POLICY_KEY, STRICT_KEY)));
    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionUnsupported() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(POLICY_KEY, newRampartConstant("unsupported"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"unsupported\" for option \"policy\""));
    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionLoose() throws InvalidRampartRuleException {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(POLICY_KEY, LOOSE_KEY)))));
        RampartXss xss = getValidator(visitorSymbolTable).validateXssConfiguration();
        assertThat(xss.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(POLICY_KEY, LOOSE_KEY)));

    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionStrictWithUnsupportedOption() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(
                                newRampartNamedValue(
                                        POLICY_KEY,
                                        STRICT_KEY),
                                newRampartNamedValue(
                                        newRampartConstant("undefined"),
                                        newRampartConstant("unsupported"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("option \"undefined\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void xssDeclarationHtmlKeyPolicyOptionInvalidType() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(POLICY_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"policy\""));
    }
}
