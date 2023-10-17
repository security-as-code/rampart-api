import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.core.RampartRuleIterator;

import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.parser.FileRampartAppReader;
import org.rampart.lang.java.InvalidRampartSyntaxError;
import matchers.RampartAppMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

public class FileRampartAppReaderTest {

    @TempDir
    Path tempFolder;

    private int fileId;

    @BeforeEach
    public void setUp() {
        fileId = 0;
    }

    private File writeContentToTempFile(String content) throws IOException {
        Path appsFile = tempFolder.resolve("rules" + (fileId++) + ".rampart");
        Files.write(appsFile, Arrays.asList(content.split("\n")));
        return appsFile.toFile();
    }

    private static List<RampartRule> collectAllRules(RampartApp app) {
        ArrayList<RampartRule> rules = new ArrayList<>();
        RampartRuleIterator it = app.getRuleIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            rules.add(it.next());
        }
        return rules;
    }

    @Test
    public void appWithWithDuplicateRuleNamesThrowsException() throws Exception {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getMessage(),
                endsWith("duplicate rule name within the same app. Offending rule name \"Sample filesystem rule\""));
    }

    @Test
    public void invalidAppIsSkippedWhenFirstAppIsValid() throws Exception {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        FileRampartAppReader appReader = new FileRampartAppReader(Collections.singletonList(rulesFile));
        Collection<RampartApp> rampartApps = null;
        try {
            appReader.readApps();
        } catch(InvalidRampartSyntaxError e) {
            rampartApps = appReader.readApps();
        }

        final Collection<RampartApp> apps = rampartApps;
        assertThat(apps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample well formed app\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp\n")));
    }

    @Test
    @Disabled("test is ignored until REM-2668 is fixed")
    public void invalidAppIsSkippedWhenLastAppIsValid() throws Exception {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        FileRampartAppReader appReader = new FileRampartAppReader(Collections.singletonList(rulesFile));
        Collection<RampartApp> rampartApps = null;
        try {
            appReader.readApps();
        } catch(InvalidRampartSyntaxError e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"Sample well formed app\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp\n")));
    }

    @Test
    public void numberOfRampartAppsParsedSameFile() throws Exception {
        String firstAppName = "Sample well formed app 1";
        String secondAppName = "Sample well formed app 2";
        String thirdAppName = "Sample well formed app 3";

        File rulesFile = writeContentToTempFile(
            "app(\"" + firstAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n"
            + "app(\"" + secondAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Another sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"read log Message\", severity: 8)\n"
            + "    endfilesystem\n"
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
            + "app(\"" + thirdAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Sample http input validation rule\"):\n"
            + "        request()\n"
            + "        validate(parameters: \"sid\", is: integer)\n"
            + "        detect(message: \"log message\", severity: 3)\n"
            + "    endhttp\n"
            + "    library(\"Sample library rule\"):\n"
            + "        load(\"some.so\")\n"
            + "        detect(message: \"log message\", severity: 3)\n"
            + "    endlibrary\n"
            + "    process(\"Sample process rule\"):\n"
            + "        execute(\"/bin/ls\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endprocess\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http, database, deserialization)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: 5)\n"
            + "    endsql\n"
            + "    marshal(\"Deserialization controls\"):\n"
            + "        deserialize(java)\n"
            + "        rce()\n"
            + "        protect(message: \"attack found\", severity: 5)\n"
            + "    endmarshal\n"
            + "    socket(\"Blocking server binds on all interfaces and all ports\"):\n"
            + "        bind(server: \"0.0.0.0:0\")\n"
            + "        protect(message: \"port binding blocked\", severity: 8)\n"
            + "    endsocket\n"
            + "    dns(\"Detecting address resolution for rampart.org\"):\n"
            + "        lookup(\"rampart.org\")\n"
            + "        protect(message: \"dns lookup occurred for rampart.org\", severity: 8)\n"
            + "    enddns\n"
            + "endapp");

        Collection<RampartApp> apps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(3));
            for (RampartApp app : apps) {
                String appName = app.getAppName().toString();
                if (firstAppName.equals(appName)) {
                    assertThat(firstAppName + " should contain number of elements", collectAllRules(app).size(),
                            equalTo(1));
                } else if (secondAppName.equals(appName)) {
                    assertThat(secondAppName + " should contain number of elements", collectAllRules(app).size(),
                            equalTo(2));
                } else if (thirdAppName.equals(appName)) {
                    assertThat(thirdAppName + " should contain number of elements", collectAllRules(app).size(),
                            equalTo(7));
                } else {
                    fail("unexpected app \"" + appName + "\"");
                }
            }
        });
    }

    @Test
    public void numberOfRampartAppsParsedDifferentFiles() throws Exception {
        String firstAppName = "Sample well formed app 1";
        String secondAppName = "Sample well formed app 2";
        String thirdAppName = "Sample well formed app 3";

        File rulesFile1 = writeContentToTempFile(
            "app(\"" + firstAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n");
        File rulesFile2 = writeContentToTempFile(
            "app(\"" + secondAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Another sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"read log Message\", severity: 8)\n"
            + "    endfilesystem\n"
            + "    patch(\"Sample patch rule\"):\n"
            + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
            + "        entry()\n"
            + "        code(language: java):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp\n");
        File rulesFile3 = writeContentToTempFile(
            "app(\"" + thirdAppName + "\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    http(\"Sample http input validation rule\"):\n"
            + "        request()\n"
            + "        validate(parameters: \"sid\", is: integer)\n"
            + "        detect(message: \"log message\", severity: 3)\n"
            + "    endhttp\n"
            + "    library(\"Sample library rule\"):\n"
            + "        load(\"some.so\")\n"
            + "        detect(message: \"log message\", severity: 3)\n"
            + "    endlibrary\n"
            + "    process(\"Sample process rule\"):\n"
            + "        execute(\"/bin/ls\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "    endprocess\n"
            + "    sql(\"controls for SQL operations\"):\n"
            + "        vendor(sybase)\n"
            + "        input(http, database, deserialization)\n"
            + "        injection(failed-attempt)\n"
            + "        protect(message: \"denying sql injections\", severity: 5)\n"
            + "    endsql\n"
            + "    marshal(\"Deserialization controls\"):\n"
            + "        deserialize(java)\n"
            + "        rce()\n"
            + "        protect(message: \"attack found\", severity: 5)\n"
            + "    endmarshal\n"
            + "    socket(\"Blocking server binds on all interfaces and all ports\"):\n"
            + "        bind(server: \"0.0.0.0:0\")\n"
            + "        protect(message: \"port binding blocked\", severity: 8)\n"
            + "    endsocket\n"
            + "    dns(\"Detecting address resolution for rampart.org\"):\n"
            + "        lookup(\"rampart.org\")\n"
            + "        protect(message: \"dns lookup occurred for rampart.org\", severity: 8)\n"
            + "    enddns\n"
            + "endapp");

        Collection<RampartApp> apps = new FileRampartAppReader(Arrays.asList(rulesFile1, rulesFile2, rulesFile3)).readApps();

        assertAll(() -> {
            assertThat(apps.size(), equalTo(3));
            for (RampartApp app : apps) {
                assertThat(app.getAppName() + " should contain number of elements",
                        collectAllRules(app).size(), anyOf(
                                equalTo(1), equalTo(2), equalTo(7)));
            }
        });
    }

    @Test
    public void multipleFilesInvalidRampartAppInOneFile() throws Exception {
        File rulesFile1 = writeContentToTempFile(
            "app(\"Sample well formed app 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n");
        File rulesFile2 = writeContentToTempFile(
            "app(\"Sample well formed app 2\"):\n"
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
            + "endapp\n"
            + "app(\"invalid app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    bob:\n"
            + "    endbob\n"
            + "endapp\n");

        FileRampartAppReader
                appReader = new FileRampartAppReader(Arrays.asList(rulesFile1, rulesFile2));

        Collection<RampartApp> rampartApps = null;
        try {
            appReader.readApps();
        } catch(InvalidRampartSyntaxError e) {
            rampartApps = appReader.readApps();
        }

        final Collection<RampartApp> apps = rampartApps;
        assertAll(() -> {
            assertThat(apps.size(), equalTo(2));
            for (RampartApp app : apps) {
                assertThat(app, anyOf(
                        RampartAppMatcher.equalTo(
                            "app(\"Sample well formed app 1\"):\n"
                            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                            + "    filesystem(\"Sample filesystem rule\"):\n"
                            + "        write(\"/etc/shadow\")\n"
                            + "        protect(message: \"write log Message\", severity: Medium)\n"
                            + "    endfilesystem\n"
                            + "endapp\n"),
                        RampartAppMatcher.equalTo(
                            "app(\"Sample well formed app 2\"):\n"
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
                            + "endapp\n")));
            }
        });
    }

    @Test
    public void duplicateAppNameSameFile() throws Exception {
        File rulesFile = writeContentToTempFile(
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
            + "endapp\n");

        FileRampartAppReader appReader = new FileRampartAppReader(Collections.singletonList(rulesFile));

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps.size(), equalTo(1));
    }

    @Test
    public void duplicateAppNameErrorMessage() throws IOException {
        File rulesFile = writeContentToTempFile(
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
            + "endapp\n");

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getMessage(), endsWith("duplicate mod \"Sample well formed app\""));
    }

    @Test
    public void duplicateAppNameDifferentFiles() throws Exception {
        File rulesFile1 = writeContentToTempFile(
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp\n");
        File rulesFile2 = writeContentToTempFile(
            "app(\"Sample well formed app\"):\n"
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
            + "endapp\n");

        FileRampartAppReader
                appReader = new FileRampartAppReader(Arrays.asList(rulesFile1, rulesFile2));

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps.size(), equalTo(1));
    }

    @Test
    public void appVersionOverridesSameFileFirst() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        FileRampartAppReader appReader = new FileRampartAppReader(Collections.singletonList(rulesFile));

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appVersionOverridesSameFileLast() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        FileRampartAppReader appReader = new FileRampartAppReader(Collections.singletonList(rulesFile));

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void appVersionOverrideErrorMessage() throws IOException {
        File rulesFile = writeContentToTempFile(
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
            + "endapp");

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getMessage(), endsWith("RAMPART mod \"App example\" overridden by mod with version \"3\""));
    }

    @Test
    public void appVersionOverridesDifferentFile() throws InvalidRampartAppException, IOException {
        File rulesFile1 = writeContentToTempFile(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp");
        File rulesFile2 = writeContentToTempFile(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(2)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/passwd\")\n"
            + "        protect(message: \"log Message\", severity: 8)\n"
            + "     endfilesystem\n"
            + "endapp");

        FileRampartAppReader
                appReader = new FileRampartAppReader(Arrays.asList(rulesFile1, rulesFile2));

        Collection<RampartApp> rampartApps;
        try {
            rampartApps = appReader.readApps();
        } catch (InvalidRampartAppException e) {
            rampartApps = appReader.readApps();
        }

        assertThat(rampartApps, contains(RampartAppMatcher.equalTo(
            "app(\"App example\"):\n"
            + "     requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "     version(3)"
            + "     filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "     endfilesystem\n"
            + "endapp")));
    }

    @Test
    public void emptyFileReturnsEmptyRules() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile("");
        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    private static final char UTF8_BOM_MAGIC_NUMBER = '\ufeff';
    @Test
    public void rampartAppWithUtf8BomBytes() throws Exception {
        File rulesFile = writeContentToTempFile(
            UTF8_BOM_MAGIC_NUMBER + "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp");

        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();

        assertThat(rampartApps.size(), equalTo(1));
    }

    @Test
    public void rampartAppWithUtf8BomBytesAndNewLine() throws Exception {
        File rulesFile = writeContentToTempFile(
            UTF8_BOM_MAGIC_NUMBER + "\napp(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        write(\"/etc/shadow\")\n"
            + "        protect(message: \"write log Message\", severity: 4)\n"
            + "    endfilesystem\n"
            + "endapp");

        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();

        assertThat(rampartApps.size(), equalTo(1));
    }

    @Test
    public void appWithCommentsStart() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile(
            "\n"
            + "##### This is a comment #####\n"
            + "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp\n");

        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();

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
        File rulesFile = writeContentToTempFile(
            "app(\"App example\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    filesystem(\"Sample filesystem rule\"):\n"
            + "        read(\"/etc/shadow\")\n"
            + "        protect(message: \"log Message\", severity: High)\n"
            + "    endfilesystem\n"
            + "endapp\n"
            +"\n"
            + "##### This is a comment #####\n");

        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();

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
        File rulesFile = writeContentToTempFile("##### This is a comment #####\n");
        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void emptyText() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile("");
        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithWhitespace() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile("  \n \n\n");
        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithSingleHashComment() throws InvalidRampartAppException, IOException {
        File rulesFile = writeContentToTempFile("# comment\n");
        Collection<RampartApp> rampartApps = new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps();
        assertThat(rampartApps.size(), equalTo(0));
    }

    @Test
    public void textWithSingleInvalidLine() throws IOException {
        File rulesFile = writeContentToTempFile("foo");
        assertThrows(InvalidRampartSyntaxError.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());
    }

    @Test
    public void invalidRampartSyntaxErrorMessagePopulatesFilePath() throws Exception {
        File rulesFile = writeContentToTempFile(
                "APP(\"Mal-formed App\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        write(\"/etc/shadow\")\n"
                        + "        protect(message: \"write log Message\", severity: 4)\n"
                        + "    endfilesystem\n"
                        + "endapp");

        InvalidRampartSyntaxError thrown = assertThrows(InvalidRampartSyntaxError.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getFilePath(), equalTo(tempFolder.toAbsolutePath() + "/rules0.rampart"));
    }

    @Test
    public void invalidRampartAppExceptionFromDuplicateAppsIncludesFilename() throws IOException {
        File rulesFile = writeContentToTempFile(
                "app(\"Duplicate well formed app\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    filesystem(\"Sample filesystem rule\"):\n"
                        + "        write(\"/etc/shadow\")\n"
                        + "        protect(message: \"write log Message\", severity: 4)\n"
                        + "    endfilesystem\n"
                        + "endapp\n"
                        +"app(\"Duplicate well formed app\"):\n"
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
                        + "endapp\n");

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getFilePath().toString(), containsString("/tmp/"));
        assertThat(thrown.getFilePath().toString(), containsString("rules0.rampart"));
    }

    @Test
    public void exceptionForInvalidRequiresKeywordPopulatesFilePathField() throws Exception {
        File rulesFile = writeContentToTempFile(
                "app(\"Mal-formed App\"):\n"
                        + "    REQUIRES(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    patch(\"Sample Patch Rule\"):\n"
                        + "        function(\"walter/apps/HelloWorld.main([Ljava/lang/String;)V\")\n"
                        + "        entry()\n"
                        + "        code(language: java):\n"
                        + "            public void patch(JavaFrame frame) throws Throwable {\n"
                        + "                System.out.println(\"In RAMPART code for app: WalterPatch\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endpatch\n"
                        + "endapp");

        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getFilePath().toString(), containsString(tempFolder.toAbsolutePath() + "/rules0.rampart"));
    }

    @Test
    public void exceptionForInvalidCodeKeywordPopulatesFilePathField() throws Exception {
        File rulesFile = writeContentToTempFile(
                "app(\"Mal-formed App\"):\n"
                        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                        + "    patch(\"Sample Patch Rule\"):\n"
                        + "        function(\"walter/apps/HelloWorld.main([Ljava/lang/String;)V\")\n"
                        + "        entry()\n"
                        + "        CODE(language: java):\n"
                        + "            public void patch(JavaFrame frame) throws Throwable {\n"
                        + "                System.out.println(\"In RAMPART code for app: WalterPatch\");\n"
                        + "            }\n"
                        + "        endcode\n"
                        + "    endpatch\n"
                        + "endapp");

        InvalidRampartSyntaxError thrown = assertThrows(InvalidRampartSyntaxError.class,
                () -> new FileRampartAppReader(Collections.singletonList(rulesFile)).readApps());

        assertThat(thrown.getFilePath().toString(), containsString(tempFolder.toAbsolutePath() + "/rules0.rampart"));
    }
}