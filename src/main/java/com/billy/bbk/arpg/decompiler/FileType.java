package com.billy.bbk.arpg.decompiler;

import com.billy.bbk.arpg.BbkException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author lei.liu
 * @since 2019-06-12 11:46
 */
public enum FileType {
    GUT(1, "剧情脚本")
    ,MAP(2, "地图资源")
    ,ARS(3, "角色资源")
    ,MRS(4, "魔法资源")
    ,SRS(5, "特效资源")
    ,GRS(6, "道具资源")
    ,TIL(7, "tile资源")
    ,ACP(8, "角色图片")
    ,GDP(9, "道具图片")
    ,GGJ(10, "特效图片")
    ,PIC(11, "杂类图片")
    ,MLR(12, "链资源")
    ;

    public final int type;
    public final String info;

    FileType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public static boolean contains(String type) {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList()).contains(type);
    }


    public static FileType valueByType(int type) {
        for (FileType fileTypeEnum : values()) {
            if (fileTypeEnum.type == type) {
                return fileTypeEnum;
            }
        }
        throw new BbkException("unknown file type: " + type);
    }

}
