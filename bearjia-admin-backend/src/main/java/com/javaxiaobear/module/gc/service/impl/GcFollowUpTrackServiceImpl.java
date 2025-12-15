package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUpTrack;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
import com.javaxiaobear.module.gc.service.IGcFollowUpTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访跟踪记录Service实现类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcFollowUpTrackServiceImpl implements IGcFollowUpTrackService {

    @Autowired
    private GcFollowUpTrackMapper followUpTrackMapper;

    @Autowired
    private GcFollowUpMapper followUpMapper;

    /**
     * 查询随访跟踪记录列表
     * 
     * @param track 查询条件
     * @return 随访跟踪记录列表
     */
    @Override
    public List<Map<String, Object>> selectTrackList(GcFollowUpTrack track) {
        return followUpTrackMapper.selectTrackList(track);
    }

    /**
     * 查询随访跟踪记录详情
     * 
     * @param trackId 跟踪记录ID
     * @return 随访跟踪记录详情
     */
    @Override
    public Map<String, Object> selectTrackDetail(Long trackId) {
        if (trackId == null) {
            throw new ServiceException("跟踪记录ID不能为空");
        }
        return followUpTrackMapper.selectTrackDetail(trackId);
    }

    /**
     * 新增随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTrack(GcFollowUpTrack track) {
        // 1. 验证必填字段
        validateTrack(track);

        // 2. 查询随访对象
        Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(track.getFollowUpId());
        if (followUp == null) {
            throw new ServiceException("随访对象不存在");
        }

        // 3. 检查随访对象状态
        String status = (String) followUp.get("follow_up_status");
        if ("2".equals(status)) {
            throw new ServiceException("随访对象已完成，不允许新增跟踪记录");
        }

        // 4. 设置默认值
        if (track.getVisitTime() == null) {
            track.setVisitTime(new Date());
        }

        // 5. 插入跟踪记录
        int rows = followUpTrackMapper.insertTrack(track);

        // 6. 更新随访对象的随访次数和最后随访时间
        if (rows > 0) {
            int actualVisitCount = followUpTrackMapper.countByFollowUpId(track.getFollowUpId());
            followUpMapper.updateVisitCount(
                track.getFollowUpId(),
                actualVisitCount,
                track.getVisitTime()
            );

            // 7. 如果是第一次随访，更新随访状态为"随访中"
            if (actualVisitCount == 1 && "0".equals(status)) {
                followUpMapper.updateFollowUpStatus(
                    track.getFollowUpId(),
                    "1", // 随访中
                    null,
                    null
                );
            }
        }

        return rows;
    }

    /**
     * 修改随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTrack(GcFollowUpTrack track) {
        // 1. 验证跟踪记录ID
        if (track.getTrackId() == null) {
            throw new ServiceException("跟踪记录ID不能为空");
        }

        // 2. 查询原跟踪记录
        Map<String, Object> oldTrack = followUpTrackMapper.selectTrackDetail(track.getTrackId());
        if (oldTrack == null) {
            throw new ServiceException("跟踪记录不存在");
        }

        // 3. 获取关联的随访对象
        Long followUpId = (Long) oldTrack.get("follow_up_id");
        Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(followUpId);
        if (followUp != null) {
            String status = (String) followUp.get("follow_up_status");
            if ("2".equals(status)) {
                throw new ServiceException("随访对象已完成，不允许修改跟踪记录");
            }
        }

        // 4. 更新跟踪记录
        return followUpTrackMapper.updateTrack(track);
    }

    /**
     * 删除随访跟踪记录
     * 
     * @param trackId 跟踪记录ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrackById(Long trackId) {
        // 1. 验证跟踪记录ID
        if (trackId == null) {
            throw new ServiceException("跟踪记录ID不能为空");
        }

        // 2. 查询跟踪记录
        Map<String, Object> track = followUpTrackMapper.selectTrackDetail(trackId);
        if (track == null) {
            throw new ServiceException("跟踪记录不存在");
        }

        // 3. 获取关联的随访对象
        Long followUpId = (Long) track.get("follow_up_id");
        Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(followUpId);
        if (followUp != null) {
            String status = (String) followUp.get("follow_up_status");
            if ("2".equals(status)) {
                throw new ServiceException("随访对象已完成，不允许删除跟踪记录");
            }
        }

        // 4. 删除跟踪记录
        int rows = followUpTrackMapper.deleteTrackById(trackId);

        // 5. 更新随访对象的随访次数
        if (rows > 0 && followUpId != null) {
            int actualVisitCount = followUpTrackMapper.countByFollowUpId(followUpId);
            
            // 查询最近一次跟踪记录
            GcFollowUpTrack latestTrack = followUpTrackMapper.selectLatestByFollowUpId(followUpId);
            Date lastVisitTime = latestTrack != null ? latestTrack.getVisitTime() : null;
            
            followUpMapper.updateVisitCount(
                followUpId,
                actualVisitCount,
                lastVisitTime
            );

            // 6. 如果删除后无跟踪记录，恢复随访状态为"待随访"
            if (actualVisitCount == 0 && followUp != null) {
                String followUpStatus = (String) followUp.get("follow_up_status");
                if ("1".equals(followUpStatus)) {
                    followUpMapper.updateFollowUpStatus(
                        followUpId,
                        "0", // 待随访
                        null,
                        null
                    );
                }
            }
        }

        return rows;
    }

    /**
     * 批量删除随访跟踪记录
     * 
     * @param trackIds 跟踪记录ID数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrackByIds(Long[] trackIds) {
        if (trackIds == null || trackIds.length == 0) {
            throw new ServiceException("跟踪记录ID不能为空");
        }

        // 逐个验证并删除
        for (Long trackId : trackIds) {
            deleteTrackById(trackId);
        }

        return trackIds.length;
    }

    /**
     * 根据随访ID查询跟踪记录列表
     * 
     * @param followUpId 随访ID
     * @return 跟踪记录列表
     */
    @Override
    public List<Map<String, Object>> selectByFollowUpId(Long followUpId) {
        if (followUpId == null) {
            throw new ServiceException("随访ID不能为空");
        }
        return followUpTrackMapper.selectByFollowUpId(followUpId);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证随访跟踪记录必填字段
     */
    private void validateTrack(GcFollowUpTrack track) {
        if (track.getFollowUpId() == null) {
            throw new ServiceException("随访ID不能为空");
        }
        if (track.getVisitMethod() == null || track.getVisitMethod().trim().isEmpty()) {
            throw new ServiceException("随访方式不能为空");
        }
        if (track.getVisitContent() == null || track.getVisitContent().trim().isEmpty()) {
            throw new ServiceException("随访内容不能为空");
        }
    }
}
