package org.rampart.lang.java.parser;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;

public class ZipRampartSource extends RampartSource {

    private final ZipFile file;
    private final ZipEntry entry;

    public ZipRampartSource(ZipFile file, ZipEntry entry) {
        this.file = file;
        this.entry = entry;
    }

    public String getAbsoluteFilePath() {
        return file.getName() + "::" + entry.getName();
    }

    public long getSize() {
        return entry.getSize();
    }

    public InputStream getInputStream() throws java.io.IOException {
        return file.getInputStream(entry);
    }

}