package org.rampart.lang.impl.factory;

import static org.rampart.lang.api.constants.RampartGeneralConstants.REQUIRES_KEY;

import org.rampart.lang.api.*;
import org.rampart.lang.api.constants.RampartGeneralConstants;
import org.rampart.lang.api.core.RampartVersion;
import org.rampart.lang.impl.core.RampartVersionImpl;
import org.rampart.lang.impl.core.validators.v2.RampartAppValidator2_6;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.java.builder.RampartAppBuilder;
import org.rampart.lang.java.InvalidRampartAppException;
import org.rampart.lang.impl.core.Validatable;
import org.rampart.lang.impl.core.validators.v2.RampartAppValidator2_3;
import org.rampart.lang.impl.core.validators.v2.RampartAppValidatorUpTo2_3;
import org.rampart.lang.impl.interpreter.RampartInterpreterUtils;

import java.util.Map;

public class RampartAppValidatorFactory {
    private static final String RAMPART_VERSION = "RAMPART/";

    private RampartAppValidatorFactory() {}

    /**
     * Creates appropriate validator for the requested RAMPART app
     * @param appValues parameter values inside the app declaration, e.g. `app("appName"):`
     * @param appDeclarations complete declaration defined within the app, where, the Map's key is the declaration name
     *                       and the value, the list of parameter values of the declaration, e.g. `version(1.0)`
     * @return a validator for the requested Rampart app
     */
    public static Validatable<RampartAppBuilder, InvalidRampartAppException> createAppValidator(RampartList appValues,
                                                                                                Map<String, RampartList> appDeclarations) throws InvalidRampartAppException {
        RampartVersion requiredVersion = validateRequiresVersionValue(appDeclarations.remove(REQUIRES_KEY.toString()));
        if (requiredVersion.isWithinRange(RampartVersionImpl.v1_0, RampartVersionImpl.v2_2) == RampartBoolean.TRUE) {
            return new RampartAppValidatorUpTo2_3(appValues, requiredVersion, appDeclarations);
        } else if (requiredVersion.isWithinRange(RampartVersionImpl.v2_3, RampartVersionImpl.v2_5) == RampartBoolean.TRUE) {
            return new RampartAppValidator2_3(appValues, requiredVersion, appDeclarations);
        } else if (requiredVersion.greaterOrEqualThan(RampartVersionImpl.v2_6) == RampartBoolean.TRUE) {
            return new RampartAppValidator2_6(appValues, requiredVersion, appDeclarations);
        }
        throw new InvalidRampartAppException("required RAMPART language version not recognized");
    }

    private static RampartVersion validateRequiresVersionValue(RampartList requiresValues) {
        RampartObject version = RampartInterpreterUtils
                .findRampartNamedValue(RampartGeneralConstants.VERSION_KEY, requiresValues);
        if (version instanceof RampartString
                || version instanceof RampartConstant) {
            return parseVersionNumber(version.toString());
        } else {
            throw new InvalidRampartAppException("named value \"version\" is expected to be a string or a constant value.");
        }
    }

    private static RampartVersion parseVersionNumber(String value) {
        if (!value.startsWith(RAMPART_VERSION)) {
            throw new InvalidRampartAppException("version is invalid - must begin with RAMPART/");
        }

        int dotIndex = value.indexOf('.'); // RAMPART/x.y
        if (dotIndex < 0) {
            throw new InvalidRampartAppException("version is invalid - major and minor version must be separated by a dot");
        }

        int majorVersion, minorVersion;
        try {
            majorVersion = Integer.parseInt(value.substring(RAMPART_VERSION.length(), dotIndex));
            minorVersion = Integer.parseInt(value.substring(dotIndex + 1));
        } catch (NumberFormatException e) {
            throw new InvalidRampartAppException(e.getMessage(), e);
        }
        try {
            return RampartPrimitives.newRampartVersion(majorVersion, minorVersion);
        } catch(IllegalArgumentException iae) {
            throw new InvalidRampartAppException(
                    iae.getMessage() + ". Supported versions: " + RampartVersionImpl.AVAILABLE_VERSIONS, iae);
        }
    }
}
