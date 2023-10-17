package org.rampart.lang.impl.sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.junit.jupiter.api.Test;
import org.rampart.lang.impl.sql.RampartSqlInjectionTypeImpl;

public class RampartSqlInjectionTypeImplTest {

    @Test
    public void successfulAttempWithFullQueryByDefaultIsNotValid() {
        RampartSqlInjectionType rampartSqlInjectionType =  new RampartSqlInjectionTypeImpl(
                Collections.singleton(RampartSqlInjectionTypeImpl.Type.SUCCESSFUL_ATTEMPT),
                Collections.emptyList());

        assertAll(() -> {
            assertThat(rampartSqlInjectionType.shouldPermitQueryProvided(), equalTo(RampartBoolean.FALSE));

            assertThat(rampartSqlInjectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartSqlInjectionType.onFailedAttempt(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartSqlInjectionType.getConfigurationIterator().hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void failedAttemptWithFullQueryByDefaultIsNotValid() {
        RampartSqlInjectionType rampartSqlInjectionType =  new RampartSqlInjectionTypeImpl(
                Collections.singleton(RampartSqlInjectionTypeImpl.Type.FAILED_ATTEMPT),
                Collections.emptyList());

        assertAll(() -> {
            assertThat(rampartSqlInjectionType.shouldPermitQueryProvided(), equalTo(RampartBoolean.FALSE));

            assertThat(rampartSqlInjectionType.onSuccessfulAttempt(), equalTo(RampartBoolean.FALSE));
            assertThat(rampartSqlInjectionType.onFailedAttempt(), equalTo(RampartBoolean.TRUE));
            assertThat(rampartSqlInjectionType.getConfigurationIterator().hasNext(), equalTo(RampartBoolean.FALSE));
        });
    }

    @Test
    public void sqlInjectionTypesShouldNeverBeNull() {
        assertThrows(NullPointerException.class, () -> {
            // This is our internal problem, empty collection should be passed instead.
            new RampartSqlInjectionTypeImpl(
                    null,
                    Collections.emptyList());
        });
    }

    @Test
    public void configuratiosShouldNeverBeNull() {
        assertThrows(NullPointerException.class, () -> {
            // This is our internal coding problem, empty collection should be passed instead.
            new RampartSqlInjectionTypeImpl(
                    Collections.singleton(RampartSqlInjectionTypeImpl.Type.FAILED_ATTEMPT),
                    null);
        });
    }

}
