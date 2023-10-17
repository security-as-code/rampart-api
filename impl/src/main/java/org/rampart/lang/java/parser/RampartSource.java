package org.rampart.lang.java.parser;

import java.io.InputStream;
public abstract class RampartSource {
    public abstract long getSize();

    public abstract String getAbsoluteFilePath();

    public abstract InputStream getInputStream() throws java.io.IOException;

}