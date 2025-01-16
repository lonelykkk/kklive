package com.kklive.entity.enums;


import com.kklive.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;

public enum FileTypeEnum {
    IMAGE(0, new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"}, "图片"),
    VIDEO(0, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"}, "视频");

    private Integer type;
    private String[] suffixArray;
    private String desc;

    FileTypeEnum(Integer type, String[] suffixArray, String desc) {
        this.type = type;
        this.suffixArray = suffixArray;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String[] getSuffixArray() {
        return suffixArray;
    }

    public void setSuffixArray(String[] suffixArray) {
        this.suffixArray = suffixArray;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static FileTypeEnum getByType(Integer type) {
        if (null == type) {
            return null;
        }
        for (FileTypeEnum typeEnum : FileTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static FileTypeEnum getBySuffix(String suffix) {
        if (StringTools.isEmpty(suffix)) {
            return null;
        }
        for (FileTypeEnum typeEnum : FileTypeEnum.values()) {
            if (ArrayUtils.contains(typeEnum.getSuffixArray(), suffix)) {
                return typeEnum;
            }
        }
        return null;
    }
}
