package org.rampart.lang.impl.core.validators.v2;
import static org.rampart.lang.api.constants.RampartGeneralConstants.*;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.impl.core.validators.RampartAppValidatorBase;
import org.rampart.lang.java.RampartPrimitives;

import java.util.Map;

public class RampartAppValidator2_3 extends RampartAppValidatorBase {

    public RampartAppValidator2_3(RampartList appValues, RampartVersion requiredVersion, Map<String, RampartList> appDeclarations) {
        super(appValues, requiredVersion, appDeclarations);
    }

    @Override
    protected void validateAppDeclarations(Map<String, RampartList> appDeclarations) {
        if (appDeclarations.size() > 1 ||
                (appDeclarations.size() == 1 && !appDeclarations.containsKey(VERSION_KEY.toString()))) {
            throw new InvalidRampartAppException(
                    "only \"" + VERSION_KEY + "\" declaration is allowed within the RAMPART app");
        }

        RampartList appVersionValues = appDeclarations.get(VERSION_KEY.toString());
        if (appVersionValues == null) {
            return;
        }
        if (RampartPrimitives.toJavaInt(appVersionValues.size()) != 1
                || !(appVersionValues.getFirst() instanceof RampartInteger)) {
            throw new InvalidRampartAppException(
                    "invalid \"" + VERSION_KEY + "\" declaration - only a single version integer is allowed");
        }
        RampartInteger version = (RampartInteger) appVersionValues.getFirst();
        if (version.positiveNumber() == RampartBoolean.FALSE) {
            throw new InvalidRampartAppException(
                    "version number in \"" + VERSION_KEY + "\" declaration must be a positive integer");
        }
    }

    @Override
    protected void addToBuilder(String key, RampartList parameters) throws InvalidRampartAppException {
        if (VERSION_KEY.toString().equals(key)) {
            RampartInteger appVersion = (RampartInteger) parameters.getFirst();
            builder.addAppVersion(appVersion);
        }
    }
}
