package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcFollowUpTrack;

import java.util.List;

/**
 * 随访跟踪Service接口
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcFollowUpTrackService {
    /**
     * 查询随访跟踪列表
     *
     * @param followUpTrack 随访跟踪
     * @return 随访跟踪集合
     */
    List<GcFollowUpTrack> selectFollowUpTrackList(GcFollowUpTrack followUpTrack);

    /**
     * 查询随访跟踪详情列表（包含随访对象和居民信息）
     *
     * @param followUpTrack 随访跟踪
     * @return 随访跟踪集合
     */
    List<GcFollowUpTrack> selectFollowUpTrackDetailList(GcFollowUpTrack followUpTrack);

    /**
     * 查询随访跟踪详情
     *
     * @param trackId 随访跟踪主键
     * @return 随访跟踪
     */
    GcFollowUpTrack selectFollowUpTrackById(Long trackId);

    /**
     * 根据随访对象ID查询跟踪记录
     *
     * @param followUpId 随访对象ID
     * @return 跟踪记录列表
     */
    List<GcFollowUpTrack> selectTracksByFollowUpId(Long followUpId);

    /**
     * 新增随访跟踪
     *
     * @param followUpTrack 随访跟踪
     * @return 结果
     */
    int insertFollowUpTrack(GcFollowUpTrack followUpTrack);

    /**
     * 修改随访跟踪
     *
     * @param followUpTrack 随访跟踪
     * @return 结果
     */
    int updateFollowUpTrack(GcFollowUpTrack followUpTrack);

    /**
     * 批量删除随访跟踪
     *
     * @param trackIds 需要删除的随访跟踪主键集合
     * @return 结果
     */
    int deleteFollowUpTrackByIds(Long[] trackIds);

    /**
     * 删除随访跟踪信息
     *
     * @param trackId 随访跟踪主键
     * @return 结果
     */
    int deleteFollowUpTrackById(Long trackId);
}
