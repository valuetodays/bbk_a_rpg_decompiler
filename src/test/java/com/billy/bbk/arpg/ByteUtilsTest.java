package com.billy.bbk.arpg;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lei.liu
 * @since 2019-06-12 14:07
 */
public class ByteUtilsTest {

    @Test
    public void testShort2HexString() {
        String s = ByteUtils.short2HexString((short)7);
        Assert.assertEquals("0700", s);
    }

}
