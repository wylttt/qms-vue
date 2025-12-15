package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcFollowUpTrack;

import java.util.List;
import java.util.Map;

/**
 * 随访跟踪记录Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcFollowUpTrackService {

    /**
     * 查询随访跟踪记录列表
     * 
     * @param track 查询条件
     * @return 随访跟踪记录列表
     */
    List<Map<String, Object>> selectTrackList(GcFollowUpTrack track);

    /**
     * 查询随访跟踪记录详情
     * 
     * @param trackId 跟踪记录ID
     * @return 随访跟踪记录详情
     */
    Map<String, Object> selectTrackDetail(Long trackId);

    /**
     * 新增随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 新增结果
     */
    int insertTrack(GcFollowUpTrack track);

    /**
     * 修改随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 修改结果
     */
    int updateTrack(GcFollowUpTrack track);

    /**
     * 删除随访跟踪记录
     * 
     * @param trackId 跟踪记录ID
     * @return 删除结果
     */
    int deleteTrackById(Long trackId);

    /**
     * 批量删除随访跟踪记录
     * 
     * @param trackIds 跟踪记录ID数组
     * @return 删除结果
     */
    int deleteTrackByIds(Long[] trackIds);

    /**
     * 根据随访ID查询跟踪记录列表
     * 
     * @param followUpId 随访ID
     * @return 跟踪记录列表
     */
    List<Map<String, Object>> selectByFollowUpId(Long followUpId);
}
