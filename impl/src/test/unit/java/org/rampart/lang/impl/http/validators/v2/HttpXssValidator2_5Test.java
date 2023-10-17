package org.rampart.lang.impl.http.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.http.RampartXss;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;
import matchers.RampartListMatcher;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpXssValidator2_5Test extends HttpXssValidator2_0Test {
    protected HttpXssValidator2_0 getValidator(Map<String, RampartList> visitorSymbolTable) {
        return new HttpXssValidator2_5(visitorSymbolTable);
    }

    @Test
    public void xssDeclarationWithHtmlKeyExcludeOption() throws InvalidRampartRuleException {
        RampartString uri1 = newRampartString("/FileServlet/");

        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(EXCLUDE_KEY, newRampartList(uri1))))));
        RampartXss xss = getValidator(visitorSymbolTable).validateXssConfiguration();

        RampartObject excludeOptionList = RampartInterpreterUtils.findRampartNamedValue(EXCLUDE_KEY, xss.getConfigMap());
        assertThat(excludeOptionList, equalTo(newRampartList(uri1)));
    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionStrictAndExclude() throws InvalidRampartRuleException {
        RampartString uri1 = newRampartString("/FileServlet/");
        RampartString uri2 = newRampartString("/file/index.html");

        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(POLICY_KEY, STRICT_KEY),
                        newRampartNamedValue(EXCLUDE_KEY, newRampartList(uri1, uri2))))));
        RampartXss xss = getValidator(visitorSymbolTable).validateXssConfiguration();

        assertThat(xss.getConfigMap(), RampartListMatcher.containsInAnyOrder(newRampartNamedValue(POLICY_KEY, STRICT_KEY)));
        RampartObject excludeOptionList = RampartInterpreterUtils.findRampartNamedValue(EXCLUDE_KEY, xss.getConfigMap());
        assertThat(excludeOptionList, is(instanceOf(RampartList.class)));
        assertThat((RampartList) excludeOptionList, RampartListMatcher.containsInAnyOrder(uri1, uri2));
    }

    @Test
    public void xssDeclarationWithHtmlKeyExcludeOptionUnsupported() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY, newRampartList(
                        newRampartNamedValue(EXCLUDE_KEY, SUBDOMAINS_KEY)))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"" + SUBDOMAINS_KEY + "\" for option \"exclude\""));
    }

    @Test
    public void xssDeclarationWithHtmlKeyPolicyOptionWithUnsupportedExcludeOption() {
        visitorSymbolTable.put(XSS_KEY.toString(),
                newRampartList(HTML_KEY, newRampartNamedValue(OPTIONS_KEY,
                        newRampartList(
                                newRampartNamedValue(
                                        POLICY_KEY,
                                        STRICT_KEY),
                                newRampartNamedValue(
                                        EXCLUDE_KEY,
                                        newRampartConstant("unsupported"))))));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getValidator(visitorSymbolTable).validateXssConfiguration());

        assertThat(thrown.getMessage(), equalTo("incorrect value \"unsupported\" for option \"exclude\""));
    }
}
