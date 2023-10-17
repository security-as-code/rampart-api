package org.rampart.lang.java.parser;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class FileRampartSource extends RampartSource {

    private final File file;

    public FileRampartSource(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getAbsoluteFilePath() {
        return file.getAbsolutePath();
    }

    public long getSize() {
        return file.length();
    }

    public InputStream getInputStream() throws java.io.IOException {
        return new FileInputStream(file);
    }
}