package org.rampart.lang.api.marshal;

import org.rampart.lang.api.RampartInteger;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;

public interface ExternalXmlEntityConfig extends RampartObject {

    RampartList getUris();

    RampartInteger getReferenceLimit();

    RampartInteger getReferenceExpansionLimit();

}
