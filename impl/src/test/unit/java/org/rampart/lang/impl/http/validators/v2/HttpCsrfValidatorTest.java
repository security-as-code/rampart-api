package org.rampart.lang.impl.http.validators.v2;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.rampart.lang.impl.http.RampartCsrfOptions2_6;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartCsrf;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.http.RampartCsrfOptions2_5Minus;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import matchers.RampartListMatcher;

public class HttpCsrfValidatorTest {

    private static final RampartCsrfOptions2_5Minus CSRF_OPTIONS_ACCESSOR = new RampartCsrfOptions2_5Minus();
    private Map<String, RampartList> visitorSymbolTable;

    @BeforeEach
    public void setUp() {
        visitorSymbolTable = new HashMap<>();
    }

    @Test
    public void csrfIsNotMandatory() throws InvalidRampartRuleException {
        assertThat(new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration(), is(nullValue()));
    }

    @Test
    public void emptyCsrfDeclaration() {
        visitorSymbolTable.put(CSRF_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("missing declaration of \"csrf\" algorithm type"));
    }

    @Test
    public void csrfDeclarationWithSynchTokens() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(SYNCHRONIZED_TOKENS_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getAlgorithm(), equalTo(SYNCHRONIZED_TOKENS_KEY));
    }

    @Test
    public void csrfDeclarationWithSameOrigin() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(SAME_ORIGIN_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getAlgorithm(), equalTo(SAME_ORIGIN_KEY));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAndSameOrigin() {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(SYNCHRONIZED_TOKENS_KEY, SAME_ORIGIN_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("duplicate \"csrf\" parameter specified \"same-origin\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensDuplicate() {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(SYNCHRONIZED_TOKENS_KEY, SYNCHRONIZED_TOKENS_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("duplicate \"csrf\" parameter specified \"synchronized-tokens\""));
    }

    @Test
    public void csrfDeclarationWithInvalidAlgorithm() {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(newRampartConstant("russian-style")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("invalid parameter \"russian-style\" for \"csrf\" declaration"));
    }

    @Test
    public void csrfDeclarationWithSameOriginEmptyOptions() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, RampartList.EMPTY)));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("\"options\" parameter must be followed by a non empty list"));
    }

    @Test
    public void csrfDeclarationWithSynchTokensUnsupportedOption() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(
                                newRampartConstant("undefined"),
                                newRampartConstant("unsupported"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("option \"undefined\" is unsupported for \"options\" parameter"));
    }

    @Test
    public void csrfDeclarationWithSynchTokensUnsupportedHostsOption() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(HOSTS_KEY, newRampartString("host1"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(),
                equalTo("unsupported config \"hosts\" for \"options\" parameter in target \"synchronized-tokens\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensDefaultOptionsNoForeignOptions() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(), newRampartList(SYNCHRONIZED_TOKENS_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), not(RampartListMatcher
                .containsInAnyOrder(newRampartNamedValue(HOSTS_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(HOSTS_KEY)))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOption() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("/FileServlet/"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("/FileServlet/")))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionValidLeftWildcardedUri() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("*.jsp"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("*.jsp")))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionValidRightWildcardedUri() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("/myServlet/*"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("/myServlet/*")))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionMultipleUris() throws InvalidRampartRuleException {
        RampartString uri1 = newRampartString("/FileServlet/");
        RampartString uri2 = newRampartString("/*/file/index.html");
        RampartString uri3 = newRampartString("/");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY,
                                newRampartList(uri1, uri2, uri3))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        RampartObject excludeOptionList = RampartInterpreterUtils.findRampartNamedValue(EXCLUDE_KEY, csrf.getConfigMap());

        assertAll(() -> {
            assertThat(excludeOptionList, is(instanceOf(RampartList.class)));
            assertThat((RampartList) excludeOptionList, RampartListMatcher.containsInAnyOrder(uri1, uri2, uri3));
        });
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionInvalidUri() {
        RampartString invalidUri = newRampartString("^sd^#6#6^");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, invalidUri)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"" + invalidUri + "\" for option \"exclude\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"exclude\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensExcludeOptionMultipleUrisOneInvalid() {
        RampartString uri1 = newRampartString("/FileServlet/");
        RampartString uri2 = newRampartString("/*/file/index.html");
        RampartString invalidUri = newRampartString("^sd^#6#6^");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY,
                                newRampartList(uri1, uri2, invalidUri))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(),
                equalTo("incorrect value \"" + newRampartList(uri1, uri2, invalidUri) + "\" for option \"exclude\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionPost() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, POST_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(METHOD_KEY, newRampartList(POST_KEY))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionGet() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, GET_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(METHOD_KEY, newRampartList(GET_KEY))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionGetAndPost() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, newRampartList(GET_KEY, POST_KEY))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        RampartObject methodOptionsList = RampartInterpreterUtils.findRampartNamedValue(METHOD_KEY, csrf.getConfigMap());

        assertAll(() -> {
            assertThat(methodOptionsList, is(instanceOf(RampartList.class)));
            assertThat((RampartList) methodOptionsList, RampartListMatcher.containsInAnyOrder(GET_KEY, POST_KEY));
        });
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodUnsupportedOption() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"undefined\" for option \"method\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"method\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionValidInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, newRampartList(newRampartInteger(2), GET_KEY))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"[2, GET]\" for option \"method\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensMethodOptionLowerCase() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(METHOD_KEY, newRampartConstant("get"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"get\" for option \"method\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenTypeOptionUniqueType() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_TYPE_KEY, UNIQUE_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(TOKEN_TYPE_KEY, UNIQUE_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenTypeOptionSharedType() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_TYPE_KEY, SHARED_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(TOKEN_TYPE_KEY, SHARED_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenTypeOptionDefault() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(TOKEN_TYPE_KEY, SHARED_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenTypeUnsupportedOption() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_TYPE_KEY, newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"undefined\" for option \"token-type\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenTypeOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_TYPE_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"token-type\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenNameOption() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_NAME_KEY, newRampartString("RAMPART-ROCKS"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(TOKEN_NAME_KEY, newRampartString("RAMPART-ROCKS"))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenNameOptionDefault() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(TOKEN_NAME_KEY, newRampartString("_X-CSRF-TOKEN"))));
    }

    @Test
    public void csrfDeclarationWithSynchTokensTokenNameOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(TOKEN_NAME_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"token-name\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAjaxOptionNoValidate() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(AJAX_KEY, NO_VALIDATE_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(AJAX_KEY, NO_VALIDATE_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAjaxOptionValidate() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(AJAX_KEY, VALIDATE_KEY)))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(AJAX_KEY, VALIDATE_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAjaxOptionDefault() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(AJAX_KEY, VALIDATE_KEY)));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAjaxOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(AJAX_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"ajax\""));
    }

    @Test
    public void csrfDeclarationWithSynchTokensAjaxUnsupportedOption() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SYNCHRONIZED_TOKENS_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(AJAX_KEY, newRampartConstant("undefined"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"undefined\" for option \"ajax\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOption() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartString("host1"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher
                .containsInAnyOrder(newRampartNamedValue(HOSTS_KEY, newRampartList(newRampartString("host1")))));
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"hosts\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionNoForeignOptions() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartString("host1"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();

        assertAll(() -> {
            assertThat(csrf.getConfigMap(), not(RampartListMatcher.containsInAnyOrder(
                    newRampartNamedValue(METHOD_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(METHOD_KEY)))));
            assertThat(csrf.getConfigMap(), not(RampartListMatcher.containsInAnyOrder(
                    newRampartNamedValue(EXCLUDE_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(EXCLUDE_KEY)))));
            assertThat(csrf.getConfigMap(), not(RampartListMatcher
                    .containsInAnyOrder(newRampartNamedValue(AJAX_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(AJAX_KEY)))));
            assertThat(csrf.getConfigMap(), not(RampartListMatcher.containsInAnyOrder(
                    newRampartNamedValue(TOKEN_NAME_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(TOKEN_NAME_KEY)))));
            assertThat(csrf.getConfigMap(), not(RampartListMatcher.containsInAnyOrder(
                    newRampartNamedValue(TOKEN_TYPE_KEY, CSRF_OPTIONS_ACCESSOR.getDefaults(TOKEN_TYPE_KEY)))));
        });
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionMultipleHosts() throws InvalidRampartRuleException {
        RampartString host1 = newRampartString("host1");
        RampartString host2 = newRampartString("host2");
        RampartString host3 = newRampartString("host3");

        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartList(host1, host2, host3))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        RampartObject hostsOptionList = RampartInterpreterUtils.findRampartNamedValue(HOSTS_KEY, csrf.getConfigMap());

        assertAll(() -> {
            assertThat(hostsOptionList, is(instanceOf(RampartList.class)));
            assertThat((RampartList) hostsOptionList, RampartListMatcher.containsInAnyOrder(host1, host2, host3));
        });
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionMultipleHostsWithPorts() throws InvalidRampartRuleException {
        RampartString host1 = newRampartString("host1:322");
        RampartString host2 = newRampartString("host2:8080");
        RampartString host3 = newRampartString("host3");

        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartList(host1, host2, host3))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration();
        RampartObject hostsOptionList = RampartInterpreterUtils.findRampartNamedValue(HOSTS_KEY, csrf.getConfigMap());

        assertAll(() -> {
            assertThat(hostsOptionList, is(instanceOf(RampartList.class)));
            assertThat((RampartList) hostsOptionList, RampartListMatcher.containsInAnyOrder(host1, host2, host3));
        });
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionInvalidPort() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartString("host1: 999999"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"host1: 999999\" for option \"hosts\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginHostsOptionMultipleHostsInvalidPort() {
        RampartString host1 = newRampartString("host1:322");
        RampartString host2 = newRampartString("host2:999999");
        RampartString host3 = newRampartString("host3");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(HOSTS_KEY, newRampartList(host1, host2, host3))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_5Minus()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(),
                equalTo("incorrect value \"" + newRampartList(host1, host2, host3) + "\" for option \"hosts\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOption() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("/FileServlet/"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("/FileServlet/")))));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionValidLeftWildcardedUri() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("*.jsp"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("*.jsp")))));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionValidRightWildcardedUri() throws InvalidRampartRuleException {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartString("/myServlet/*"))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration();
        assertThat(csrf.getConfigMap(), RampartListMatcher.containsInAnyOrder(
                newRampartNamedValue(EXCLUDE_KEY, newRampartList(newRampartString("/myServlet/*")))));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionMultipleUris() throws InvalidRampartRuleException {
        RampartString uri1 = newRampartString("/FileServlet/");
        RampartString uri2 = newRampartString("/*/file/index.html");
        RampartString uri3 = newRampartString("/");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY,
                                newRampartList(uri1, uri2, uri3))))));
        RampartCsrf csrf = new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration();
        RampartObject excludeOptionList = RampartInterpreterUtils.findRampartNamedValue(EXCLUDE_KEY, csrf.getConfigMap());

        assertAll(() -> {
            assertThat(excludeOptionList, is(instanceOf(RampartList.class)));
            assertThat((RampartList) excludeOptionList, RampartListMatcher.containsInAnyOrder(uri1, uri2, uri3));
        });
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionInvalidUri() {
        RampartString invalidUri = newRampartString("^sd^#6#6^");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, invalidUri)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"" + invalidUri + "\" for option \"exclude\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionInvalidType() {
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY, newRampartInteger(2))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"2\" for option \"exclude\""));
    }

    @Test
    public void csrfDeclarationWithSameOriginExcludeOptionMultipleUrisOneInvalid() {
        RampartString uri1 = newRampartString("/FileServlet/");
        RampartString uri2 = newRampartString("/*/file/index.html");
        RampartString invalidUri = newRampartString("^sd^#6#6^");
        visitorSymbolTable.put(CSRF_KEY.toString(),
                newRampartList(SAME_ORIGIN_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(newRampartNamedValue(EXCLUDE_KEY,
                                newRampartList(uri1, uri2, invalidUri))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new HttpCsrfValidator(visitorSymbolTable, new RampartCsrfOptions2_6()).validateCsrfConfiguration());

        assertThat(thrown.getMessage(),
                equalTo("incorrect value \"" + newRampartList(uri1, uri2, invalidUri) + "\" for option \"exclude\""));
    }
}
