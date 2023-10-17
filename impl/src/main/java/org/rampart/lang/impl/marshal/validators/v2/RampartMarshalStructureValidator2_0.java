package org.rampart.lang.impl.marshal.validators.v2;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartRuleType;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.core.validators.v2.RampartRuleStructureValidator2_0Plus;
import org.rampart.lang.java.builder.RampartMarshalBuilder;
import java.util.Map;

public class RampartMarshalStructureValidator2_0 extends RampartRuleStructureValidator2_0Plus {

    protected RampartMarshalBuilder builder;

    public RampartMarshalStructureValidator2_0(Map<String, RampartList> visitorSymbolTable,
                                               RampartMarshalBuilder builder) {
        super(visitorSymbolTable, RampartRuleType.MARSHAL);
        this.builder = builder;
    }

    public void crossValidate() throws InvalidRampartRuleException {
        // nop
    }

}
