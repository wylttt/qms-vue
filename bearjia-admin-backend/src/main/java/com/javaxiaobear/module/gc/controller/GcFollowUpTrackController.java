package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUpTrack;
import com.javaxiaobear.module.gc.service.IGcFollowUpTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 随访跟踪记录Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/followup/track")
public class GcFollowUpTrackController extends BaseController {

    @Autowired
    private IGcFollowUpTrackService followUpTrackService;

    /**
     * 查询随访跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('gc:track:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcFollowUpTrack track) {
        startPage();
        List<Map<String, Object>> list = followUpTrackService.selectTrackList(track);
        return getDataTable(list);
    }

    /**
     * 查询随访跟踪记录详情
     */
    @PreAuthorize("@ss.hasPermi('gc:track:query')")
    @GetMapping("/{trackId}")
    public AjaxResult getInfo(@PathVariable("trackId") Long trackId) {
        return success(followUpTrackService.selectTrackDetail(trackId));
    }

    /**
     * 新增随访跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('gc:track:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcFollowUpTrack track) {
        return toAjax(followUpTrackService.insertTrack(track));
    }

    /**
     * 修改随访跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('gc:track:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcFollowUpTrack track) {
        return toAjax(followUpTrackService.updateTrack(track));
    }

    /**
     * 删除随访跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('gc:track:remove')")
    @DeleteMapping("/{trackId}")
    public AjaxResult remove(@PathVariable Long trackId) {
        return toAjax(followUpTrackService.deleteTrackById(trackId));
    }

    /**
     * 批量删除随访跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('gc:track:remove')")
    @DeleteMapping("/batch/{trackIds}")
    public AjaxResult removeBatch(@PathVariable Long[] trackIds) {
        return toAjax(followUpTrackService.deleteTrackByIds(trackIds));
    }

    /**
     * 根据随访ID查询跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('gc:track:query')")
    @GetMapping("/followup/{followUpId}")
    public AjaxResult getByFollowUpId(@PathVariable Long followUpId) {
        List<Map<String, Object>> list = followUpTrackService.selectByFollowUpId(followUpId);
        return success(list);
    }

    /**
     * 导出随访跟踪记录列表
     */
    @PreAuthorize("@ss.hasPermi('gc:track:export')")
    @PostMapping("/export")
    public void export(GcFollowUpTrack track) {
        List<Map<String, Object>> list = followUpTrackService.selectTrackList(track);
        // TODO: 实现Excel导出逻辑
        // ExcelUtil<Map<String, Object>> util = new ExcelUtil<>(Map.class);
        // util.exportExcel(response, list, "随访跟踪记录数据");
    }
}
