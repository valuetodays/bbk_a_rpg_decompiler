package com.billy.bbk.arpg.decompiler.lib;

import com.billy.bbk.arpg.ByteUtils;
import com.billy.bbk.arpg.decompiler.FileType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lei.liu
 * @since 2019-06-12 15:11
 */
public class LibDecompiler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final long SEGMENT_OFFSET_RESOURCE_DEFINITION = 0x10; // 资源定义段
    private static final long SEGMENT_OFFSET_RESOURCE_JUMP = 0x2000; // 资源地址转移段
    private static final long SIZE_PER_SEGMENT = 0x4000; // 资源地址段开始

    private boolean enableDebug = true;

    private String libPath;
    private String targetDirectoryPath;
    private RandomAccessFile raf; // 文件句柄

    private List<int[]> resourceDefinition = new ArrayList<>();   // 三个字节，分别是资源id、type、index；
    private List<int[]> resourceJump = new ArrayList<>();         // 三个字节，分别是段号、相对4000h开始的资源段的偏移段的标志

    public LibDecompiler(String libPath, String targetDirectory) {
        this.libPath = libPath;
        this.targetDirectoryPath = targetDirectory;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public int doDecompile() throws IOException {
        File targetDirectory = new File(targetDirectoryPath);
        FileUtils.deleteDirectory(targetDirectory);
        FileUtils.forceMkdir(targetDirectory);
        Exception exceptionToThrow = null;
        try {
            logger.debug("to read file: " + libPath);
            raf = new RandomAccessFile(libPath, "r");
            checkMagicNumber();
            if (enableDebug) {
                debugSegmentInfo();
            }
            readResourceDefinition();
            if (enableDebug) {
                debugResourceDefinition();
            }
            readResourceJump();
            if (enableDebug) {
                debugResourceJump();
            }
            decompileFile();
        } catch (Exception e) {
            exceptionToThrow = e;
        } finally {
            IOUtils.closeQuietly(raf);
        }
        if (exceptionToThrow != null) {
            throw new RuntimeException(exceptionToThrow.getMessage(), exceptionToThrow);
        }
        return 0;
    }

    private void debugSegmentInfo() throws IOException {
        logger.debug("== segmentInfo ==");
        long length = raf.length();

        for (long i = 0; i < (length / SIZE_PER_SEGMENT); i++) {
            raf.seek(Math.min(i * SIZE_PER_SEGMENT, length));
            byte[] segmentHeaderByteArr = new byte[16];
            int segmentHeaderReadBytes = raf.read(segmentHeaderByteArr);
            if (segmentHeaderByteArr.length != segmentHeaderReadBytes) {
                throw new IOException("exception when retrieve segment");
            }
            int high = segmentHeaderByteArr[12] & 0XFF;
            int low = segmentHeaderByteArr[13] & 0XFF;

            String fileType = ""
                    + (char)segmentHeaderByteArr[0]
                    + (char)segmentHeaderByteArr[1]
                    + (char)segmentHeaderByteArr[2];
            logger.debug("header #{}: {} with {} bytes",
                    (i+1),
                    fileType,
                    (Integer.parseInt(Integer.toHexString(low) + Integer.toHexString(high), 16))
            );
            if (!FileType.contains(fileType)) {
                logger.warn("unknown file type: {}.", fileType);
            }
        }

    }

    private void debugResourceJump() {
        logger.debug("== resourceJump ==");
        for (int i = 0; i < resourceJump.size(); i++) {
            int[] bytes = resourceJump.get(i);
            logger.debug(" (" + (i + 1) + "/" + resourceJump.size() + ") " + bytes[0] + " " + bytes[1] + " " + bytes[2]);
        }
    }

    private void debugResourceDefinition() {
        logger.debug("== resourceDefinition ==");
        for (int i = 0; i < resourceDefinition.size(); i++) {
            int[] bytes = resourceDefinition.get(i);
            logger.debug("  (" + (i + 1) + "/" + resourceDefinition.size() + ") " + bytes[0] + " " + bytes[1] + " " + bytes[2]);
        }
    }

    private void decompileFile() throws IOException {
        for (int i = 0; i < resourceDefinition.size(); i++) {
            int[] resourceDefinitionArr = resourceDefinition.get(i);
//                logger.debug("parse resource: " + resourceDefinitionArr[0]);
            int resourceDefinitionType = resourceDefinitionArr[1];
            int resourceDefinitionIndex = resourceDefinitionArr[2];
            String resourceDefinitionStr = resourceDefinitionType + "-" + resourceDefinitionIndex;

            {
                if (enableDebug) {
                    // for debug start
                    int[] resourceJumpByteArr = resourceJump.get(i);
                    int segmentNumber = resourceJumpByteArr[0];
                    int resourceJumpHigh = resourceJumpByteArr[1];
                    int resourceJumpLow = resourceJumpByteArr[2];
                    String resourceAddressStr =
                            int2HexString((int) (segmentNumber * SIZE_PER_SEGMENT))
                                    + "+0x" + ByteUtils.byte2HexString((byte) resourceJumpLow)
                                    + ByteUtils.byte2HexString((byte) resourceJumpHigh);
                    logger.debug(" | " + resourceDefinitionStr + " --> " + resourceAddressStr);
                    // for debug ends
                }
            }

            long addressStart = getStartAddressOfResource(i);
            logger.debug("addressStart=" + Integer.toHexString((int)addressStart));
            long addressEnd = getEndAddressOfResource(i);
            logger.debug("addressEnd=" + Integer.toHexString((int)addressEnd));
            if ((addressEnd - addressStart) > 0) {
                logger.debug(i + "->" + addressEnd + "-" + addressStart + "=" + (addressEnd - addressStart) + "");
            } else {
                logger.error(i + " -> " + " error end and start");
            }

            int dataLength = (int) (addressEnd - addressStart);
            int[] resourceDataArr = new int[dataLength];
            raf.seek(addressStart);
            int tmpCnt = 0;
            while (tmpCnt < dataLength) {
                resourceDataArr[tmpCnt++] = raf.read() & 0xFF;
            }

            // write to file start
            FileType fileType = FileType.valueByType(resourceDefinitionArr[0]);
            File fileTarget = new File(targetDirectoryPath + File.separator + resourceDefinitionStr + "." + fileType.name());
            OutputStream out = null;
            try {
                out = new FileOutputStream(fileTarget, false);
                for (int resourceData : resourceDataArr) { // 循环保存吧，^_^
                    out.write(resourceData);
                }
                out.flush();
            } finally {
                IOUtils.closeQuietly(out);
            }
            // write to file end

        }
    }

    /**
     使用jump来计算该资源的长度
     *
     * @param id 资源id
     */
    private long getEndAddressOfResource(int id) throws IOException {
        int[] nextInSegment = getNextInSegment(id);
        if (nextInSegment == null) { // id是当前段的最后一条
            int[] resourceJumpByteArrLast = resourceJump.get(id);
            int lastSegmentNumber = resourceJumpByteArrLast[0];
            long segmentEndPointer = lastSegmentNumber * SIZE_PER_SEGMENT + 0x0c;
            long old = raf.getFilePointer();
            raf.seek(segmentEndPointer);
            int high = raf.read();
            int low = raf.read();
            raf.seek(old);

            return lastSegmentNumber * SIZE_PER_SEGMENT
                    + (Integer.parseInt(Integer.toHexString(low) + Integer.toHexString(high), 16))
                    ;
        }

        return getStartAddressOfResource(nextInSegment);
    }

    /**
     * 找到同段中当前资源的下一个资源
     *
     * 思路是：
     *   先查找到当前资源的段号、高位、低位，在所有转移段中查找同段的但是低位大于等于当前资源且高位大于当前资源的所有资源
     *   再按低位从小到大排序，得到结果
     *   若结果有数据，结果中第一条数据就是想要的数据
     *   若没有结果，则说明当前结果就是同段中最后一个资源
     *
     * @param id 资源序号
     * @return 下个资源转移段字节信息
     */
    private int[] getNextInSegment(int id) {
        int[] resourceJumpByteArr = resourceJump.get(id);
        int segmentNumber = resourceJumpByteArr[0];
        int resourceJumpHigh = resourceJumpByteArr[1];
        int resourceJumpLow = resourceJumpByteArr[2];

        List<int[]> nextList = resourceJump.stream()
                .filter(e -> e[0] == segmentNumber)
                .filter(e -> e[2] >= resourceJumpLow && e[1] > resourceJumpHigh)
                .sorted(Comparator.comparingInt(e -> e[2])).collect(Collectors.toList());
        if (nextList.isEmpty()) {
            return null;
        }

        return nextList.get(0);
    }

    private long getStartAddressOfResource(int resourceIndex) {
        return getStartAddressOfResource(resourceJump.get(resourceIndex));
    }

    private long getStartAddressOfResource(int[] resourceJumpArr) {
        int segmentNumber = resourceJumpArr[0];
        int resourceJumpHigh = resourceJumpArr[1];
        int resourceJumpLow = resourceJumpArr[2];
        // low 要左移1个字节（即8位）
        long address = segmentNumber * SIZE_PER_SEGMENT + ((resourceJumpLow & 0xFF) << 8) + (resourceJumpHigh & 0xFF);
        logger.debug("address=" + Integer.toHexString((int)(address)));
        return address;
    }

    private static String int2HexString(int n) {
        return short2HexString((short) (n >> 16)) + short2HexString((short) n);
    }
    private static String short2HexString(short b) {
        return ByteUtils.byte2HexString((byte) (b >> 8)) + ByteUtils.byte2HexString((byte) (b));
    }

    private void readResourceJump() throws IOException {
        raf.seek(SEGMENT_OFFSET_RESOURCE_JUMP);
        while (true) {
            int[] resourceJumpByteArr = new int[3];
            resourceJumpByteArr[0] = (raf.read() & 0xFF);
            resourceJumpByteArr[1] = (raf.read() & 0xFF);
            resourceJumpByteArr[2] = (raf.read() & 0xFF);

            if (resourceJumpByteArr[0] == 0xFF) {
                break;
            }

            resourceJump.add(resourceJumpByteArr);
        }
    }

    private void readResourceDefinition() throws IOException {
        raf.seek(SEGMENT_OFFSET_RESOURCE_DEFINITION);
        while (true) {
            int[] resourceByteArr = new int[3];
            resourceByteArr[0] = (raf.read() & 0xFF);
            resourceByteArr[1] = (raf.read() & 0xFF);
            resourceByteArr[2] = (raf.read() & 0xFF);

            if (resourceByteArr[0] == 0xFF) {
                break;
            }
            if (resourceByteArr[0] > 0x10) {
                throw new RuntimeException("illegal resource identifier : " + resourceByteArr[0]);
            }
            resourceDefinition.add(resourceByteArr);
        }
    }

    private void checkMagicNumber() throws IOException {
        raf.seek(BigDecimal.ZERO.longValue());
        byte[] magicNumberByteArr = new byte[3];
        int readCount = raf.read(magicNumberByteArr, 0, magicNumberByteArr.length);
        if (readCount != magicNumberByteArr.length) {
            throw new IOException("exception when retrieve magicNumber");
        }
        byte[] magicNumberExcepted = new byte[]{0x4C, 0x49, 0x42};
        if (!Objects.deepEquals(magicNumberByteArr, magicNumberExcepted)) {
            throw new IOException("exception when retrieve magicNumber");
        }
        logger.debug("legal lib file");
    }

}
