package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.R;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcFollowUpTrack;
import com.javaxiaobear.module.gc.service.IGcFollowUpTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访跟踪Controller
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/gc/followup/track")
public class GcFollowUpTrackController extends BaseController {
    @Autowired
    private IGcFollowUpTrackService followUpTrackService;

    /**
     * 查询随访跟踪列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcFollowUpTrack followUpTrack) {
        startPage();
        List<GcFollowUpTrack> list = followUpTrackService.selectFollowUpTrackList(followUpTrack);
        return getDataTable(list);
    }

    /**
     * 查询随访跟踪详情列表（包含随访对象和居民信息）
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:list')")
    @GetMapping("/list/detail")
    public TableDataInfo listDetail(GcFollowUpTrack followUpTrack) {
        startPage();
        List<GcFollowUpTrack> list = followUpTrackService.selectFollowUpTrackDetailList(followUpTrack);
        return getDataTable(list);
    }

    /**
     * 获取随访跟踪详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:query')")
    @GetMapping(value = "/{trackId}")
    public R<GcFollowUpTrack> getInfo(@PathVariable("trackId") Long trackId) {
        return R.ok(followUpTrackService.selectFollowUpTrackById(trackId));
    }

    /**
     * 根据随访对象ID获取跟踪记录
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:query')")
    @GetMapping(value = "/followup/{followUpId}")
    public R<List<GcFollowUpTrack>> getByFollowUpId(@PathVariable("followUpId") Long followUpId) {
        return R.ok(followUpTrackService.selectTracksByFollowUpId(followUpId));
    }

    /**
     * 新增随访跟踪
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:add')")
    @PostMapping
    public R<Void> add(@RequestBody GcFollowUpTrack followUpTrack) {
        return toAjax(followUpTrackService.insertFollowUpTrack(followUpTrack));
    }

    /**
     * 修改随访跟踪
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody GcFollowUpTrack followUpTrack) {
        return toAjax(followUpTrackService.updateFollowUpTrack(followUpTrack));
    }

    /**
     * 删除随访跟踪
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:track:remove')")
    @DeleteMapping("/{trackIds}")
    public R<Void> remove(@PathVariable Long[] trackIds) {
        return toAjax(followUpTrackService.deleteFollowUpTrackByIds(trackIds));
    }
}
