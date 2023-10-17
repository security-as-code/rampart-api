package org.rampart.lang.java.parser;

import java.io.*;

public class InputStreamBufferedReader extends BufferedReader {

    private static final char UTF8_BOM = '\ufeff';
    private final RampartSource rampartSource;

    public InputStreamBufferedReader(RampartSource rampartSource, InputStream inputStream) throws IOException {
        super(new InputStreamReader(inputStream));
        this.rampartSource = rampartSource;
        removeByteOrderMark(UTF8_BOM);
    }

    public RampartSource getFile() {
        return rampartSource;
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