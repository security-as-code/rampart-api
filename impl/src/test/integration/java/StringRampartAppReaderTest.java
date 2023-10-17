import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.InvalidRampartSyntaxError;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

public class StringRampartAppReaderTest {

    @Test
    public void appWithWithDuplicateRuleNamesThrowsException() {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"read log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "endapp";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(),
                endsWith("duplicate rule name within the same app. Offending rule name \"Sample filesystem rule\""));
    }

    @Test
    public void invalidAppIsSkippedWhenFirstAppIsValid() throws Exception {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp\n"
            + "app(\"Sample well formed app\"):\n"
            + "     requires(version: \"RAMPART/1.4\")\n"
            + "     bob:\n"
            + "     enbob\n"
            + "endapp";

        StringRampartAppReader appReader = new StringRampartAppReader(appText);
        Collection<RampartApp> rampartApps = null;
        try {
            appReader.readApps();
        } catch(InvalidRampartSyntaxError e) {
            rampartApps = appReader.readApps();
        }

        final Collection<RampartApp> apps = rampartApps;
        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(
                "app(\"Sample well formed app\"):\n"
                + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "     filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "     endfilesystem\n"
                + "endapp\n"));
        });
    }

    @Test
    @Disabled("test is ignored until REM-2668 is fixed")
    public void invalidAppIsSkippedWhenLastAppIsValid() throws Exception {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "     requires(version: \"RAMPART/1.4\")\n"
            + "     bob:\n"
            + "     endbob\n"
            + "endapp\n"
            + "app(\"Sample well formed app\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        StringRampartAppReader appReader = new StringRampartAppReader(appText);
        Collection<RampartApp> rampartApps = null;
        try {
            appReader.readApps();
        } catch(InvalidRampartSyntaxError e) {
            rampartApps = appReader.readApps();
        }

        final Collection<RampartApp> apps = rampartApps;
        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next(), RampartAppMatcher.equalTo(
                "app(\"Sample well formed app\"):\n"
                + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "     filesystem(\"Sample filesystem rule\"):\n"
                + "        read(\"/etc/shadow\")\n"
                + "        protect(message: \"log Message\", severity: High)\n"
                + "     endfilesystem\n"
                + "endapp\n"));
        });
    }


    @Test
    public void duplicateAppName() throws Exception {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n"
            +"app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
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

        StringRampartAppReader appReader = new StringRampartAppReader(appText);

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps.size(), equalTo(1));
    }

    @Test
    public void duplicateAppNameErrorMessage() {
        String appText =
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n"
            +"app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
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

        InvalidRampartAppException thrown =
                assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("duplicate mod \"Sample well formed app\""));
    }

    @Test
    public void appVersionOverridesFirst() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp\n"
            + "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(2)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        StringRampartAppReader appReader = new StringRampartAppReader(appText);

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        Collection<RampartApp> apps = rampartApps;
        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next(),
                    RampartAppMatcher.equalTo(
                        "app(\"App example\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     version(3)"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "     endfilesystem\n"
                        + "endapp"));
        });
    }

    @Test
    public void appVersionOverridesLast() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(2)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp\n"
            + "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        StringRampartAppReader appReader = new StringRampartAppReader(appText);

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        Collection<RampartApp> apps = rampartApps;
        assertAll(() -> {
            assertThat(apps.size(), equalTo(1));
            assertThat(apps.iterator().next(),
                    RampartAppMatcher.equalTo(
                        "app(\"App example\"):\n"
                        + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "     version(3)"
                        + "     filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "     endfilesystem\n"
                        + "endapp"));
        });
    }

    @Test
    public void appVersionOverrideErrorMessage() {
        String appText =
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(2)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp\n"
            + "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp";

        InvalidRampartAppException thrown =
                assertThrows(InvalidRampartAppException.class, () -> new StringRampartAppReader(appText).readApps());

        assertThat(thrown.getMessage(), equalTo("RAMPART mod \"App example\" overridden by mod with version \"3\""));
    }

    @Test
    public void emptyContentReturnsEmptyRules() throws InvalidRampartAppException, IOException {
        String appText = "";
        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }


    @Test
    public void appWithCommentsStart() throws InvalidRampartAppException, IOException {
        String appText =
            "\n"
            + "##### This is a comment #####\n"
            + "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp\n";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appWithCommentsEnd() throws InvalidRampartAppException, IOException {
        String appText =
            "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp\n"
            +"\n"
            + "##### This is a comment #####\n";

        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void textWithCommentsOnly() throws InvalidRampartAppException, IOException {
        String appText = "##### This is a comment #####\n";
        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void emptyText() throws InvalidRampartAppException, IOException {
        String appText = "";
        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithWhitespace() throws InvalidRampartAppException, IOException {
        String appText = "  \n \n\n";
        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithSingleHashComment() throws InvalidRampartAppException, IOException {
        String appText = "# comment\n";
        Collection<RampartApp> rampartApps = new StringRampartAppReader(appText).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithSingleInvalidLine() {
        String appText = "foo";
        assertThrows(InvalidRampartSyntaxError.class, () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void filenameNotAddedToExceptionProducedFromString() throws Exception {
        String appText =
                "app(\"Mal-formed app example\"):\n"
                        + "    REQUIRES(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        read(\"/etc/shadow\")\n"
                        + "        protect(message: \"log Message\", severity: High)\n"
                        + "    endfilesystem\n"
                        + "endapp\n";

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText)
                        .readApps());

        assertThat(thrown.getFilePath(), equalTo(null));
    }
}