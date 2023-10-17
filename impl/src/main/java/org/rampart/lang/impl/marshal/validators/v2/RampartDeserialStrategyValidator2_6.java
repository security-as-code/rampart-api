package org.rampart.lang.impl.marshal.validators.v2;

import static org.rampart.lang.api.constants.RampartMarshalConstants.DESERIALIZE_KEY;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.impl.core.InvalidRampartRuleException;
import org.rampart.lang.impl.marshal.DeserialStrategy;
import java.util.Map;

public class RampartDeserialStrategyValidator2_6 extends RampartDeserialStrategyValidator2_0 {

    private final boolean hasDeserialConfig;

    public RampartDeserialStrategyValidator2_6(Map<String, RampartList> visitorSymbolTable) {
        super(visitorSymbolTable);
        hasDeserialConfig = hasDeserialConfig(visitorSymbolTable);
    }

    // @Override
    public DeserialStrategy validateStrategy() throws InvalidRampartRuleException {
        if (!this.hasDeserialConfig) {
            // NOTE:    As of RAMPART/2.6 and the introduction of the XXE
            //          configuration for the Marshal rule, it is now
            //          OK to return 'null' for the DeserialStrategy.
            return null;
        }
        return super.validateStrategy();
    }

    private static boolean hasDeserialConfig(Map<String, RampartList> visitorSymbolTable) {
        return visitorSymbolTable.get(DESERIALIZE_KEY.toString()) != null;
    }

}
