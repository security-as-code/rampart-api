package org.rampart.lang.impl.marshal.validators.v2;

import org.rampart.lang.api.RampartList;

import java.util.Map;

public class RampartMarshalValidator2_3 extends RampartMarshalValidator2_0 {

    public RampartMarshalValidator2_3(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        actionValidator = new RampartMarshalActionValidator2_3(visitorSymbolTable);
        deserialTypeValidator = new DeserializeTypeValidator2_3(visitorSymbolTable);
    }

}
