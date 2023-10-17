package org.rampart.lang.impl.core.validators.v1;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.core.validators.v1.RampartHttpStructureValidator1_6;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.rampart.lang.api.constants.RampartHttpConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("deprecation")
public class RampartHttpStructureValidator1_6Test {

    private Map<String, RampartList> symbolTable;

    @BeforeEach
    public void setup() {
        symbolTable = new LinkedHashMap<>();
    }

    @Test
    public void fullyValidValidateDeclaration() throws InvalidRampartRuleException {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);
        new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure();
    }

    @Test
    public void fullyValidInjectionDeclaration() throws InvalidRampartRuleException {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);
        new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure();
    }

    @Test
    public void validateDeclarationWithResponseHttpIO() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("\"response\" declaration is only supported with \"injection\" declaration"));
    }

    @Test
    public void injectionDeclarationWithRequestHttpIO() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("\"request\" declaration is only supported with \"validate\" declaration"));
    }

    @Test
    public void foreignDeclaration() {
        String invalidKey = "bla";
        symbolTable.put(invalidKey, RampartList.EMPTY);
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(),
                equalTo("unsupported declaration found. All http elements must be one of: [http, request, response, injection, validate, action]"));
    }

    @Test
    public void httpIOAndActionDeclarationsOnly() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("RAMPART http rules must contain a \"validate\" or \"injection\" declarations and an \"action\" declaration"));
    }

    @Test
    public void validateDeclarationMissingHttpIO() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("a single declaration must be specified: \"request\" or \"response\""));
    }

    @Test
    public void injectionDeclarationMissingHttpIO() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(), equalTo("a single declaration must be specified: \"request\" or \"response\""));
    }

    @Test
    public void validateDeclarationWithoutAction() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART http rules must contain a \"validate\" or \"injection\" declarations and an \"action\" declaration"));
    }

    @Test
    public void injectionDeclarationWithoutAction() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(),
                equalTo("RAMPART http rules must contain a \"validate\" or \"injection\" declarations and an \"action\" declaration"));
    }

    @Test
    public void injectionDeclarationWithRequestAndResponseDeclarations() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(INJECTION_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(),
                equalTo("a single declaration must be specified: \"request\" or \"response\""));
    }

    @Test
    public void validateDeclarationWithRequestAndResponseDeclarations() {
        symbolTable.put(HTTP_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(REQUEST_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(RESPONSE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(VALIDATE_KEY.toString(), RampartList.EMPTY);
        symbolTable.put(ACTION_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new RampartHttpStructureValidator1_6(symbolTable).validateHttpStructure());

        assertThat(thrown.getMessage(),
                equalTo("a single declaration must be specified: \"request\" or \"response\""));
    }

}
