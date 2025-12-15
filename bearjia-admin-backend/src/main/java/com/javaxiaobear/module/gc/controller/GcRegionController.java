package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;
import com.javaxiaobear.module.gc.service.IGcRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行政区划Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/api/gc/region")
public class GcRegionController extends BaseController {
    
    @Autowired
    private IGcRegionService gcRegionService;
    
    /**
     * 查询行政区划列表
     */
    @PreAuthorize("@ss.hasPermi('gc:region:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcRegion gcRegion) {
        startPage();
        List<GcRegion> list = gcRegionService.selectGcRegionList(gcRegion);
        return getDataTable(list);
    }
    
    /**
     * 查询省级列表
     */
    @GetMapping("/provinces")
    public AjaxResult getProvinces() {
        List<GcRegion> list = gcRegionService.selectProvinces();
        return AjaxResult.success(list);
    }
    
    /**
     * 查询市级列表
     */
    @GetMapping("/cities")
    public AjaxResult getCities(@RequestParam Long provinceId) {
        List<GcRegion> list = gcRegionService.selectCitiesByProvinceId(provinceId);
        return AjaxResult.success(list);
    }
    
    /**
     * 查询区级列表
     */
    @GetMapping("/districts")
    public AjaxResult getDistricts(@RequestParam Long cityId) {
        List<GcRegion> list = gcRegionService.selectDistrictsByCityId(cityId);
        return AjaxResult.success(list);
    }
    
    /**
     * 查询街道级列表
     */
    @GetMapping("/streets")
    public AjaxResult getStreets(@RequestParam Long districtId) {
        List<GcRegion> list = gcRegionService.selectStreetsByDistrictId(districtId);
        return AjaxResult.success(list);
    }
    
    /**
     * 查询社区级列表
     */
    @GetMapping("/communities")
    public AjaxResult getCommunities(@RequestParam Long streetId) {
        List<GcRegion> list = gcRegionService.selectCommunitiesByStreetId(streetId);
        return AjaxResult.success(list);
    }
    
    /**
     * 构建区划树形结构
     */
    @GetMapping("/tree")
    public AjaxResult getTree(@RequestParam(required = false) Long rootId,
                              @RequestParam(required = false) Integer maxLevel) {
        List<RegionTreeVO> tree = gcRegionService.buildRegionTree(rootId, maxLevel);
        return AjaxResult.success(tree);
    }
    
    /**
     * 获取行政区划详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:region:query')")
    @GetMapping(value = "/{regionId}")
    public AjaxResult getInfo(@PathVariable("regionId") Long regionId) {
        return AjaxResult.success(gcRegionService.selectGcRegionByRegionId(regionId));
    }
    
    /**
     * 新增行政区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcRegion gcRegion) {
        return toAjax(gcRegionService.insertGcRegion(gcRegion));
    }
    
    /**
     * 修改行政区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcRegion gcRegion) {
        return toAjax(gcRegionService.updateGcRegion(gcRegion));
    }
    
    /**
     * 删除行政区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:remove')")
    @DeleteMapping("/{regionIds}")
    public AjaxResult remove(@PathVariable Long[] regionIds) {
        return toAjax(gcRegionService.deleteGcRegionByRegionIds(regionIds));
    }
    
    /**
     * 检查区划代码是否唯一
     */
    @GetMapping("/checkRegionCodeUnique")
    public AjaxResult checkRegionCodeUnique(@RequestParam String regionCode) {
        boolean unique = gcRegionService.checkRegionCodeUnique(regionCode);
        return AjaxResult.success(unique);
    }
}
