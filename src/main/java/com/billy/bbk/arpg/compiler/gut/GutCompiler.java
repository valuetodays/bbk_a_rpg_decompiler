package com.billy.bbk.arpg.compiler.gut;

import com.billy.bbk.arpg.ByteUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lei.liu
 * @since 2019-06-12 13:44
 */
public class GutCompiler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String txtPath;
    private String gutPath;
    private byte type = 0; // gut type
    private byte index = 0; // gut index
    private List<GutCommand> commandInfoList;
    private short[] jumpTable;
    private Map<String, Integer> guteventTag;
    private Map<String, Integer> labelAddrMap;
    private String scriptBytesStr;
    private String jumpTableBytesStr;

    public String compile(String txtPath) throws IOException {
        logger.info("start to compile");
        this.txtPath = txtPath;

        parsePath();

        parseCommand();
        parseGuteventTag();
        parseLabelAddr(commandInfoList); // 标签及地址集合
        parseScriptBytesStr();
        parseJumpTableBytesStr();

        FileOutputStream fos = new FileOutputStream(gutPath);
        writeFile(fos);
        IOUtils.closeQuietly(fos);
        logger.info("compiled successfully.");

        FileInputStream is = new FileInputStream(new File(gutPath));
        String output = IOUtils.toString(is);
        IOUtils.closeQuietly(is);
        return output;
    }

    private void writeFile(FileOutputStream fos) throws IOException {
        byte jumpTableSize = (byte)(jumpTable.length*2);
        short fileSize = (short)(Short.BYTES + Byte.BYTES + jumpTableBytesStr.length()/2 + scriptBytesStr.length()/2);
        String fileSizeHex = ByteUtils.short2HexString(fileSize);

        logger.debug("fileSize={}, fileSizeHex={}.", fileSize, fileSizeHex);
        logger.debug("jumpTableBytesStr=[{}]", jumpTableBytesStr);
        logger.debug("scriptBytesStr=[{}]", scriptBytesStr);

        if (!(type == 0 && index == 0)) {
            fos.write(type);
            fos.write(index);
            String hexStr = "31313131313100CCCCCCCCCCCCCCCCCCCCCCCCCCCC00";
            writeHexString(fos, hexStr);
        }
        writeHexString(fos, fileSizeHex);
        fos.write(jumpTableSize/2);
        if (!StringUtils.isEmpty(jumpTableBytesStr)) {
            writeHexString(fos, jumpTableBytesStr);
        }
        writeHexString(fos, scriptBytesStr);
    }

    private void writeHexString(FileOutputStream fos, String hex) throws IOException {
        if (StringUtils.isEmpty(hex)) {
            throw new RuntimeException("illegal hex string");
        }
        if (hex.length()%2 != 0) {
            throw new RuntimeException("illegal hex string");
        }
        for (int i = 0; i < hex.length()/2; i++) {
            String hexValue = hex.substring(i*2, i*2+2);
            Integer integer = Integer.valueOf(hexValue, 16);
            fos.write(integer);
        }
    }

    private void parsePath() {
        int lastDot = txtPath.lastIndexOf('.');
        if (-1 == lastDot) {
            throw new RuntimeException("file path ["+txtPath+"] does not have an extension");
        }

        String fileExt = txtPath.substring(lastDot);
        logger.debug("fileExe=" + fileExt);
        gutPath = txtPath.substring(0, lastDot) + ".gut";
        logger.debug("gutPath=" + gutPath);

        String fileName = new File(txtPath).getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String[] typeAndIndex = fileName.split("-");
        if (typeAndIndex.length == 2) {
            type  = Byte.valueOf(typeAndIndex[0]);
            index = Byte.valueOf(typeAndIndex[1]);
        }
    }

    private void parseCommand() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(txtPath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "gbk");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<GutCommand> commandInfoList = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            if (line.startsWith("@")) {
                continue;
            }
            String command;
            String param = "";
            int firstSpace = line.indexOf(" ");
            if (firstSpace == -1) {
                command = line;
            } else {
                command = line.substring(0, firstSpace);
                param = line.substring(firstSpace).trim();
                // 去除脚本中的"符号，不知会不会产生什么问题，因为say的参数有两个，第一个是数字，第二个是含"的文本
                param = param.replace("\"", "");
            }

            logger.debug("command=`"+command+"` || param=`"+param+"`");
            commandInfoList.add(new GutCommand(command.toUpperCase(), param.trim()));
        }
        IOUtils.closeQuietly(bufferedReader);
        IOUtils.closeQuietly(inputStreamReader);
        IOUtils.closeQuietly(fileInputStream);

        this.commandInfoList = Collections.unmodifiableList(commandInfoList);
    }

    private void parseGuteventTag() {
        int maxGuteventNumber = 0; // 最大gutevent编号，影响到转移表的大小
        Map<String, Integer> guteventTagMap = new HashMap<>(); // gutevent的标签

        for (GutCommand ci : commandInfoList) {
            String name = ci.getName();
            if (GutCommandResource.GUTEVENT.equals(name)) {
                String param = ci.getParam();
                String tagNumber = param.split(" ")[0];
                int n = Integer.parseInt(tagNumber);
                if (n > 40) {
                    maxGuteventNumber = Math.max(n, maxGuteventNumber);
                }
                String tagName = param.split(" ")[1];
                guteventTagMap.put(tagName, n);
            }
        }

        logger.debug("maxGuteventNumber={}.", maxGuteventNumber);
        jumpTable = new short[maxGuteventNumber];
        this.guteventTag = Collections.unmodifiableMap(guteventTagMap);
    }

    /**
     * 解析出label所在的地址
     * @param commandInfoList 命令集合
     * @throws IOException e
     */
    private void parseLabelAddr(List<GutCommand> commandInfoList) throws IOException {
        Map<String, Integer> labelAddrMap = new HashMap<>(); // 普通标签

        int curBytes = 0;
        for (GutCommand ci : commandInfoList) {
            String name = ci.getName();
            if ("GUTEVENT".equals(name)) {
                continue;
            }

            if (name.endsWith(":")) {
                final String tagName = name.substring(0, name.length() - 1);
                labelAddrMap.put(tagName, (curBytes+1));
                continue;
            }
            String b = getBytesOfCommandInfo(ci, labelAddrMap, 0);
            curBytes += b.length()/2;
        }
        logger.debug("labelAddrMap: " + labelAddrMap);
        this.labelAddrMap = Collections.unmodifiableMap(labelAddrMap);
    }

    private void parseScriptBytesStr() throws IOException {
        StringBuilder scriptBytesStr = new StringBuilder();
        int curBytes = 0;
        for (GutCommand ci : commandInfoList) {
            String name = ci.getName();
            if ("GUTEVENT".equals(name)) {
                continue;
            }
            //  a tag
            if (name.endsWith(":")) {
                final String tagName = name.substring(0, name.length() - 1);
                if (guteventTag.containsKey(tagName)) {
                    jumpTable[guteventTag.get(tagName)-1] = (short)(curBytes+1);
                }
                continue;
            }
            String b = getBytesOfCommandInfo(ci, labelAddrMap, jumpTable.length);
            scriptBytesStr.append(b);
            curBytes += b.length()/2;
        }
        this.scriptBytesStr = scriptBytesStr.toString();
    }

    private void parseJumpTableBytesStr() {
        StringBuilder jumpTableStr = new StringBuilder();
        for (short j : jumpTable) {
            if (j != 0) {
                j = (short) ((jumpTable.length + 1) * 2 + j);
            }
            jumpTableStr.append(ByteUtils.short2HexString(j));
        }
        this.jumpTableBytesStr = jumpTableStr.toString();
    }

    /**
     * 获得最终的字节码形式的字符串
     * @param ci 命令
     * @param labelAddrMap l
     * @param jumpTableLength j
     * @throws IOException e
     */
    private String getBytesOfCommandInfo(GutCommand ci,
                                                Map<String, Integer> labelAddrMap,
                                                int jumpTableLength) throws IOException {
        String name = ci.getName();
        final String param = ci.getParam();
        String[] paramArr = param.split(" ");
        String commandCode = getCommandCode(name);
        if (commandCode == null) {
            throw new RuntimeException("unknown command exception:" + name);
        }

        Integer commandIndex = Integer.valueOf(commandCode, 16);
        String paramInShort = GutCommandResource.COMMAND_PARAM[commandIndex];


        StringBuilder binCodeInHexStr = new StringBuilder();
        char[] chars = paramInShort.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            switch (aChar) {
                case 'A': { // 直接地址
                    Integer integer = labelAddrMap.get(paramArr[i]);
                    if (integer != null) {
                        integer += (jumpTableLength + 1) * 2;
                    } else {
                        integer = 0;
                    }
                    binCodeInHexStr.append(ByteUtils.short2HexString(integer.shortValue()));
                }
                break;
                case 'C': { // 字符串
                    byte[] gbkstr = paramArr[i].getBytes("gbk");
                    binCodeInHexStr.append(ByteUtils.bytes2HexString(gbkstr)).append("00");
                }
                break;
                case 'E': { // 间接地址，该处只有gutevent命令使用到
                }
                break;
                case 'L': { // 4字节数
                    binCodeInHexStr.append(ByteUtils.int2HexString(Integer.valueOf(paramArr[i])));
                }
                break;
                case 'N': { // 2字节数
                    binCodeInHexStr.append(ByteUtils.short2HexString(Short.valueOf(paramArr[i])));
                }
                break;
                case 'U': { // 多个N, 目前仅buy使用到
                    for (String _p: paramArr) {
                        byte type = Byte.valueOf(_p.substring(0, _p.length()-3));
                        byte index = Byte.valueOf(_p.substring(_p.length()-3));
                        binCodeInHexStr.append(ByteUtils.byte2HexString(index));
                        binCodeInHexStr.append(ByteUtils.byte2HexString(type));
                    }
                    binCodeInHexStr.append("00");
                }
                break;
            }
        }

        logger.debug("command="+name+", param=" + param +
                ", command index=" + commandIndex + "/" + commandCode +
                ", paramInShort=" + paramInShort + ",binCode=" + binCodeInHexStr);
        return commandCode + binCodeInHexStr;
    }

    /**
     * 获取命令所对应的编号
     * @param commandName 命令名称
     * @return 命令编号 (in hex)
     */
    private String getCommandCode(String commandName) {
        if (StringUtils.isEmpty(commandName)) {
            throw new RuntimeException("unknown command exception: " + commandName);
        }
        String[] tagText = GutCommandResource.COMMAND_TEXT;
        for (int i = 0;i < tagText.length; i++) {
            String tag = tagText[i];
            if (commandName.toUpperCase().equals(tag.toUpperCase())) {
                return ByteUtils.byte2HexString((byte)i);
            }
        }

        return null;
    }
}
