package org.rampart.lang.java.builder;

import org.rampart.lang.api.http.RampartCsrf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartCsrfBuilderTest {
    private RampartCsrfBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartCsrfBuilder();
    }

    @Test
    public void createCsrfStpDefaultOptions() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY).createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {method: [POST], token-name: \"_X-CSRF-TOKEN\", token-type: shared, ajax: validate})"));
    }

    @Test
    public void createCsrfStpDefaultOptionsAndExcludeOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY)
                                      .addExcludeOption(newRampartList(newRampartString("/servlet")))
                                      .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {exclude: [\"/servlet\"], method: [POST], token-name: \"_X-CSRF-TOKEN\", token-type: shared, ajax: validate})"));
    }

    @Test
    public void createCsrfStpDefaultOptionsAndMethodOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY)
                                      .addMethodOption(newRampartList(GET_KEY, POST_KEY))
                                      .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {method: [GET, POST], token-name: \"_X-CSRF-TOKEN\", token-type: shared, ajax: validate})"));
    }

    @Test
    public void createCsrfStpDefaultOptionsAndTokenNameOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY)
                .addTokenNameOption(newRampartString("BANANA_TOKEN"))
                .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {token-name: \"BANANA_TOKEN\", method: [POST], token-type: shared, ajax: validate})"));
    }

    @Test
    public void createCsrfStpDefaultOptionsAndTokenTypeOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY)
                .addTokenTypeOption(UNIQUE_KEY)
                .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {token-type: unique, method: [POST], token-name: \"_X-CSRF-TOKEN\", ajax: validate})"));
    }

    @Test
    public void createCsrfStpDefaultOptionsAndAjaxOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SYNCHRONIZED_TOKENS_KEY)
                .withoutAjaxOption()
                .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(synchronized-tokens, options: {ajax: no-validate, method: [POST], token-name: \"_X-CSRF-TOKEN\", token-type: shared})"));
    }

    @Test
    public void createCsrfSameOriginDefaultOptions() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SAME_ORIGIN_KEY).createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(same-origin)"));
    }

    @Test
    public void createCsrfSameOriginWithHostsOption() {
        RampartCsrf csrfFeature = builder.addCsrfAlgorithm(SAME_ORIGIN_KEY)
                                      .addHostsOption(newRampartList(newRampartString("rampart.org")))
                                      .createRampartObject();
        assertThat(csrfFeature.toString(), equalTo(
                "csrf(same-origin, options: {hosts: [\"rampart.org\"]})"));
    }
}
