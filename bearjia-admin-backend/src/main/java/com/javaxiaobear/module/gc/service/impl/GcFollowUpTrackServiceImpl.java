package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.GcFollowUpTrack;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
import com.javaxiaobear.module.gc.service.IGcFollowUpTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 随访跟踪Service业务层处理
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcFollowUpTrackServiceImpl implements IGcFollowUpTrackService {
    @Autowired
    private GcFollowUpTrackMapper followUpTrackMapper;

    /**
     * 查询随访跟踪列表
     *
     * @param followUpTrack 随访跟踪
     * @return 随访跟踪
     */
    @Override
    public List<GcFollowUpTrack> selectFollowUpTrackList(GcFollowUpTrack followUpTrack) {
        return followUpTrackMapper.selectFollowUpTrackList(followUpTrack);
    }

    /**
     * 查询随访跟踪详情列表（包含随访对象和居民信息）
     *
     * @param followUpTrack 随访跟踪
     * @return 随访跟踪集合
     */
    @Override
    public List<GcFollowUpTrack> selectFollowUpTrackDetailList(GcFollowUpTrack followUpTrack) {
        return followUpTrackMapper.selectFollowUpTrackDetailList(followUpTrack);
    }

    /**
     * 查询随访跟踪详情
     *
     * @param trackId 随访跟踪主键
     * @return 随访跟踪
     */
    @Override
    public GcFollowUpTrack selectFollowUpTrackById(Long trackId) {
        return followUpTrackMapper.selectFollowUpTrackById(trackId);
    }

    /**
     * 根据随访对象ID查询跟踪记录
     *
     * @param followUpId 随访对象ID
     * @return 跟踪记录列表
     */
    @Override
    public List<GcFollowUpTrack> selectTracksByFollowUpId(Long followUpId) {
        return followUpTrackMapper.selectTracksByFollowUpId(followUpId);
    }

    /**
     * 新增随访跟踪
     *
     * @param followUpTrack 随访跟踪
     * @return 结果
     */
    @Override
    public int insertFollowUpTrack(GcFollowUpTrack followUpTrack) {
        return followUpTrackMapper.insertFollowUpTrack(followUpTrack);
    }

    /**
     * 修改随访跟踪
     *
     * @param followUpTrack 随访跟踪
     * @return 结果
     */
    @Override
    public int updateFollowUpTrack(GcFollowUpTrack followUpTrack) {
        return followUpTrackMapper.updateFollowUpTrack(followUpTrack);
    }

    /**
     * 批量删除随访跟踪
     *
     * @param trackIds 需要删除的随访跟踪主键
     * @return 结果
     */
    @Override
    public int deleteFollowUpTrackByIds(Long[] trackIds) {
        return followUpTrackMapper.deleteFollowUpTrackByIds(trackIds);
    }

    /**
     * 删除随访跟踪信息
     *
     * @param trackId 随访跟踪主键
     * @return 结果
     */
    @Override
    public int deleteFollowUpTrackById(Long trackId) {
        return followUpTrackMapper.deleteFollowUpTrackById(trackId);
    }
}
