package com.kklive.entity.vo;

import com.kklive.entity.po.UserAction;

import java.util.List;

public class VideoInfoResultVo {
    private VideoInfoVo videoInfo;

    private List<UserAction> userActionList;

    public VideoInfoVo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfoVo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public List<UserAction> getUserActionList() {
        return userActionList;
    }

    public void setUserActionList(List<UserAction> userActionList) {
        this.userActionList = userActionList;
    }
}
