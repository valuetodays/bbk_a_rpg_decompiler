package com.billy.bbk.arpg.decompiler.gut;

import com.billy.bbk.arpg.AbstractTestBase;
import com.billy.bbk.arpg.compiler.gut.GutCompilerTest;
import org.junit.Test;

/**
 * @author lei.liu
 * @since 2019-06-12 13:42
 */
public class GutDecompilerTest extends AbstractTestBase {

    /**
     * 运行本测试方法时请先执行com.billy.bbk.arpg.compiler.gut.GutCompilerTest#compile()
     * @see GutCompilerTest#compile()
     * @throws Exception e
     */
    @Test
    public void testDecompile() throws Exception {
        // 先编译
        new GutCompilerTest().compile();

        String gutPath = getPath("3-9.gut");
        GutDecompiler gd = new GutDecompiler(gutPath);
        /*System.out.println("type:" + gd.getType());
        System.out.println("index:" + gd.getIndex());
        System.out.println("length:" + gd.getLength());*/
        String out = gd.getOut();
        System.out.println(out);
    }
}