package com.billy.bbk.arpg.decompiler.gut;

import com.billy.bbk.arpg.compiler.gut.GutCommandResource;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * gut文件反编译器
 *
 * 反编译bbk a rpg的脚本文件(*.gut)，代码是完全参考rpg script decompiler实现的。
 * 当命令是buy时，原rpg script decompiler的实现有些问题，没能获得真正的数据，不知是不是bbk开发包版本不同的问题，本程序已修正
 *
 * @author lei.liu
 * @since 2019-06-12 13:37
 */
public class GutDecompiler {
    private static final byte uBaseAddr = 0x18;
    private int type; // gut文件 type
    private int index; // gut文件 index
    private int length; // 脚本文件大小(去除头部信息之后的大小)
    private boolean[] labelFlags; // 标签标志，为true说明本地址有一个label
    private int[] jumpTable; // 转移表
    private int jumpTableSize; // 转移表大小
    private RandomAccessFile raf; // 文件句柄
    private String out = ""; // 反编译后的脚本

    /**
     *
     * @param gutPath gut文件路径
     * @throws Exception exception
     */
    public GutDecompiler(String gutPath) throws Exception {
        raf = new RandomAccessFile(gutPath, "r");
        decompile();
        raf.close();
    }

    private void decompile() throws Exception {
        type = raf.read();
        index = raf.read();
        raf.seek(uBaseAddr);
        length = readWord();

        setJumpTable();
        process();
    }

    private int readWord() throws Exception {
        int low = raf.read();
        int high = raf.read();
        return low | (high << 8);
    }

    private int readDword() throws Exception {
        int wTmp = readWord();
        wTmp |= readWord() << 16;

        return wTmp;
    }

    private void setJumpTable() throws Exception {
        labelFlags = new boolean[length];

        jumpTableSize = raf.read();
        jumpTable = new int[jumpTableSize];

        for (int i = jumpTableSize * 2; i < length; i++) {
            labelFlags[i] = false;
        }

        int wTmp = 0;
        for (int i = 0; i < jumpTableSize; i++) {
            wTmp = readWord();
            if (wTmp != 0) {
                if (wTmp < 5) {
                    throw new RuntimeException("Error JumpTable!");
                }
                labelFlags[wTmp] = true;
            }
            jumpTable[i] = wTmp;
        }
    }

    private void process() throws Exception {
        List<Command> commands = new ArrayList<>();
        int n = 0;
        while( (n = raf.read()) != -1) {
            long filePointer = raf.getFilePointer() - uBaseAddr - 1;
            String cmdName = GutCommandResource.COMMAND_TEXT[n];
            String buf = "";
            buf += cmdName;
            if ("Callback".equals(cmdName) || "Return".equals(cmdName)) {
                buf += "\n";
            }
            String cmdParam = GutCommandResource.COMMAND_PARAM[n];
            if (!"".equals(cmdParam)) {
                buf += " ";
                buf += formateParam(cmdParam);
            }

            commands.add(new Command(filePointer, buf));
        }

//        System.out.println("=== commands === ");
        for (Command c : commands) {
            int address = (int)c.getAddress();
            if (labelFlags[address]) {
                out += "\n" + "label_" +
                        ("0x" + Integer.toHexString(address)).toUpperCase()
                        + ":" + "\n";
            }
            out += c.getText() + "\n";
            //System.out.println(c);
        }
//        System.out.println("=== commands === ");

        for (int i = 0; i < jumpTableSize; i++) {
            int tmp = jumpTable[i];
            if (tmp != 0) {
                out += "GutEvent " + (i+1) + " label_" + ("0x" + Integer.toHexString(tmp)).toUpperCase() + "\n";
            }
        }
    }

    private String formateParam(String cmdParam) throws Exception {
        if (cmdParam == null || "".equals(cmdParam)) {
            return "";
        }

        final String label = "label_";
        String str = "";
        int addr = 0;

        char c = 0;
        int n = 0;
        while ( n < cmdParam.length() && (c = cmdParam.charAt(n++)) != '\0') {
            if (!"".equals(str)) {
                str += " ";
            }
            switch (c) {
                case 'A': // 直接地址
                    addr = readWord();
                    labelFlags[addr] = true;
                    str = label + addr;
                    break;
                case 'C': // 字符串
                    str += "\"";
                    int f = 0;
                    List<Byte> contents = new ArrayList<>();
                    while ((f = raf.read()) != '\0') {
                        contents.add((byte) f);
                    }
                    byte[] bytes = new byte[contents.size()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = contents.get(i);
                    }
                    String contentStr = new String(bytes, "gbk");
                    str += contentStr;
                    str += "\"";
                    break;
                case 'E': // 间接地址
                    addr = jumpTable[raf.read()];
                    labelFlags[addr] = true;
                    str = label + addr;
                    break;
                case 'L':// 4字节数
                    int dword = readDword();
                    str += dword;
                    break;
                case 'N': { // 2字节数
                    int word = readWord();
                    str += word;
                }
                break;
                case 'U': { // 多个N
                    String innerStr = "";
                    while (true) {
                        long filePointer = raf.getFilePointer();
                        int b = raf.read();
                        if (b == 0) {
                            break;
                        }
                        raf.seek(filePointer);
                        int lindex = raf.read();
                        int ltype = raf.read();
                        innerStr += " " + ltype + String.format("%03d", lindex);
                    }
                    str += "\"" + innerStr.substring(1) + "\"";
                    raf.skipBytes(1); // 跳过一个00
                }
                break;
            }
        }

        return str;
    }

    public String getOut() {
        return out;
    }


}

/**
* 脚本中每行即一个命令
*/
class Command {
    final long address;
    final String text;

    public Command(long address, String text) {
        super();
        this.address = address;
        this.text = text;
    }
    public long getAddress() {
        return address;
    }
    public String getText() {
        return text;
    }
    @Override
    public String toString() {
        return "Command [address=" + address + ", text=" + text + "]";
    }
}
