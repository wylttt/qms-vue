package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcFollowUp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 随访记录Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Mapper
public interface GcFollowUpMapper {
    
    /**
     * 查询随访记录
     * 
     * @param followUpId 随访记录主键
     * @return 随访记录
     */
    public GcFollowUp selectFollowUpById(Long followUpId);
    
    /**
     * 查询随访记录列表
     * 
     * @param followUp 随访记录
     * @return 随访记录集合
     */
    public List<GcFollowUp> selectFollowUpList(GcFollowUp followUp);
    
    /**
     * 新增随访记录
     * 
     * @param followUp 随访记录
     * @return 结果
     */
    public int insertFollowUp(GcFollowUp followUp);
    
    /**
     * 修改随访记录
     * 
     * @param followUp 随访记录
     * @return 结果
     */
    public int updateFollowUp(GcFollowUp followUp);
    
    /**
     * 删除随访记录
     * 
     * @param followUpId 随访记录主键
     * @return 结果
     */
    public int deleteFollowUpById(Long followUpId);
    
    /**
     * 批量删除随访记录
     * 
     * @param followUpIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFollowUpByIds(Long[] followUpIds);
    
    /**
     * 根据居民ID查询随访记录
     * 
     * @param residentId 居民ID
     * @return 随访记录
     */
    public GcFollowUp selectFollowUpByResidentId(Long residentId);
    
    /**
     * 查询待随访列表
     * 
     * @param followUp 查询条件
     * @return 随访记录集合
     */
    public List<GcFollowUp> selectPendingFollowUpList(GcFollowUp followUp);
}
