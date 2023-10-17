package org.rampart.lang.impl.http.validators.v2;

import java.util.Map;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartActionType;
import org.rampart.lang.api.http.RampartHttpFeaturePattern;
import org.rampart.lang.java.builder.RampartHttpBuilder;

public class RampartHttpStructureValidator2_7 extends RampartHttpStructureValidator2_0 {

    public RampartHttpStructureValidator2_7(Map<String, RampartList> visitorSymbolTable, RampartHttpBuilder builder) {
        super(visitorSymbolTable, builder);
    }

    @Override
    protected boolean isActionCompatibleWith(RampartHttpFeaturePattern securityFeature) {
        RampartActionType rampartActionType = builder.getRampartAction().getActionType();
        switch (securityFeature) {
            case OPEN_REDIRECT:
                return RampartActionType.PROTECT.equals(rampartActionType)
                        || RampartActionType.DETECT.equals(rampartActionType)
                        || RampartActionType.ALLOW.equals(rampartActionType);
            default:
                return super.isActionCompatibleWith(securityFeature);
        }
    }
}
