package org.rampart.lang.impl.core;

import java.util.List;
import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.impl.core.validators.v2.RampartConfigMapValidator;
import org.rampart.lang.impl.core.validators.v2.ConfigValueValidator;

/**
 * This interface is to be used when validating an options map which allows for full coverage and
 * validation of the map when used together with {@link RampartConfigMapValidator}. An example of the
 * given options map:
 *
 *  csrf(synchronized-tokens, options: {token-type: shared, exclude: "/servlet/", token-name: "RAMPART-TOKEN"})
 */
public interface RampartOptions {
    /**
     * Gets the default configurations for the specified supported config of the options map.
     *
     * @param config the supported configuration for the context of the options map
     * @return the defaults in the form of an RampartObject. It can be an RampartList, RampartInteger,
     *         RampartString, RampartConstant, etc... If config is not supported null is returned
     */
    RampartObject getDefaults(RampartConstant config);

    /**
     * Gets a validator function that checks for the validity of the values declared for the single
     * configuration declared in the options parameter.
     *
     * @param config single configuration to validate get the validator function for
     * @return a validator function that will validate the values of 'config'
     */
    ConfigValueValidator getOptionValidator(RampartConstant config);

    /**
     * Some options are only available for specific functionality, this time around declared by a
     * single RampartConstant. An example with CSRF:
     *
     * csrf(same-origin, options: {hosts: ["host1", "host2:8080"]})
     *
     * The above options parameter only make sense for the 'same-origin' algorithm and mean nothing
     * to the 'synchronized-tokens' approach. Hence, this method returns all the compatible
     * configurations, that can be declared inside the options parameter for the targeted
     * functionality.
     *
     * @param target the overall functionality that the options are linked to
     * @return a list of compatible configurations that can be declared within the options parameter
     *         for 'target'
     */
    List<RampartConstant> getAllConfigsForTarget(RampartConstant target);
}
