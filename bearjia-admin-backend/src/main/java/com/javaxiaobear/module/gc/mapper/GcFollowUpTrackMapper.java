package com.javaxiaobear.module.gc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUpTrack;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访跟踪记录Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface GcFollowUpTrackMapper extends BaseMapper<GcFollowUpTrack> {

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
    Map<String, Object> selectTrackDetail(@Param("trackId") Long trackId);

    /**
     * 新增随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 影响行数
     */
    int insertTrack(GcFollowUpTrack track);

    /**
     * 修改随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 影响行数
     */
    int updateTrack(GcFollowUpTrack track);

    /**
     * 删除随访跟踪记录
     * 
     * @param trackId 跟踪记录ID
     * @return 影响行数
     */
    int deleteTrackById(@Param("trackId") Long trackId);

    /**
     * 批量删除随访跟踪记录
     * 
     * @param trackIds 跟踪记录ID数组
     * @return 影响行数
     */
    int deleteTrackByIds(@Param("trackIds") Long[] trackIds);

    /**
     * 根据随访ID查询跟踪记录列表
     * 
     * @param followUpId 随访ID
     * @return 跟踪记录列表
     */
    List<Map<String, Object>> selectByFollowUpId(@Param("followUpId") Long followUpId);

    /**
     * 查询随访对象的最近一次跟踪记录
     * 
     * @param followUpId 随访ID
     * @return 跟踪记录
     */
    GcFollowUpTrack selectLatestByFollowUpId(@Param("followUpId") Long followUpId);

    /**
     * 统计随访次数
     * 
     * @param followUpId 随访ID
     * @return 随访次数
     */
    int countByFollowUpId(@Param("followUpId") Long followUpId);

    /**
     * 统计指定时间段内的随访次数
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 随访次数
     */
    int countVisits(@Param("regionId") Long regionId,
                   @Param("regionLevel") Integer regionLevel,
                   @Param("startDate") Date startDate,
                   @Param("endDate") Date endDate);
}
