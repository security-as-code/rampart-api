package org.rampart.lang.java.builder;

import org.rampart.lang.api.http.RampartXss;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.rampart.lang.api.constants.RampartHttpConstants.LOOSE_KEY;
import static org.rampart.lang.api.constants.RampartHttpConstants.STRICT_KEY;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RampartXssBuilderTest {
    private RampartXssBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartXssBuilder();
    }

    @Test
    public void createXssDefaultOptions() {
        RampartXss xssFeature = builder.createRampartObject();
        assertThat(xssFeature.toString(), equalTo("xss(html, options: {policy: loose})"));
    }

    @Test
    public void createXssPolicyStrictOption() {
        RampartXss xssFeature = builder.addPolicyOption(STRICT_KEY).createRampartObject();
        assertThat(xssFeature.toString(), equalTo("xss(html, options: {policy: strict})"));
    }

    @Test
    public void createXssPolicyLooseOption() {
        RampartXss xssFeature = builder.addPolicyOption(LOOSE_KEY).createRampartObject();
        assertThat(xssFeature.toString(), equalTo("xss(html, options: {policy: loose})"));
    }

    @Test
    public void createXssPolicyLooseAndExcludeOption() {
        RampartXss xssFeature = builder.addPolicyOption(LOOSE_KEY)
                                    .addExcludeOption(newRampartList(newRampartString("/servlet")))
                                    .createRampartObject();
        assertThat(xssFeature.toString(), equalTo("xss(html, options: {policy: loose, exclude: [\"/servlet\"]})"));
    }

    @Test
    public void createXssPolicyStrictAndMultipleExcludesOption() {
        RampartXss xssFeature = builder.addPolicyOption(LOOSE_KEY)
                .addExcludeOption(newRampartList(
                        newRampartString("/servlet"),
                        newRampartString("/spiracle/index.html")))
                .createRampartObject();
        assertThat(xssFeature.toString(), equalTo(
                "xss(html, options: {policy: loose, exclude: [\"/servlet\", \"/spiracle/index.html\"]})"));
    }
}
