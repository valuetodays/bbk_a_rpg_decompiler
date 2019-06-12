package com.billy.bbk.arpg.decompiler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author lei.liu
 * @since 2019-06-12 11:47
 */
public class FileTypeTest {
    @Test
    public void contain_normal() {
        assertTrue(FileType.contains("GUT"));
    }

    @Test
    public void contain_abnormal() {
        assertFalse(FileType.contains("NOT_EXIST"));
    }

    @Test
    public void valueByType_normal() {
        assertEquals(FileType.ACP, FileType.valueByType(FileType.ACP.type));
    }

    @Test(expected = RuntimeException.class)
    public void valueByType_abnormal() {
        FileType.valueByType(-1);
    }
}
