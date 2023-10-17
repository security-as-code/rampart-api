package org.rampart.lang.impl.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.utils.UriUtils;

public class UriUtilsTest {

    @Test
    public void isValidHost_blank() {
        assertThat(UriUtils.isValidHost(""), equalTo(false));
    }

    @Test
    public void isValidHost_hostPort() {
        assertThat(UriUtils.isValidHost("a:7001"), equalTo(true));
    }

    @Test
    public void isValidHost_host() {
        assertThat(UriUtils.isValidHost("foo"), equalTo(true));
    }

    @Test
    public void isValidUriPathPositiveCases() {
        for (String uri : new String[]{
                "/",
                "/myservlet/resource.html",
                "/servlet/",
                "/wiki/spaces/pages",
                "/admin",
                "/v2/call"
        }) {
            assertThat(uri + " should be a valid input", UriUtils.isValidUriPath(uri), equalTo(true));
        }
    }

    @Test
    public void isValidUriPathNegativeCases() {
        for (String uri : new String[]{
                "\\admin",
                "http://example.com"
        }) {
            assertThat(uri + " should be an invalid input", UriUtils.isValidUriPath(uri), equalTo(false));
        }
    }

    @Test
    public void isValidUriPathWithWildcardsPositiveCases() {
        for (String uri : new String[]{
                "/*",
                "/myservlet/*",
                "*/servlet/",
                "*/servlet/*"
        }) {
            assertThat(uri + " should be a valid input", UriUtils.isValidUriPathWithWildcards(uri), equalTo(true));
        }
    }
}
