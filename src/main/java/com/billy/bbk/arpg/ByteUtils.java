package com.billy.bbk.arpg;

/**
 * @author lei.liu
 * @since 2019-06-12 13:57
 */
public final class ByteUtils {
    private ByteUtils() {}

    public static String int2HexString(int n) {
        return short2HexString((short) n) + short2HexString((short) (n >> 16));
    }

    public static String short2HexString(short b) {
        return byte2HexString((byte) (b)) + byte2HexString((byte) (b >> 8));
    }

    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = "0" + hex;
        }

        return hex.toUpperCase();
    }

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder r = new StringBuilder();

        for (byte b : bytes) {
            r.append(byte2HexString(b));
        }

        return r.toString();
    }
}
