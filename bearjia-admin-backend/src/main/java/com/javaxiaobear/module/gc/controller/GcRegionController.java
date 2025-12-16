package com.javaxiaobear.module.gc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;
import com.javaxiaobear.module.gc.service.IGcRegionService;

/**
 * 行政区划Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/gc/region")
public class GcRegionController extends BaseController
{
    @Autowired
    private IGcRegionService regionService;

    /**
     * 查询省列表
     */
    @GetMapping("/provinces")
    public AjaxResult listProvinces()
    {
        List<GcRegion> list = regionService.selectProvinceList();
        return success(list);
    }

    /**
     * 查询市列表
     */
    @GetMapping("/cities")
    public AjaxResult listCities(@RequestParam("provinceId") Long provinceId)
    {
        List<GcRegion> list = regionService.selectCityList(provinceId);
        return success(list);
    }

    /**
     * 查询区列表
     */
    @GetMapping("/districts")
    public AjaxResult listDistricts(@RequestParam("cityId") Long cityId)
    {
        List<GcRegion> list = regionService.selectDistrictList(cityId);
        return success(list);
    }

    /**
     * 查询街道/乡镇列表
     */
    @GetMapping("/streets")
    public AjaxResult listStreets(@RequestParam("districtId") Long districtId)
    {
        List<GcRegion> list = regionService.selectStreetList(districtId);
        return success(list);
    }

    /**
     * 查询社区/村列表
     */
    @GetMapping("/communities")
    public AjaxResult listCommunities(@RequestParam("streetId") Long streetId)
    {
        List<GcRegion> list = regionService.selectCommunityList(streetId);
        return success(list);
    }

    /**
     * 查询区划树形结构
     */
    @GetMapping("/tree")
    public AjaxResult tree(@RequestParam(value = "rootId", required = false) Long rootId,
                          @RequestParam(value = "maxLevel", required = false, defaultValue = "5") Integer maxLevel)
    {
        List<RegionTreeVO> tree = regionService.selectRegionTree(rootId, maxLevel);
        return success(tree);
    }

    /**
     * 获取区划列表
     */
    @PreAuthorize("@ss.hasPermi('gc:region:list')")
    @GetMapping("/list")
    public AjaxResult list(GcRegion region)
    {
        List<GcRegion> list = regionService.selectRegionList(region);
        return success(list);
    }

    /**
     * 获取区划详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:region:query')")
    @GetMapping(value = "/{regionId}")
    public AjaxResult getInfo(@PathVariable("regionId") Long regionId)
    {
        return success(regionService.selectRegionById(regionId));
    }

    /**
     * 新增区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:add')")
    @Log(title = "行政区划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcRegion region)
    {
        if (!regionService.checkRegionCodeUnique(region))
        {
            return error("新增区划'" + region.getRegionName() + "'失败,区划代码已存在");
        }

        // 验证区域层级
        if (region.getRegionLevel() == null || region.getRegionLevel() < 1 || region.getRegionLevel() > 5)
        {
            return error("区域层级必须在1-5之间");
        }

        region.setCreateBy(getUsername());
        return toAjax(regionService.insertRegion(region));
    }

    /**
     * 修改区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:edit')")
    @Log(title = "行政区划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcRegion region)
    {
        region.setUpdateBy(getUsername());
        return toAjax(regionService.updateRegion(region));
    }

    /**
     * 删除区划
     */
    @PreAuthorize("@ss.hasPermi('gc:region:remove')")
    @Log(title = "行政区划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{regionId}")
    public AjaxResult remove(@PathVariable Long regionId)
    {
        if (regionService.hasChildByRegionId(regionId))
        {
            return warn("存在下级区划,不允许删除");
        }

        if (regionService.checkRegionInUse(regionId))
        {
            return warn("该区划已被采血点或调查员引用,不允许删除");
        }

        return toAjax(regionService.deleteRegionById(regionId));
    }
}
