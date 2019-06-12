package com.billy.bbk.arpg.decompiler.lib;

import com.billy.bbk.arpg.AbstractTestBase;
import org.junit.Test;

import java.io.IOException;

/**
 * @author lei.liu
 * @since 2019-06-12 15:10
 */
public class LibDecompilerTest extends AbstractTestBase {

    @Test
    public void testDecompile() throws IOException {
        String libPath = getPath("DAT.LIB");
        String targetDirectory = libPath + "_unpacked";
        LibDecompiler libDecompiler = new LibDecompiler(libPath, targetDirectory);
        libDecompiler.setEnableDebug(true);
        libDecompiler.doDecompile();
    }

}
