package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.javaxiaobear.module.gc.domain.entity.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskVO;
import com.javaxiaobear.module.gc.domain.vo.SubRegionProgressVO;

/**
 * 任务Mapper接口
 * 
 * @author javaxiaobear
 */
public interface GcTaskMapper {
    
    /**
     * 查询任务列表
     * 
     * @param task 任务信息
     * @return 任务集合
     */
    List<TaskVO> selectTaskList(GcTask task);
    
    /**
     * 根据任务ID查询任务信息
     * 
     * @param taskId 任务ID
     * @return 任务信息
     */
    GcTask selectTaskById(Long taskId);
    
    /**
     * 根据任务编号查询任务信息
     * 
     * @param taskCode 任务编号
     * @return 任务信息
     */
    GcTask selectTaskByCode(String taskCode);
    
    /**
     * 新增任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int insertTask(GcTask task);
    
    /**
     * 修改任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int updateTask(GcTask task);
    
    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int deleteTaskById(Long taskId);
    
    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务ID
     * @return 结果
     */
    int deleteTaskByIds(Long[] taskIds);
    
    /**
     * 统计指定区域的完成数量（问卷类型）
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 完成数量
     */
    int countQuestionnaireByRegion(@Param("regionId") Long regionId, 
                                     @Param("regionLevel") Integer regionLevel,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);
    
    /**
     * 统计指定区域的完成数量（采血类型）
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 完成数量
     */
    int countBloodByRegion(@Param("regionId") Long regionId,
                           @Param("regionLevel") Integer regionLevel,
                           @Param("startDate") String startDate,
                           @Param("endDate") String endDate);
    
    /**
     * 查询下级区域进度列表
     * 
     * @param taskId 任务ID
     * @return 下级区域进度列表
     */
    List<SubRegionProgressVO> selectSubRegionProgress(Long taskId);
}
