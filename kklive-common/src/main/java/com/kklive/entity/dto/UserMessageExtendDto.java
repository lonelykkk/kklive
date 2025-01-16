package com.kklive.entity.dto;

public class UserMessageExtendDto {
    private String messageContent;

    private String messageContentReply;

    //审核状态
    private Integer auditStatus;

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageContentReply() {
        return messageContentReply;
    }

    public void setMessageContentReply(String messageContentReply) {
        this.messageContentReply = messageContentReply;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}
