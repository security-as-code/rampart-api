package org.rampart.lang.impl.core.validators;

import static org.rampart.lang.java.RampartPrimitives.toJavaInt;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.java.builder.RampartAppBuilder;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.java.RampartPrimitives;

import java.util.Map;

public abstract class RampartAppValidatorBase implements Validatable<RampartAppBuilder, InvalidRampartAppException> {
    protected final RampartAppBuilder builder;
    private final RampartList appValues;
    protected final Map<String, RampartList> appDeclarations;

    public RampartAppValidatorBase(RampartList appValues, RampartVersion requiredVersion, Map<String, RampartList> appDeclarations) {
        builder = new RampartAppBuilder();
        builder.addRequiredLanguageVersion(requiredVersion);
        this.appValues = appValues;
        this.appDeclarations = appDeclarations;
    }

    // @Override
    public RampartAppBuilder validate() throws InvalidRampartAppException {
        RampartObject appParameter = validateForSingleRampartAppParameter();
        validateAppDeclarations(appDeclarations);
        builder.addAppName(validateAppName(appParameter));
        for (String declaration : appDeclarations.keySet()) {
            addToBuilder(declaration, appDeclarations.get(declaration));
        }
        return builder;
    }

    private RampartObject validateForSingleRampartAppParameter() {
        if (RampartPrimitives.toJavaInt(appValues.size()) != 1) { // app("app-name", "another value")
            throw new InvalidRampartAppException("invalid number of arguments passed to app constructor");
        }

        return appValues.getFirst();
    }

    private RampartString validateAppName(RampartObject parameter) throws InvalidRampartAppException {
        if (!(parameter instanceof RampartString)) { // app(2) or app([....])
            throw new InvalidRampartAppException(
                    "app constructor argument is expected to be the app name as a string value.");
        }
        RampartString appName = (RampartString) parameter;
        if (toJavaInt(appName.length()) == 0) {
            throw new InvalidRampartAppException("app name cannot be empty");
        }
        return appName;
    }

    protected abstract void validateAppDeclarations(Map<String, RampartList> appDeclarations);
    protected abstract void addToBuilder(String key, RampartList parameters);
}
