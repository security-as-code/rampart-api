package org.rampart.lang.java.builder;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.constants.RampartHttpConstants;
import org.rampart.lang.api.http.RampartOpenRedirect;
import org.rampart.lang.java.RampartPrimitives;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartOpenRedirectBuilderTest {
    private RampartOpenRedirectBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartOpenRedirectBuilder();
    }

    @Test
    public void createDefault() {
        RampartOpenRedirect openRedirect = builder.createRampartObject();
        assertThat(openRedirect.toString(), equalTo("open-redirect()"));
    }

    @Test
    public void createWithExcludeSubdomainsOption() {
        RampartOpenRedirect openRedirect = builder.addExcludeOption(RampartHttpConstants.SUBDOMAINS_KEY).createRampartObject();
        assertThat(openRedirect.toString(), equalTo("open-redirect(options: {exclude: subdomains})"));
    }

    @Test
    public void createWithHosts() {
        RampartList hosts = RampartPrimitives.newRampartList(newRampartString("www.another.com"));
        RampartOpenRedirect openRedirect = builder.addHosts(hosts).createRampartObject();
        assertThat(openRedirect.toString(), equalTo("open-redirect(hosts: [\"www.another.com\"])"));
    }

    @Test
    public void createFatRule() {
        RampartList hosts = RampartPrimitives.newRampartList(newRampartString("www.another.com"));
        RampartOpenRedirect openRedirect = builder.addExcludeOption(RampartHttpConstants.SUBDOMAINS_KEY)
                .addHosts(hosts).createRampartObject();
        assertThat(openRedirect.toString(), equalTo(
                "open-redirect(hosts: [\"www.another.com\"], options: {exclude: subdomains})"));
    }

    @Test
    public void createWithSetExcludeSubdomains() {
        RampartOpenRedirect openRedirect = builder.setExcludeSubdomains().createRampartObject();
        assertThat(openRedirect.toString(), equalTo("open-redirect(options: {exclude: subdomains})"));
    }

    @Test
    public void createFatRuleUsingSetExcludeSubdomains() {
        RampartList hosts = RampartPrimitives.newRampartList(newRampartString("www.another.com"));
        RampartOpenRedirect openRedirect = builder.setExcludeSubdomains().addHosts(hosts).createRampartObject();
        assertThat(openRedirect.toString(), equalTo(
                "open-redirect(hosts: [\"www.another.com\"], options: {exclude: subdomains})"));
    }

}
