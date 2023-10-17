package org.rampart.lang.impl.core.validators.v1;

import static org.rampart.lang.api.constants.RampartGeneralConstants.*;
import static org.rampart.lang.java.RampartPrimitives.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.RampartCodeImpl;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

/**
 * Class to validate an Rampart code block
 * Eg.
 *  code(language: "java", import: ["java.io.File"]):
 *      public void patch(JavaFrame frame){
 *          // patch source
 *      }
 *  endcode
 *
 *  @see RampartCodeImpl
 */

public class RampartCodeValidatorUpTo1_5 {
    private static final RampartList SUPPORTED_CODE_BLOCK_LANGUAGES = newRampartList(JAVA_KEY, CSHARP_KEY);

    private final RampartList codeValues;
    private final RampartList sourceCodeList;

    public RampartCodeValidatorUpTo1_5(RampartList codeValues, RampartList sourceCodeList) {
        this.codeValues = codeValues;
        this.sourceCodeList = sourceCodeList;
    }

    /**
     * Validates the code block is well formed according to the following criteria.
     *  1. The code block requests a supported language
     *  2. Imports (if present) are well formed
     *  3. The source code block is a non-empty string literal
     * @return RampartCode instance constructed from the given values, if they are valid.
     * @throws InvalidRampartRuleException when the above criteria is not met.
     */
    public RampartCode validateCodeBlock() throws InvalidRampartRuleException {
        return new RampartCodeImpl(newRampartConstant(validateSourceLanguage().toString()), RampartVersionImpl.v1_5,
                validateSourceCode(), validateImports());
    }

    /**
     * Validates the code block language is supported by the RAMPART engine.
     * @return language requested by the code block
     * @throws InvalidRampartRuleException when the language is an invalid type
     * or if an unsupported language is requested.
     */
    protected RampartString validateSourceLanguage() throws InvalidRampartRuleException {
        RampartObject language = RampartInterpreterUtils.
                findRampartNamedValue(LANGUAGE_KEY, codeValues);
        RampartString languageRequested = getSourceCodeLanguage(language);
        if (supportedCodeBlockLanguages()
                .contains(newRampartConstant(languageRequested.toLowerCase().toString())) == RampartBoolean.FALSE) {
            throw new InvalidRampartRuleException(
                    "unsupported language for patch, must be one of: " + supportedCodeBlockLanguages());
        }
        return languageRequested;
    }

    protected RampartList supportedCodeBlockLanguages() {
        return SUPPORTED_CODE_BLOCK_LANGUAGES;
    }

    protected RampartString getSourceCodeLanguage(RampartObject language) throws InvalidRampartRuleException {
        if (!(language instanceof RampartString) || language.toString().length() == 0) {
            throw new InvalidRampartRuleException("missing language definition from code block");
        }
        return (RampartString) language;
    }

    /**
     * Validates the imports if they are present
     * @throws InvalidRampartRuleException when imports are malformed
     * @return list of imports
     */
    protected RampartList validateImports() throws InvalidRampartRuleException {
        RampartObject imports = RampartInterpreterUtils.
                findRampartNamedValue(IMPORT_KEY, codeValues);
        if (imports == null) {
            return RampartList.EMPTY; // import is an optional argument of code.
        }
        if (!areImportsValid(imports)) {
            throw new InvalidRampartRuleException("imports definition must be a list of strings");
        }
        return (RampartList) imports;
    }

    /**
     * Validates the code block's imports are of type RampartString and are not empty
     * @param importsObject object representing the code block imports.
     * @return true if all imports are valid, false otherwise
     * @throws InvalidRampartRuleException if an empty list is passed to the method.
     */
    private boolean areImportsValid(RampartObject importsObject) throws InvalidRampartRuleException {
        if (!(importsObject instanceof RampartList)) {
            return false;
        }
        RampartList imports = (RampartList) importsObject;
        RampartObjectIterator it = imports.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            RampartObject importObject = it.next();
            if (!(importObject instanceof RampartString)) {
                return false;
            }
            RampartString importString = (RampartString) importObject;
            if (importString.toString().length() == 0) {
                throw new InvalidRampartRuleException("import list entry must be a non-empty string literal");
            }
        }
        return true;
    }

    /**
     * Validates the sourceCode declaration in an Rampart patch.
     * @return RampartString instance representing the source code.
     * @throws InvalidRampartRuleException if the sourceCode declaration is an empty string or null
     */
    protected RampartString validateSourceCode() throws InvalidRampartRuleException {
        RampartObjectIterator it = sourceCodeList.getObjectIterator();
        RampartObject sourceCode = toJavaBoolean(it.hasNext()) ? it.next() : null;
        if (toJavaInt(sourceCodeList.size()) != 1
                || !(sourceCode instanceof RampartString)
                || sourceCode.toString().length() == 0) {
            throw new InvalidRampartRuleException(
                    "source code block of patch must be a non-empty string literal");
        }
        return (RampartString) sourceCode;
    }
}
