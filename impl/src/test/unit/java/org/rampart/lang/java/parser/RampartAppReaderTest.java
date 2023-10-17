package org.rampart.lang.java.parser;

import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.rampart.lang.java.InvalidRampartAppException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class RampartAppReaderTest {

    private static final String WELL_FORMED_PATCH_RULE_APP = "app(\"Sample well formed app\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    patch(\"Sample patch rule\"):\n"
            + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n" + "        entry()\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n" + "            }\n" + "        endcode\n"
            + "    endpatch\n" + "endapp";
    private static final String OCCURRENCES_APP_APPROPRIATE_VERSION =
            "app(\"occurrences too low version app\"):\n"
                    + "    requires(version: \"RAMPART/1.2\")\n"
                    + "    patch(\"occurrences patch\"):\n"
                    + "        function(\"com/foo/bar.clain()V\")\n"
                    + "        call(\"com/foo/bar.main()V\", occurrences: [1, 2, 3])\n"
                    + "        code(language: \"java\"):\n"
                    + "            public void patch(JavaFrame frame){\n"
                    + "                // patch code\n" + "            }\n" + "        endcode\n"
                    + "    endpatch\n" + "endapp";

    @Test
    public void duplicateAppNameThrowsException() throws InvalidRampartAppException {
        RampartAppReader reader = new RampartAppReader(new BufferedReader(
                new StringReader(WELL_FORMED_PATCH_RULE_APP + "\n\n" + WELL_FORMED_PATCH_RULE_APP)),
                WELL_FORMED_PATCH_RULE_APP.length());
        // Invoke readApp twice to consume two apps with the same name.
        // We expect this to cause an Exception to be thrown.

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class, () -> {
            reader.readApp();
            reader.readApp();
        });

        assertThat(thrown.getMessage(), equalTo(
                "duplicate app \"Sample well formed app\" detected of same version 1"));
    }

    /**
     * When an error occurs during normal parsing / validation of an RampartApp an InvalidRampartAppException
     * is thrown by RAMPART.
     *
     * This is caught by MT which advances the file marker past the next `endapp` token in the rules file,
     * skipping the app.
     *
     * In the case of a duplicate appName an InvalidRampartAppException is also thrown.
     * However, the file marker has already moved by the next `endapp` token by definition as we have
     * successfully consumed and created an RampartApp object.
     *
     * Test below asserts that invocation of `skipUntilEndApp` does not advance the file marker
     * and the next app is successfully consumed.
     */
    @Test
    public void duplicateAppNameDoesNotSkipNextApp() throws InvalidRampartAppException, IOException {
        RampartAppReader reader = new RampartAppReader(new BufferedReader(new StringReader(
                WELL_FORMED_PATCH_RULE_APP + "\n\n" + WELL_FORMED_PATCH_RULE_APP + "\n\n"
                        + OCCURRENCES_APP_APPROPRIATE_VERSION)),
                WELL_FORMED_PATCH_RULE_APP.length());
        reader.readApp();
        try {
            reader.readApp();
        } catch(InvalidRampartAppException iaae) {
            reader.skipUntilEndApp();
        }
        assertThat(reader.readApp().getAppName(), equalTo(newRampartString("occurrences too low version app")));
    }

    @Test
    public void duplicateAppNameDifferentVersionIsValid() throws InvalidRampartAppException {
        String rampartApps =
                "app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/2.3)\n"
                + "    version(2)\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp\n"

                +"app(\"Sample well formed app\"):\n"
                + "    requires(version: RAMPART/2.3)\n"
                + "    version(3)\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp\n";

        RampartAppReader reader = new RampartAppReader(new BufferedReader(new StringReader(rampartApps)), rampartApps.length());

        assertDoesNotThrow(() -> {
            reader.readApp();
            reader.readApp();
        });
    }

}
