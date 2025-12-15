package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcFollowUpTrack;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 随访跟踪记录Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Mapper
public interface GcFollowUpTrackMapper {
    
    /**
     * 查询随访跟踪记录
     * 
     * @param trackRecordId 随访跟踪记录主键
     * @return 随访跟踪记录
     */
    public GcFollowUpTrack selectTrackById(Long trackRecordId);
    
    /**
     * 查询随访跟踪记录列表
     * 
     * @param track 随访跟踪记录
     * @return 随访跟踪记录集合
     */
    public List<GcFollowUpTrack> selectTrackList(GcFollowUpTrack track);
    
    /**
     * 新增随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 结果
     */
    public int insertTrack(GcFollowUpTrack track);
    
    /**
     * 修改随访跟踪记录
     * 
     * @param track 随访跟踪记录
     * @return 结果
     */
    public int updateTrack(GcFollowUpTrack track);
    
    /**
     * 删除随访跟踪记录
     * 
     * @param trackRecordId 随访跟踪记录主键
     * @return 结果
     */
    public int deleteTrackById(Long trackRecordId);
    
    /**
     * 批量删除随访跟踪记录
     * 
     * @param trackRecordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTrackByIds(Long[] trackRecordIds);
    
    /**
     * 根据随访ID查询跟踪记录列表
     * 
     * @param followUpId 随访ID
     * @return 随访跟踪记录集合
     */
    public List<GcFollowUpTrack> selectTrackByFollowUpId(Long followUpId);
}
