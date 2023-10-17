package org.rampart.lang.java.parser;

import java.io.*;

public class FileBufferedReader extends BufferedReader {
    private static final char UTF8_BOM = '\ufeff';
    private final File file;

    public FileBufferedReader(File file) throws IOException {
        super(new FileReader(file));
        this.file = file;
        removeByteOrderMark(UTF8_BOM);
    }

    public File getFile() {
        return file;
    }

    private void removeByteOrderMark(char BOM) throws IOException {
        mark(1);
        char[] buf = new char[1];
        if (read(buf) == 1
                && buf[0] == BOM) {
            return;
        }
        reset();
    }

}
