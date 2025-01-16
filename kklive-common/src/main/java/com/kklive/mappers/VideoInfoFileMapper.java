package com.kklive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 视频文件信息 数据库操作接口
 */
public interface VideoInfoFileMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据FileId更新
	 */
	 Integer updateByFileId(@Param("bean") T t,@Param("fileId") String fileId);


	/**
	 * 根据FileId删除
	 */
	 Integer deleteByFileId(@Param("fileId") String fileId);


	/**
	 * 根据FileId获取对象
	 */
	 T selectByFileId(@Param("fileId") String fileId);


	/**
	 * 根据VideoIdAndFileIndex更新
	 */
	 Integer updateByVideoIdAndFileIndex(@Param("bean") T t,@Param("videoId") String videoId,@Param("fileIndex") Integer fileIndex);


	/**
	 * 根据VideoIdAndFileIndex删除
	 */
	 Integer deleteByVideoIdAndFileIndex(@Param("videoId") String videoId,@Param("fileIndex") Integer fileIndex);


	/**
	 * 根据VideoIdAndFileIndex获取对象
	 */
	 T selectByVideoIdAndFileIndex(@Param("videoId") String videoId,@Param("fileIndex") Integer fileIndex);


}
