package com.billy.bbk.arpg.compiler.gut;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * @author lei.liu
 * @since 2019-06-12 13:44
 */
public class GutCompilerTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void compile() throws Exception {
        URL targetDirectoryURL = GutCompilerTest.class.getClassLoader().getResource("");
        Assert.assertNotNull(targetDirectoryURL);
        String txtPath = targetDirectoryURL.getFile() + "3-9.TXT";
        File file = new File(txtPath);
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());

        GutCompiler gc = new GutCompiler();
        String compile = gc.compile(txtPath);
        logger.debug("compile: {}.", compile);
    }

}