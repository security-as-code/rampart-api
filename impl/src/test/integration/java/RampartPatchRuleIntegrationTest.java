import static org.rampart.lang.java.RampartPrimitives.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.IntegrationTestsUtils.getLatest2_XSupportedRampartVersion;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.core.RampartApp;
import org.rampart.lang.api.core.RampartMetadata;
import org.rampart.lang.api.core.RampartRule;
import org.rampart.lang.api.patch.RampartPatch;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.java.parser.RampartAppReader;
import org.rampart.lang.java.parser.StringRampartAppReader;
import matchers.RampartAppMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RampartPatchRuleIntegrationTest {

    private static final String WELL_FORMED_PATCH_RULE_APP =
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    patch(\"Sample patch rule\"):\n"
            + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
            + "        entry()\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    private static final String WELL_FORMED_PATCH_RULE_APP_V2_X =
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
            + "endapp";

    private static final String PATCH_RULE_APP_V2_X_INVALID_LANGUAGE_TYPE =
            "app(\"Sample well formed app\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    patch(\"Sample patch rule\"):\n"
            + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
            + "        entry()\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    private static final String CHECKSUMS_APP_TOO_LOW_VERSION =
            "app(\"occurrences too low version app\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    patch(\"occurrences patch\"):\n"
            + "        function(\"com/foo.bar.clain()V\", checksums: [\"123456789abcdef\"])\n"
            + "        call(\"com/foo/bar.main()V\")\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";


    private static final String CHECKSUMS_APP_WITH_APPROPRIATE_VERSION =
            "app(\"occurrences too low version app\"):\n"
            + "    requires(version: \"RAMPART/1.3\")\n"
            + "    patch(\"occurrences patch\"):\n"
            + "        function(\"com/foo.bar.clain()V\", checksums: [\"123456789abcdef\"])\n"
            + "        call(\"com/foo/bar.main()V\")\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    private static final String OCCURRENCES_APP_TOO_LOW_VERSION =
            "app(\"occurrences too low version app\"):\n"
            + "    requires(version: \"RAMPART/1.1\")\n"
            + "    patch(\"occurrences patch\"):\n"
            + "        function(\"com/foo.bar.clain()V\")\n"
            + "        call(\"com/foo/bar.main()V\", occurrences: [1, 2, 3])\n"
            + "        code(language: \"java\"):\n"
            + "            public void patch(JavaFrame frame){\n"
            + "                // patch code\n"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    private static final String OCCURRENCES_APP_APPROPRIATE_VERSION =
            "app(\"occurrences too low version app\"):\n"
            +"    requires(version: \"RAMPART/1.2\")\n"
            +"    patch(\"occurrences patch\"):\n"
            +"        function(\"com/foo.bar.clain()V\")\n"
            +"        call(\"com/foo/bar.main()V\", occurrences: [1, 2, 3])\n"
            +"        code(language: \"java\"):\n"
            +"            public void patch(JavaFrame frame){\n"
            +"                // patch code\n"
            +"            }\n"
            +"        endcode\n"
            +"    endpatch\n"
            +"endapp";

    private static final String WELL_FORMED_PATCH_RULE_APP_WITH_TABS =
            "app(\"Sample well formed app\"):\n"
            + "\trequires(version: \"RAMPART/1.5\")\n"
            + "\t\tpatch(\"CVE-2017-10295 - HttpsURLConnectionOldImpl Constructor (URL, Proxy, Handler)\"):\n"
            + "\t\t         function(\"com/sun/net/ssl/internal/www/protocol/https/HttpsURLConnectionOldImpl.<init>(Ljava/net/URL;Ljava/net/Proxy;Lcom/sun/net/ssl/internal/www/protocol/https/Handler;)V\")\n"
            + "\t\t         entry()\n"
            + "\t\t         code(language : \"java\", import: [\"java.net.MalformedURLException\", \"java.net.URL\"]):\n"
            + "             public void patch(JavaFrame frame) {\n"
            + "                 URL url = (URL)frame.loadObjectParam(1);\n"
            + "                 if (url != null) {\n"
            + "                     if (url.toExternalForm().indexOf('\n') > -1) {\n"
            + "                         frame.raiseException(new MalformedURLException(\"Illegal character in URL\"));\n"
            + "                     }\n"
            + "                 }\n"
            + "             }\n"
            + "\t\t         endcode\n"
            + "\t     endpatch\n"
            + "endapp";

    private static final String LONG_APP_WITH_PATCHES =
            "app(\"CVE-2017-10295\"):\n" +
            "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n" +
            "    patch(\"CVE-2017-10295 - HttpsURLConnectionOldImpl Constructor (URL, Proxy, Handler)\"):\n" +
            "        function(\"com/sun/net/ssl/internal/www/protocol/https/HttpsURLConnectionOldImpl.<init>(Ljava/net/URL;Ljava/net/Proxy;Lcom/sun/net/ssl/internal/www/protocol/https/Handler;)V\")\n" +
            "        entry()\n" +
            "        code(language : java, import: [\"java.net.MalformedURLException\", \"java.net.URL\"]):\n" +
            "            public void patch(JavaFrame frame) {\n" +
            "                URL url = (URL)frame.loadObjectParam(1);\n" +
            "                   if (url != null) {\n" +
            "                       if (url.toExternalForm().indexOf('\\n') > -1) {\n" +
            "                           frame.raiseException(new MalformedURLException(\"Illegal character in URL\"));\n" +
            "                       }\n" +
            "                   }\n" +
            "            }\n" +
            "        endcode\n" +
            "    endpatch\n" +
            "    patch(\"CVE-2017-10295 - HttpURLConnection Constructor (URL, String, int)\"):\n" +
            "        function(\"sun/net/www/protocol/http/HttpURLConnection.<init>(Ljava/net/URL;Ljava/lang/String;I)V\")\n" +
            "        entry()\n" +
            "        code(language : java, import: [\"java.net.MalformedURLException\", \"java.net.URL\"]):\n" +
            "            public void patch(JavaFrame frame) {\n" +
            "                String host = (String)frame.loadObjectParam(2);\n" +
            "                if (host != null) {\n" +
            "                    if (host.indexOf('\\n') > -1) {\n" +
            "                        frame.raiseException(new MalformedURLException(\"Illegal character in host\"));\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        endcode\n" +
            "    endpatch\n" +
            "    patch(\"CVE-2017-10295 - HttpURLConnection Constructor (URL, Proxy, Handler)\"):\n" +
            "        function(\"sun/net/www/protocol/http/HttpURLConnection.<init>(Ljava/net/URL;Ljava/net/Proxy;Lsun/net/www/protocol/http/Handler;)V\")\n" +
            "        entry()\n" +
            "        code(language : java, import: [\"java.net.MalformedURLException\", \"java.net.URL\"]):\n" +
            "            public void patch(JavaFrame frame) {\n" +
            "                URL url = (URL)frame.loadObjectParam(1);\n" +
            "                if (url != null) {\n" +
            "                    if (url.toExternalForm().indexOf('\\n') > -1) {\n" +
            "                        frame.raiseException(new MalformedURLException(\"Illegal character in URL\"));\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        endcode\n" +
            "    endpatch\n" +
            "    patch(\"CVE-2017-10295 - HttpsURLConnection Constructor (URL, Proxy, Handler)\"):\n" +
            "        function(\"sun/net/www/protocol/https/HttpsURLConnectionImpl.<init>(Ljava/net/URL;Ljava/net/Proxy;Lsun/net/www/protocol/https/Handler;)V\")\n" +
            "        entry()\n" +
            "        code(language : java, import: [\"java.net.MalformedURLException\", \"java.net.URL\"]):\n" +
            "            public void patch(JavaFrame frame) {\n" +
            "                URL url = (URL)frame.loadObjectParam(1);\n" +
            "                if (url != null) {\n" +
            "                    if (url.toExternalForm().indexOf('\\n') > -1) {\n" +
            "                        frame.raiseException(new MalformedURLException(\"Illegal character in URL\"));\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        endcode\n" +
            "    endpatch\n" +
            "endapp";

    private static final String JAVASCRIPT_PATCH_2_7_INVALID_LANGUAGE =
            "app(\"javascript patch 1\"):\n"
            + "    requires(version: RAMPART/2.7)\n"
            + "    patch(\"nodejs patch 1\"):\n"
            + "        function(\"TestClass.prototype.hello\")\n"
            + "        entry()\n"
            + "        code(language: javascript):\n"
            + "            console.log('rampart javascript patch 1');\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";


    private static final String JAVASCRIPT_PATCH_WITHOUT_IMPORT =
            "app(\"javascript patch 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    patch(\"nodejs patch 1\"):\n"
            + "        function(\"TestClass.prototype.hello\")\n"
            + "        entry()\n"
            + "        code(language: javascript):\n"
            + "            patch () {"
            + "              console.log('Hello Node!');"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    private static final String JAVASCRIPT_PATCH_WITH_IMPORT =
            "app(\"javascript patch 1\"):\n"
            + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
            + "    patch(\"nodejs patch 1\"):\n"
            + "        function(\"TestClass.prototype.hello\")\n"
            + "        entry()\n"
            + "        code(language: javascript, import: [\"import { export1 } from 'module-name';\"]):\n"
            + "            patch () {"
            + "              console.log('Hello Node!');"
            + "            }\n"
            + "        endcode\n"
            + "    endpatch\n"
            + "endapp";

    @Test
    public void validPatchAppIsParsedCorrectly() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(WELL_FORMED_PATCH_RULE_APP)), WELL_FORMED_PATCH_RULE_APP.length())
                .readApp();
        assertThat(app, RampartAppMatcher.equalTo(WELL_FORMED_PATCH_RULE_APP));
    }

    @Test
    public void tooLowVersionAppWithChecksumsLeavesChecksumsFieldAsEmptyList() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(CHECKSUMS_APP_TOO_LOW_VERSION)),
                    CHECKSUMS_APP_TOO_LOW_VERSION.length())
                .readApp();
        RampartPatch patchRule = (RampartPatch) app.getRuleIterator().next();

        assertAll(() -> {
            assertThat(app, RampartAppMatcher.equalTo(
                    "app(\"occurrences too low version app\"):\n"
                            + "    requires(version: \"RAMPART/1.1\")\n"
                            + "    patch(\"occurrences patch\"):\n"
                            + "        function(\"com/foo.bar.clain()V\")\n"
                            + "        call(\"com/foo/bar.main()V\")\n"
                            + "        code(language: \"java\"):\n"
                            + "            public void patch(JavaFrame frame){\n"
                            + "                // patch code\n"
                            + "            }\n"
                            + "        endcode\n"
                            + "    endpatch\n"
                            + "endapp"));
            assertThat(patchRule.getFunction().getChecksums(), equalTo(RampartList.EMPTY));
        });
    }

    @Test
    public void appropriateVersionAppWithChecksumsSetsChecksumsField() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(CHECKSUMS_APP_WITH_APPROPRIATE_VERSION)),
                        CHECKSUMS_APP_WITH_APPROPRIATE_VERSION.length())
                .readApp();
        RampartPatch patchRule = (RampartPatch) app.getRuleIterator().next();

        assertAll(() -> {
            assertThat(app, RampartAppMatcher.equalTo(CHECKSUMS_APP_WITH_APPROPRIATE_VERSION));
            assertThat(patchRule.getFunction().getChecksums(), equalTo(newRampartList(newRampartString("123456789abcdef"))));
        });
    }

    @Test
    public void tooLowVersionAppWithOccurrencesLeavesOccurrencesFieldAsEmptyList() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(OCCURRENCES_APP_TOO_LOW_VERSION)),
                        OCCURRENCES_APP_TOO_LOW_VERSION.length())
                .readApp();
        RampartPatch patchRule = (RampartPatch) app.getRuleIterator().next();

        assertAll(() -> {
            assertThat(app, RampartAppMatcher.equalTo(
                    "app(\"occurrences too low version app\"):\n"
                            + "    requires(version: \"RAMPART/1.1\")\n"
                            + "    patch(\"occurrences patch\"):\n"
                            + "        function(\"com/foo.bar.clain()V\")\n"
                            + "        call(\"com/foo/bar.main()V\")\n"
                            + "        code(language: \"java\"):\n"
                            + "            public void patch(JavaFrame frame){\n"
                            + "                // patch code\n"
                            + "            }\n"
                            + "        endcode\n"
                            + "    endpatch\n"
                            + "endapp"));
            assertThat(patchRule.getLocation().getOccurrences(), equalTo(RampartList.EMPTY));
        });
    }

    @Test
    public void appropriateVersionAppWithOccurrencesSetsOccurrencesField() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(OCCURRENCES_APP_APPROPRIATE_VERSION)),
                OCCURRENCES_APP_APPROPRIATE_VERSION.length())
                .readApp();
        RampartPatch patchRule = (RampartPatch) app.getRuleIterator().next();

        assertAll(() -> {
            assertThat(app, RampartAppMatcher.equalTo(OCCURRENCES_APP_APPROPRIATE_VERSION));
            assertThat(patchRule.getLocation().getOccurrences(),
                    equalTo(newRampartList(
                            RampartPrimitives.newRampartInteger(1),
                            RampartPrimitives.newRampartInteger(2),
                            RampartPrimitives.newRampartInteger(3))));
        });
    }

    @Test
    public void patchToStringImplementation() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(WELL_FORMED_PATCH_RULE_APP_WITH_TABS)),
                WELL_FORMED_PATCH_RULE_APP_WITH_TABS.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(WELL_FORMED_PATCH_RULE_APP_WITH_TABS));
    }

    @Test
    public void patchToStringOnLongAppWithPatches() throws InvalidRampartAppException, IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(LONG_APP_WITH_PATCHES)),
                LONG_APP_WITH_PATCHES.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(LONG_APP_WITH_PATCHES));
    }

    @Test
    public void validPatchAppIsParsedCorrectlyV2_0() throws IOException {
        RampartApp app = new RampartAppReader(new BufferedReader(
                new StringReader(WELL_FORMED_PATCH_RULE_APP_V2_X)), WELL_FORMED_PATCH_RULE_APP_V2_X.length())
                .readApp();

        assertThat(app, RampartAppMatcher.equalTo(WELL_FORMED_PATCH_RULE_APP_V2_X));
    }

    @Test
    public void patchAppInvalidCodeLanguageType() throws InvalidRampartAppException {
        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(
                        new StringReader(PATCH_RULE_APP_V2_X_INVALID_LANGUAGE_TYPE)),
                        PATCH_RULE_APP_V2_X_INVALID_LANGUAGE_TYPE.length()).readApp());
    }

    @Test
    public void patchAppFunctionSignatureValidatedV2_X() throws InvalidRampartAppException {
        String appText = "app(\"invalid signature patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"example patch\"):\n"
                + "        function(\"com/foo.bar.Class()V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
    }

    @Test
    public void patchAppFunctionSignatureNotValidatedV2_0() throws InvalidRampartAppException, IOException {
        String appText = "app(\"invalid signature patch\"):\n"
                + "    requires(version: RAMPART/2.0)\n"
                + "    patch(\"example patch\"):\n"
                + "        function(\"com.foo.Class.bar()V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertAll(() -> {
            RampartRule patchRule = app.getRuleIterator().next();
            assertThat(patchRule, instanceOf(RampartPatch.class));
            assertThat(((RampartPatch) patchRule).getFunction().getFunctionName(),
                    equalTo(newRampartString("com.foo.Class.bar()V")));
        });
    }

    @Test
    public void patchAppCallLocationSignatureValidatedV2_X() throws InvalidRampartAppException {
        String appText = "app(\"invalid signature patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"example patch\"):\n"
                + "        function(\"com/foo/Class.bar()V\")\n"
                + "        call(\"com/foo/bar/Class.main\")\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
    }

    @Test
    public void patchAppErrorLocationSignatureValidatedV2_X() throws InvalidRampartAppException {
        String appText = "app(\"invalid signature patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"example patch\"):\n"
                + "        function(\"com/foo/Class.bar()V\")\n"
                + "        error(\"java.io.IOException\")\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
    }

    @Test
    public void patchAppReadLocationSignatureValidatedV2_X() throws InvalidRampartAppException {
        String appText = "app(\"invalid signature patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"example patch\"):\n"
                + "        function(\"com/foo/Class.bar()V\")\n"
                + "        read(\"com.foo.bar.Class.field\")\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
    }

    @Test
    public void patchForCsharpStringConversion1_x() throws IOException {
        String appText = "app(\"Test import csharp .Net patch\"):\n"
                + "    requires(version: \"RAMPART/1.1\")\n"
                + "    patch(\"Test import csharp .Net patch\"):\n"
                + "        function(\"instance void [System.Web]System.Web.UI.Page::ProcessRequest(class [System.Web]System.Web.HttpContext)\")\n"
                + "        entry()\n"
                + "        code(language: \"csharp\", import: [\"System->\","
                + " \"System.Collections.Specialized->C:\\\\\\\\external_apps\\\\asp_dot_net\\\\WebGoat.NET\\\\bin\\\\System.Collections.Specialized.dll\","
                + " \"System.Text.RegularExpressions->\", \"System.Linq->System.Core.dll\", \"System.Web->\"]):\n"
                + "            public override void Patch(Rampart.Patch.Libraries.CSharpFrame frame) {\n"
                + "                RampartEvent evt = RampartEvent.Load(\"IIS_Rampart_test\", CefSeverity.High);\n"
                + "                evt.AddExtension(\"msg\", \"IIS_Rampart_test executed\");\n"
                + "                evt.Commit();\n"
                + "                frame.RaiseException(new System.IO.IOException(\"Rampart test exception\"));\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";


        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void patchForCsharpStringConversion2_x() throws IOException {
        String appText = "app(\"Test import csharp .Net patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"Test import csharp .Net patch\"):\n"
                + "        function(\"instance void [System.Web]System.Web.UI.Page::ProcessRequest(class [System.Web]System.Web.HttpContext)\")\n"
                + "        entry()\n"
                + "        code(language: csharp, import: [\"System->\","
                + " \"System.Collections.Specialized->C:\\\\\\\\external_apps\\\\asp_dot_net\\\\WebGoat.NET\\\\bin\\\\System.Collections.Specialized.dll\", \"System.Text.RegularExpressions->\", \"System.Linq->System.Core.dll\", \"System.Web->\"]):\n"
                + "            public override void Patch(Rampart.Patch.Libraries.CSharpFrame frame) {\n"
                + "                RampartEvent evt = RampartEvent.Load(\"IIS_Rampart_test\", CefSeverity.High);\n"
                + "                evt.AddExtension(\"msg\", \"IIS_Rampart_test executed\");\n"
                + "                evt.Commit();\n"
                + "                frame.RaiseException(new System.IO.IOException(\"Rampart test exception\"));\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        RampartApp app = new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp();

        assertThat(app, RampartAppMatcher.equalTo(appText));
    }

    @Test
    public void patchRuleInvalidDeclaration() throws InvalidRampartAppException {
        String appText = "app(\"invalid patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        unsupported()"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new RampartAppReader(new BufferedReader(new StringReader(appText)), appText.length()).readApp());
    }

    @Test
    public void metadataInRuleStart() throws Exception {
        String appText = "app(\"valid patch\"):\n"
        + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
        + "    patch(\"Sample patch rule\"):\n"
        + "        metadata(\n"
        + "            foo: \"bar\")\n"
        + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
        + "        entry()\n"
        + "        code(language: java):\n"
        + "            public void patch(JavaFrame frame){\n"
        + "                // patch code\n"
        + "            }\n"
        + "        endcode\n"
        + "    endpatch\n"
        + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        RampartObject value = ruleMetadata.get(newRampartConstant("foo"));
        assertThat(value, equalTo(newRampartString("bar")));
    }

    @Test
    public void metadataInRuleEnd() throws Exception {
        String appText = "app(\"valid patch\"):\n"
                + "    requires(version: RAMPART/" + getLatest2_XSupportedRampartVersion() + ")\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "    endpatch\n"
                + "endapp";

        Collection<RampartApp> apps = new StringRampartAppReader(appText).readApps();
        RampartMetadata ruleMetadata = apps.iterator().next().getRuleIterator().next().getMetadata();
        RampartObject value = ruleMetadata.get(newRampartConstant("foo"));
        assertThat(value, equalTo(newRampartString("bar")));
    }

    @Test
    public void metadataInRuleOlderVersion() {
        String appText = "app(\"valid patch\"):\n"
                + "    requires(version: RAMPART/2.5)\n"
                + "    patch(\"Sample patch rule\"):\n"
                + "        metadata(\n"
                + "            foo: \"bar\")\n"
                + "        function(\"com/foo/bar.main([Ljava/lang/String;)V\")\n"
                + "        entry()\n"
                + "        code(language: java):\n"
                + "            public void patch(JavaFrame frame){\n"
                + "                // patch code\n"
                + "            }\n"
                + "        endcode\n"
                + "    endpatch\n"
                + "endapp";

        assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(appText).readApps());
    }

    @Test
    public void javascriptPatchWithoutImports() throws InvalidRampartAppException, IOException {
        Collection<RampartApp> apps = new StringRampartAppReader(JAVASCRIPT_PATCH_WITHOUT_IMPORT).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(JAVASCRIPT_PATCH_WITHOUT_IMPORT)));
    }

    @Test
    public void javascriptPatchWithImports() throws InvalidRampartAppException, IOException {
        Collection<RampartApp> apps = new StringRampartAppReader(JAVASCRIPT_PATCH_WITH_IMPORT).readApps();
        assertThat(apps, contains(RampartAppMatcher.equalTo(JAVASCRIPT_PATCH_WITH_IMPORT)));
    }

    @Test
    public void javascriptPatch2_7IsIsInvalidLanguage() throws InvalidRampartAppException {
        InvalidRampartAppException thrown = assertThrows(InvalidRampartAppException.class,
                () -> new StringRampartAppReader(JAVASCRIPT_PATCH_2_7_INVALID_LANGUAGE).readApps());
        assertThat(thrown.getMessage(),
                equalTo("unsupported language for patch, must be one of: [java, csharp]"));
    }
}
