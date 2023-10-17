package org.rampart.lang.api.core;

import org.rampart.lang.api.*;

public interface RampartMetadata extends RampartObject {
    /**
     * Get the value of the `cwe` metadata entry.
     * @return the value of the metadata entry as an RampartList of RampartStrings
     */
    RampartList getCwe();

    /**
     * Check the `cwe` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsCwe();

    /**
     * Get the value of the `cve` metadata entry.
     * @return the value of the metadata entry as an RampartList of RampartStrings
     */
    RampartList getCve();

    /**
     * Check the `cve` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsCve();

    /**
     * Get the value of the `cvss` metadata entry.
     * @return the value of the metadata entry as an RampartCvss
     */
    RampartCvss getCvss();

    /**
     * Check the `cvss` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsCvss();

    /**
     * Get the value of the `description` metadata entry.
     * @return the value of the metadata entry as an RampartString
     */
    RampartString getDescription();

    /**
     * Check the `description` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsDescription();

    /**
     * Get the value of the `affected-os` metadata entry.
     * @return the value of the metadata entry as an RampartList of RampartStrings
     */
    RampartList getAffectedOperatingSystem();

    /**
     * Check the `affected-os` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsAffectedOperatingSystem();

    /**
     * Get the value of the `affected-product-name` metadata entry.
     * @return the value of the metadata entry as an RampartString
     */
    RampartString getAffectedProductName();

    /**
     * Check the `affected-product-name` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsAffectedProductName();

    /**
     * Get the value of the `affected-product-version` metadata entry.
     * @return the value of the metadata entry as an RampartAffectedProductVersion instance
     */
    RampartAffectedProductVersion getAffectedProductVersion();

    /**
     * Check the `affected-product-version` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsAffectedProductVersion();

    /**
     * Get the value of the `creation-time` metadata entry.
     * @return the value of the metadata entry as an RampartString
     */
    RampartString getCreationTime();

    /**
     * Check the `creation-time` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsCreationTime();

    /**
     * Get the value of the `version` metadata entry.
     * @return the value of the metadata entry as an RampartInteger
     */
    RampartInteger getVersion();

    /**
     * Check the `version` metadata entry with exists in this metadata collection.
     * @return whether the entry exists in this collection
     */
    RampartBoolean containsVersion();

    /**
     * Check if there is a metadata entry with the provided key in this metadata collection.
     * @param key to check if the entry exists
     * @return whether the entry exists in this collection
     */
    RampartBoolean contains(RampartConstant key);

    /**
     * Get the value of a metadata entry using the provided key.
     * @param key to check if the entry exists
     * @return the value of the metadata entry
     */
    RampartObject get(RampartConstant key);

    /**
     * @return an iterator that returns metadata entries stored within this instance as RampartNamedValue objects.
     */
    RampartObjectIterator getObjectIterator();

    /**
     * Checks if there is a metadata entry with the provided key that should be logged by an agent.
     * @param key to check if an entry should be logged by an agent
     * @return whether the entry should be logged
     */
    RampartBoolean isLoggable(RampartConstant key);

    /**
     * Merge two metadata instances together. If `other` metadata collection contains an entry that already exists in
     * `this` metadata collection, then the entry is not overwritten.
     * @param other metadata instance to merge with
     * @return a brand new RampartMetadata instance with the merged entries of both `this` and `other` instances.
     */
    RampartMetadata mergeWith(RampartMetadata other);

    /**
     * Checks if there are any metadata entries.
     * @return whether this RampartMetadata has entries or not
     */
    RampartBoolean isEmpty();
}
