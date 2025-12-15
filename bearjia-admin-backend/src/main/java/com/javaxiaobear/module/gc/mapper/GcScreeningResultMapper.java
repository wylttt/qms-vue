package com.javaxiaobear.module.gc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaxiaobear.module.gc.domain.entity.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningResultVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 筛查结果Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface GcScreeningResultMapper extends BaseMapper<GcScreeningResult> {

    /**
     * 查询筛查结果列表(关联居民、采血点、区域)
     * 
     * @param result 查询条件
     * @return 筛查结果列表
     */
    List<ScreeningResultVO> selectScreeningResultList(GcScreeningResult result);

    /**
     * 查询筛查结果详情(关联居民、采血点、区域)
     * 
     * @param resultId 结果ID
     * @return 筛查结果详情
     */
    ScreeningResultVO selectScreeningResultDetail(@Param("resultId") Long resultId);

    /**
     * 新增筛查结果
     * 
     * @param result 筛查结果
     * @return 影响行数
     */
    int insertScreeningResult(GcScreeningResult result);

    /**
     * 修改筛查结果
     * 
     * @param result 筛查结果
     * @return 影响行数
     */
    int updateScreeningResult(GcScreeningResult result);

    /**
     * 删除筛查结果
     * 
     * @param resultId 结果ID
     * @return 影响行数
     */
    int deleteScreeningResultById(@Param("resultId") Long resultId);

    /**
     * 批量删除筛查结果
     * 
     * @param resultIds 结果ID数组
     * @return 影响行数
     */
    int deleteScreeningResultByIds(@Param("resultIds") Long[] resultIds);

    /**
     * 更新审核状态
     * 
     * @param resultId 结果ID
     * @param reviewStatus 审核状态
     * @param reviewUserId 审核人ID
     * @param reviewUserName 审核人姓名
     * @param reviewTime 审核时间
     * @return 影响行数
     */
    int updateReviewStatus(@Param("resultId") Long resultId,
                          @Param("reviewStatus") String reviewStatus,
                          @Param("reviewUserId") Long reviewUserId,
                          @Param("reviewUserName") String reviewUserName,
                          @Param("reviewTime") Date reviewTime);

    /**
     * 根据预约ID查询筛查结果
     * 
     * @param appointmentId 预约ID
     * @return 筛查结果
     */
    GcScreeningResult selectByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 根据居民ID查询筛查结果列表
     * 
     * @param residentId 居民ID
     * @return 筛查结果列表
     */
    List<ScreeningResultVO> selectByResidentId(@Param("residentId") Long residentId);

    /**
     * 统计高风险人数
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 高风险人数
     */
    int countHighRisk(@Param("regionId") Long regionId,
                     @Param("regionLevel") Integer regionLevel,
                     @Param("startDate") Date startDate,
                     @Param("endDate") Date endDate);

    /**
     * 统计待审核结果数量
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @return 待审核数量
     */
    int countPendingReview(@Param("regionId") Long regionId,
                          @Param("regionLevel") Integer regionLevel);

    /**
     * 统计各风险等级人数
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Map<风险等级, 人数>
     */
    List<java.util.Map<String, Object>> countByRiskLevel(@Param("regionId") Long regionId,
                                                         @Param("regionLevel") Integer regionLevel,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);
}
