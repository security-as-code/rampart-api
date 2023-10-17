package org.rampart.lang.impl.patch.validators;

import org.rampart.lang.api.RampartBoolean;
import org.rampart.lang.api.RampartList;
import org.rampart.lang.api.RampartObject;
import org.rampart.lang.api.RampartString;
import org.rampart.lang.api.core.RampartObjectIterator;
import org.rampart.lang.impl.core.InvalidRampartRuleException;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to validate checksums values in the Rampart function declaration
 * Eg.
 *  function("com/foo/bar.fn()V", checksums : ["123456789abcdef"])
 * @since 1.3
 */
public class RampartChecksumValidator {
    private static final int MINIMUM_CHECKSUM_LENGTH = 10;
    private static final int MAXIMUM_CHECKSUM_LENGTH = 40;
    private static final String CHECKSUMS_ERROR_PREFIX = "invalid checksums field: ";

    private final RampartObject checksumValues;

    public RampartChecksumValidator(RampartObject checksumValues) {
        this.checksumValues = checksumValues;
    }

    public RampartList validateChecksumValues() throws InvalidRampartRuleException {
        if (checksumValues == null) {
           return null;
        }

        if (!(checksumValues instanceof RampartList)) {
            throw new InvalidRampartRuleException(CHECKSUMS_ERROR_PREFIX + "must be an RampartList and not " + checksumValues.getClass());
        }

        RampartList checksumList = (RampartList) checksumValues;
        if (checksumList.isEmpty() == RampartBoolean.TRUE) {
            throw new InvalidRampartRuleException(CHECKSUMS_ERROR_PREFIX + "must be a non empty RampartList");
        }

        Set<String> uniqueChecksums = new HashSet<String>();
        RampartObjectIterator it = checksumList.getObjectIterator();
        while (it.hasNext() == RampartBoolean.TRUE) {
            validateChecksumValue(it.next(), uniqueChecksums);
        }
        return checksumList;
    }

    private void validateChecksumValue(RampartObject checksum, Set<String> uniqueChecksums) throws InvalidRampartRuleException {
        if (!(checksum instanceof RampartString)) {
            throw new InvalidRampartRuleException(CHECKSUMS_ERROR_PREFIX + "checksum [" + checksum + "] is not a String");
        }

        String checksumString = checksum.toString();
        int checksumLength = checksumString.length();
        if (checksumLength < MINIMUM_CHECKSUM_LENGTH || checksumLength > MAXIMUM_CHECKSUM_LENGTH) {
            throw new InvalidRampartRuleException(CHECKSUMS_ERROR_PREFIX +
                    "checksum [" + checksum + "] length is not in the supported range of "
                    + MINIMUM_CHECKSUM_LENGTH + "-" + MAXIMUM_CHECKSUM_LENGTH + " characters long");
        }


        if (!uniqueChecksums.add(checksumString)) {
            throw new InvalidRampartRuleException(CHECKSUMS_ERROR_PREFIX + "duplicate checksum [" + checksumString + "]");
        }
    }
}
