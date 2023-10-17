package org.rampart.lang.java.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.rampart.lang.api.sql.RampartSqlInjectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RampartSqlInjectionTypeBuilderTest {

    private RampartSqlInjectionTypeBuilder builder;

    @BeforeEach
    public void beforeEach() {
        builder = new RampartSqlInjectionTypeBuilder();
    }

    @Test
    public void createDefault() {
        RampartSqlInjectionType rampartSqlInjectionType = builder.createRampartObject();
        assertThat(rampartSqlInjectionType.toString(), equalTo("injection()"));
    }

    @Test
    public void createFailedAttemptsInjections() {
        RampartSqlInjectionType rampartSqlInjectionType = builder.setFailedAttemptInjections().createRampartObject();
        assertThat(rampartSqlInjectionType.toString(), equalTo("injection(failed-attempt)"));
    }

    @Test
    public void createSuccessfulAttemptsInjections() {
        RampartSqlInjectionType rampartSqlInjectionType = builder.setSuccessfulAttemptInjections().createRampartObject();
        assertThat(rampartSqlInjectionType.toString(), equalTo("injection(successful-attempt)"));
    }

    @Test
    public void createAllAttemptsInjections() {
        RampartSqlInjectionType rampartSqlInjectionType = builder.setFailedAttemptInjections()
                .setSuccessfulAttemptInjections().createRampartObject();
        assertThat(rampartSqlInjectionType.toString(), equalTo("injection(successful-attempt, failed-attempt)"));
    }

    @Test
    public void createSetPermitQueryProvided() {
        RampartSqlInjectionType rampartSqlInjectionType = builder.setPermitQueryProvided().createRampartObject();
        assertThat(rampartSqlInjectionType.toString(), equalTo("injection(permit: query-provided)"));
    }

}
