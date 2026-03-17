package edu.graduation.scrap.controller;

import edu.graduation.common.Result;
import edu.graduation.scrap.bean.Scrap;
import edu.graduation.scrap.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ScrapController {

    @Autowired
    private ScrapService scrapService;

    @PostMapping("/scrap")
    @Operation(summary = "提交报废申请")
    public ResponseEntity<Result<Long>> create(@RequestBody Scrap scrap,
                                               @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                               @RequestHeader(value = "X-Roles", required = false) String roles,
                                               @RequestHeader(value = "X-Internal", required = false) String internal) {
        boolean allowed = "maintain".equals(internal)
                || (roles != null && (roles.contains("ADMIN") || roles.contains("TEACHER") || roles.contains("REPAIR")));
        if (!allowed) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        if (scrap.getApplyUserId() == null && userIdStr != null && !userIdStr.isEmpty()) {
            scrap.setApplyUserId(Long.parseLong(userIdStr));
        }
        try {
            long id = scrapService.create(scrap);
            return ResponseEntity.ok(Result.success("提交成功", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @GetMapping("/scraps/pending")
    @Operation(summary = "待审批报废列表")
    public ResponseEntity<Result<List<Scrap>>> listPending(@RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        List<Scrap> list = scrapService.listPending();
        return ResponseEntity.ok(Result.success("成功", list));
    }

    @GetMapping("/scraps")
    @Operation(summary = "全部报废记录")
    public ResponseEntity<Result<List<Scrap>>> listAll(@RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        List<Scrap> list = scrapService.listAll();
        return ResponseEntity.ok(Result.success("成功", list));
    }

    @PostMapping("/scraps/{id}/approve")
    @Operation(summary = "审批报废申请", description = "1-通过 2-驳回，通过后设备 count-1、status=3")
    public ResponseEntity<Result<Void>> approve(@PathVariable("id") Long id,
                                                 @RequestBody Map<String, Object> body,
                                                 @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无审批权限"));
        }
        Integer status = body != null && body.get("status") != null ? ((Number) body.get("status")).intValue() : null;
        String disposalMethod = body != null && body.get("disposalMethod") != null ? body.get("disposalMethod").toString() : null;
        Long approvalUserId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        try {
            scrapService.approve(id, status, approvalUserId, disposalMethod);
            return ResponseEntity.ok(Result.success(status != null && status == 1 ? "已通过" : "已驳回", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }
}
