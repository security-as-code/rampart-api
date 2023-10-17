package org.rampart.lang.api.marshal;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.core.RampartActionableRule;

public interface RampartMarshal extends RampartActionableRule {
    RampartList getRampartDeserializeTypes();
    RampartBoolean onRemoteCodeExecution();
    RampartBoolean onDenialOfService();
    ExternalXmlEntityConfig getExternalXmlEntityConfig();
}
