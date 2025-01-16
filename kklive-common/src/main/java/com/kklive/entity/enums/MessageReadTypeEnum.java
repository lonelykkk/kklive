package com.kklive.entity.enums;


public enum MessageReadTypeEnum {
    NO_READ(0, "未读"),
    READ(1, "已读");
    private Integer type;
    private String desc;

    MessageReadTypeEnum(Integer status, String desc) {
        this.type = status;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static MessageReadTypeEnum getByStatus(Integer status) {
        for (MessageReadTypeEnum statusEnum : MessageReadTypeEnum.values()) {
            if (statusEnum.getType().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }
}