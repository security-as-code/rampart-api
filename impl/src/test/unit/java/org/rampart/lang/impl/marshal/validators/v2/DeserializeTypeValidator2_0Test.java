package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.*;
import static org.rampart.lang.java.RampartPrimitives.newRampartConstant;
import static org.rampart.lang.java.RampartPrimitives.newRampartString;
import static org.rampart.lang.java.RampartPrimitives.newRampartList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

public class DeserializeTypeValidator2_0Test {

    private static final RampartConstant JSON_KEY = newRampartConstant("json");
    protected HashMap<String, RampartList> symbolTable;

    protected Constructor<? extends DeserializeTypeValidator2_0> getDeserializeTypeValidatorConstructor()
            throws Exception {
        return DeserializeTypeValidator2_0.class.getDeclaredConstructor(Map.class);
    }

    @BeforeEach
    public void setup() {
        symbolTable = new HashMap<>();
    }

    @Test
    public void noKeysDeclared() {
        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(),
                equalTo("\"" + DESERIALIZE_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void deserializeDeclarationEmpty() {
        symbolTable.put(DESERIALIZE_KEY.toString(), RampartList.EMPTY);

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(),
                equalTo("\"" + DESERIALIZE_KEY + "\" declaration must be followed by a non empty list"));
    }

    @Test
    public void deserializeDeclarationUnsupportedType() {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(JSON_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new DeserializeTypeValidator2_0(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(), equalTo("\"" + DESERIALIZE_KEY
                + "\" declaration in marshal rule only supports \"java\" or \"dotnet\" constants as parameters"));
    }

    @Test
    public void deserializeDeclarationInvalidTypeFormat() {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(
                newRampartString("java")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(),
                equalTo("\"" + DESERIALIZE_KEY + "\" declaration list entries must be constants"));
    }

    @Test
    public void deserializeDeclarationCaseSensitive() {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(
                newRampartConstant("JaVa")));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new DeserializeTypeValidator2_0(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(), equalTo("\"" + DESERIALIZE_KEY
                + "\" declaration in marshal rule only supports \"java\" or \"dotnet\" constants as parameters"));
    }

    @Test
    public void deserializeDeclarationJavaIsSupported() throws Exception {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(JAVA_KEY));
        getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType();

    }

    @Test
    public void deserializeDeclarationDotNetIsSupported() throws Exception {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(DOTNET_KEY));
        getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType();

    }

    @Test
    public void deserializeDeclarationJavaAndDotNetAreSupported() throws Exception {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(JAVA_KEY, DOTNET_KEY));
        getDeserializeTypeValidatorConstructor().newInstance(symbolTable).validateDeserialType();

    }

    @Test
    public void deserializeDeclarationSupportedAndUnsupported() {
        symbolTable.put(DESERIALIZE_KEY.toString(), newRampartList(JAVA_KEY, JSON_KEY));

        InvalidRampartRuleException thrown = assertThrows(InvalidRampartRuleException.class,
                () -> new DeserializeTypeValidator2_0(symbolTable).validateDeserialType());

        assertThat(thrown.getMessage(), equalTo("\"" + DESERIALIZE_KEY
                + "\" declaration in marshal rule only supports \"java\" or \"dotnet\" constants as parameters"));
    }

}
