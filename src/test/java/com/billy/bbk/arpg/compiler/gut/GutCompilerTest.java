package com.billy.bbk.arpg.compiler.gut;

import com.billy.bbk.arpg.AbstractTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * @author lei.liu
 * @since 2019-06-12 13:44
 */
public class GutCompilerTest extends AbstractTestBase {

    @Test
    public void compile() throws Exception {
        URL targetDirectoryURL = GutCompilerTest.class.getClassLoader().getResource("");
        Assert.assertNotNull(targetDirectoryURL);
        String txtPath = getPath("3-9.TXT");
        File file = new File(txtPath);
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());

        GutCompiler gc = new GutCompiler();
        String compile = gc.compile(txtPath);
        logger.debug("compile: {}.", compile);
    }

}