package com.kklive.entity.vo;

public class VideoStatusCountInfoVO {
    private Integer auditPassCount;
    private Integer auditFailCount;
    private Integer inProgress;

    public Integer getAuditPassCount() {
        return auditPassCount;
    }

    public void setAuditPassCount(Integer auditPassCount) {
        this.auditPassCount = auditPassCount;
    }

    public Integer getAuditFailCount() {
        return auditFailCount;
    }

    public void setAuditFailCount(Integer auditFailCount) {
        this.auditFailCount = auditFailCount;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }
}
