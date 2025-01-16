package com.kklive.service;

import com.kklive.entity.po.UserVideoSeries;
import com.kklive.entity.query.UserVideoSeriesQuery;
import com.kklive.entity.vo.PaginationResultVO;

import java.util.List;


/**
 * 用户视频序列归档 业务接口
 */
public interface UserVideoSeriesService {

    /**
     * 根据条件查询列表
     */
    List<UserVideoSeries> findListByParam(UserVideoSeriesQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(UserVideoSeriesQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery param);

    /**
     * 新增
     */
    Integer add(UserVideoSeries bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<UserVideoSeries> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<UserVideoSeries> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(UserVideoSeries bean, UserVideoSeriesQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(UserVideoSeriesQuery param);

    /**
     * 根据SeriesId查询对象
     */
    UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId);


    /**
     * 根据SeriesId修改
     */
    Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean, Integer seriesId);


    /**
     * 根据SeriesId删除
     */
    Integer deleteUserVideoSeriesBySeriesId(Integer seriesId);


    void saveUserVideoSeries(UserVideoSeries bean, String videoIds);

    void changeVideoSeriesSort(String userId, String seriesIds);

    void saveSeriesVideo(String userId, Integer seriesId, String videoIds);

    void delSeriesVideo(String userId, Integer seriesId, String videoId);

    List<UserVideoSeries> getUserAllSeries(String userId);

    void delVideoSeries(String userId, Integer seriesId);

    List<UserVideoSeries> findListWithVideoList(UserVideoSeriesQuery query);
}