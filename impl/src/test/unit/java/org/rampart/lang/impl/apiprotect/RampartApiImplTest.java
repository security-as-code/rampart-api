package org.rampart.lang.impl.apiprotect;

import org.junit.jupiter.api.Test;

import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartCode;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.impl.core.RampartMetadataImpl;
import org.rampart.lang.impl.core.RampartRuleBackdoor;
import org.rampart.lang.java.RampartPrimitives;
import org.rampart.lang.utils.Permutations;
import org.rampart.lang.utils.Values;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


/**
 * Tests for a basic RAMPART API functionality.
 */
public final class RampartApiImplTest {

    /** Some code samples. */
    private static final RampartCode[] codes = {
        Values.javaCodeBlockForRampart2_8(
            "public void action(ApiAction action) { }"
        ),
        Values.javaCodeBlockForRampart2_8(
            "public void action(ApiAction action) { System.out.println(\"Hello, World\"); }",
            "java.lang.String"
        )
    };


    /** Some pattern samples. */
    private static final RampartList[] pathses = {
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/api/v1")
        ),
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/hello/world"),
            RampartPrimitives.newRampartString("/goodbye/world")
        ),
        RampartPrimitives.newRampartList(
            RampartPrimitives.newRampartString("/api/v1/*/world"),
            RampartPrimitives.newRampartString("/api/v2/hello/*"),
            RampartPrimitives.newRampartString("/api/v3/void")
        )
      };



    @Test
    public void basicRampartApiEqualityAndHashCode() {
        for (RampartString appName: Values.VALID_APPLICATION_NAMES) {
            for (RampartString ruleName: Values.VALID_RULE_NAMES) {
                for (RampartCode code: codes) {
                    for (RampartList oses: Values.VALID_OS_LISTS) {
                        for (RampartList paths: pathses) {
                            for (RampartHttpIOType stage: RampartHttpIOType.values()) {
                                final RampartApiImpl api1 =
                                    new RampartApiImpl(
                                        appName, ruleName,
                                        code,
                                        oses, RampartMetadataImpl.EMPTY,
                                        paths, stage
                                    );
                                RampartRuleBackdoor.setApp(api1, Values.FAKE_APPLICATION);

                                assertThat(api1.hashCode(), equalTo(api1.hashCode()));
                                assertThat(api1, equalTo(api1));

                                final RampartApiImpl api2 =
                                    new RampartApiImpl(
                                        appName, ruleName,
                                        code,
                                        oses, RampartMetadataImpl.EMPTY,
                                        paths, stage
                                    );
                                RampartRuleBackdoor.setApp(api2, Values.FAKE_APPLICATION);

                                assertThat(api1.hashCode(), equalTo(api2.hashCode()));
                                assertThat(api1, equalTo(api2));
                            }
                        }
                    }
                }
            }
        }
    }


    @Test
    public void basicRampartApiInequality() {
        int hashCollisions = 0;

        for (Permutations.Pair<RampartString> appName: Permutations.distinctPairs(Values.VALID_APPLICATION_NAMES)) {
            for (Permutations.Pair<RampartString> ruleName: Permutations.distinctPairs(Values.VALID_RULE_NAMES)) {
                for (Permutations.Pair<RampartCode> code: Permutations.distinctPairs(codes)) {
                    for (Permutations.Pair<RampartList> oses: Permutations.distinctPairs(Values.VALID_OS_LISTS)) {
                        for (Permutations.Pair<RampartList> paths: Permutations.distinctPairs(pathses)) {
                            for (Permutations.Pair<RampartHttpIOType> stage: Permutations.distinctPairs(RampartHttpIOType.values())) {
                                final RampartApiImpl api1 =
                                    new RampartApiImpl(
                                        appName.first, ruleName.first,
                                        code.first,
                                        oses.first, RampartMetadataImpl.EMPTY,
                                        paths.first, stage.first
                                    );
                                RampartRuleBackdoor.setApp(api1, Values.FAKE_APPLICATION);

                                final RampartApiImpl api2 =
                                    new RampartApiImpl(
                                        appName.second, ruleName.second,
                                        code.second,
                                        oses.second, RampartMetadataImpl.EMPTY,
                                        paths.second, stage.second
                                    );
                                RampartRuleBackdoor.setApp(api2, Values.FAKE_APPLICATION);

                                assertThat(api1, not(equalTo(api2)));

                                if (api1.hashCode() == api2.hashCode()) {
                                    hashCollisions++;
                                }
                            }
                        }
                    }
                }
            }
        }
        /* The following is not the inherent property of the hashCode but a property
         * of the current implementation. Further changes in the API implementation
         * may change the hash-code. This does not mean the code is wrong, it means
         * the assertion should be updated. In general, it should be the case
         * that the number of hash colisions is greatly lower than the total amount
         * of elements being compared.
         */
        assertThat(hashCollisions, equalTo(0));
    }
}