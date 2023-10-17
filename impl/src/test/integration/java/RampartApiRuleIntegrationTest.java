import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.apiprotect.RampartApiImpl;
import org.rampart.lang.impl.core.RampartAppImpl;
import org.rampart.lang.impl.core.RampartMetadataImpl;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.StringRampartAppReader;
import org.rampart.lang.utils.Values;
import matchers.RampartAppMatcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


public class RampartApiRuleIntegrationTest {

    /** Some code samples. */
    private static final RampartCode[] codes = {
        Values.javaCodeBlockForRampart2_8(
            "public void action(ApiAction action) { }"
        ),
        Values.javaCodeBlockForRampart2_8(
            "public void action(ApiAction action) { System.out.println(\"Hello, World\"); }",
            "java.lang.String"
        )
    };


    /** Some pattern samples. */
    private static final RampartList[] apiPathLists = {
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/api/v1")
        ),
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/hello/world"),
            RampartPrimitives.newRampartString("/goodbye/world")
        ),
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/api/v1/*/world"),
            RampartPrimitives.newRampartString("/api/v2/hello/*"),
            RampartPrimitives.newRampartString("/api/v3/void")
        )
      };


    @Test
    public void validApiRuleAppIsParsedSuccessfully() throws IOException {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void validApiRuleAppIsParsedSuccessfully2() throws IOException {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\", \"/Sukios\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }

    @Test
    public void validApiRuleAppIsParsedSuccessfully3() throws IOException {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\", \"/Sukios\")\n"
                        + "        code(language: java, import:  [\"java.lang.Thread\", \"java.lang.String\"]):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(appText)));
    }


    @Test
    public void propertyBasedReparsingTest() throws IOException {
        for (RampartString appName: Values.VALID_APPLICATION_NAMES) {
            for (RampartString ruleName: Values.VALID_RULE_NAMES) {
                for (RampartCode code: codes) {
                    for (RampartList oses: Values.VALID_OS_LISTS) {
                        for (RampartList paths: apiPathLists) {
                            for (RampartHttpIOType stage: RampartHttpIOType.values()) {
                                final RampartApiImpl apiRule =
                                    new RampartApiImpl(
                                        appName, ruleName,
                                        code,
                                        oses, RampartMetadataImpl.EMPTY,
                                        paths, stage
                                    );
                                final RampartAppImpl app =
                                    new RampartAppImpl(
                                        RampartPrimitives.newRampartString("Hello, app"),
                                        RampartVersionImpl.v2_9,
                                        RampartPrimitives.newRampartInteger(47),
                                        RampartMetadataImpl.EMPTY,
                                        new RampartRule[] { apiRule }
                                    );
                                final Collection<RampartApp> apps = new StringRampartAppReader(app.toString()).readApps();
                                assertThat(apps.size(), equalTo(1));
                                assertThat(apps.iterator().next().getRuleIterator().next(), equalTo(apiRule));
                            }
                        }
                    }
                }
            }
        }
    }


    @Test
    public void noInputMode() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("missing one of the mandatory \"request\" or \"response\""));
    }


    @Test
    public void tooManyInputModes() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        request(\"/Greetings\")\n"
                        + "        response(\"/Greetings\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("cannot use more than one of \"request\" or \"response\" at the same time"));
    }


    @Test
    public void noURIsInRequest() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        request()\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("\"request\" declaration must be followed by a non-empty list"));
    }


    @Test
    public void noURIsInResponse() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response()\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("\"response\" declaration must be followed by a non-empty list"));
    }


    @Test
    public void badURIsInRequest() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        request(\"!*&[{})(]\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("\"request\" list entry \"!*&[{})(]\" is not a valid relative URI"));
    }


    @Test
    public void badURIsInResponse() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"!*&[{})(]\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";

        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("\"response\" list entry \"!*&[{})(]\" is not a valid relative URI"));
    }


    @Test
    public void badFieldsInMessage2Items() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\")\n"
                        + "        kitten(\"Small\")\n"
                        + "        cat(\"Brown\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertUnrecognizedDeclaration(t.getMessage(), "cat", "kitten");
    }

    @Test
    public void badFieldsInMessage3Items() {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.9)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\")\n"
                        + "        kitten(\"Small\")\n"
                        + "        cat(\"Brown\")\n"
                        + "        dog(\"Angry\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertUnrecognizedDeclaration(t.getMessage(), "cat", "dog", "kitten");
    }

    @Test
    public void apiRuleIsNotSupportedInRampart2_8() throws IOException {
        String appText =
                "app(\"Sample API rule app\"):\n"
                        + "    requires(version: RAMPART/2.8)\n"
                        + "    api(\"well formed api rule\"):\n"
                        + "        response(\"/Greetings\")\n"
                        + "        code(language: java):\n"
                        + "            public void action(ApiAction action){\n"
                        + "                System.out.println(\"Hello World\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endapi\n"
                        + "endapp\n";
        final Throwable t = assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());
        assertThat(t.getMessage(), equalTo("app \"Sample API rule app\" does not contain any valid rules"));
    }


    /**
     * Checks that the given message is valid "unrecognized declaration" message that corresponds to the
     * specific elements.
     */
    /* TODO: Better exception hierarchy where this information could be extracted from the exception? */
    private void assertUnrecognizedDeclaration(String message, String... invalidDeclarations) {
        final String commonSuffix = " not a recognized declaration in rule \"api\"";
        assertThat(message, endsWith(commonSuffix));
        message = message.substring(0, message.length() - commonSuffix.length());
        final String verb = invalidDeclarations.length > 1 ? " are" : " is";
        assertThat(message, endsWith(verb));
        message = message.substring(0, message.length() - verb.length());

        final List<String> elementNames = Arrays.asList(message.split("(, )|( and )"));
        assertThat(elementNames.size(), equalTo(invalidDeclarations.length));

        final String[] expectedElements = new String[invalidDeclarations.length];
        for (int i = 0; i < invalidDeclarations.length; i++) {
            expectedElements[i] = "\"" + invalidDeclarations[i] + "\"";
        }

        assertThat(elementNames, containsInAnyOrder(expectedElements));
    }
}
