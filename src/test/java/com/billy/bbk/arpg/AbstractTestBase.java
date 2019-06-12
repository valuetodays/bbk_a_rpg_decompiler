package com.billy.bbk.arpg;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author lei.liu
 * @since 2019-06-12 14:50
 */
public abstract class AbstractTestBase {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String getPath(String relativeFileName) {
        URL targetDirectoryURL = AbstractTestBase.class.getClassLoader().getResource("");
        Assert.assertNotNull(targetDirectoryURL);
        return targetDirectoryURL.getFile() + relativeFileName;
    }
}
