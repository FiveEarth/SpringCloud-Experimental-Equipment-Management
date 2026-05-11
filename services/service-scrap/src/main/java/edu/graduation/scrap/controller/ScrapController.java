package edu.graduation.scrap.controller;

import edu.graduation.common.Result;
import edu.graduation.scrap.bean.Scrap;
import edu.graduation.scrap.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
                                               @RequestHeader(value = "X-User-Name", required = false) String applyUserNameHeader,
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
        if (applyUserNameHeader != null && !applyUserNameHeader.isBlank()) {
            String exist = scrap.getApplyUserName();
            if (exist == null || exist.isBlank()) {
                scrap.setApplyUserName(applyUserNameHeader.trim());
            }
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

    @GetMapping("/scraps/catalog")
    @Operation(summary = "报废记录查询（学生只读）", description = "可选 approvalStatus：0 待审批 1 已通过 2 已驳回；不传则全部")
    public ResponseEntity<Result<List<Scrap>>> catalog(
            @RequestParam(value = "approvalStatus", required = false) Integer approvalStatus,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isStudentOnly(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅学生可访问"));
        }
        List<Scrap> list = scrapService.listFiltered(approvalStatus);
        return ResponseEntity.ok(Result.success("成功", list));
    }

    private static boolean isStudentOnly(String roles) {
        if (roles == null || roles.isBlank()) {
            return false;
        }
        return roles.contains("STUDENT")
                && !roles.contains("ADMIN")
                && !roles.contains("TEACHER")
                && !roles.contains("REPAIR");
    }

    @PostMapping("/scraps/{id}/approve")
    @Operation(summary = "审批报废申请", description = "1-通过 2-驳回；有 assetId 时仅报废该实例并 count-1，无 assetId 时整类型置为已报废")
    public ResponseEntity<Result<Void>> approve(@PathVariable("id") Long id,
                                                 @RequestBody Map<String, Object> body,
                                                 @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                                 @RequestHeader(value = "X-User-Name", required = false) String approvalUserNameHeader,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无审批权限"));
        }
        Integer status = body != null && body.get("status") != null ? ((Number) body.get("status")).intValue() : null;
        String disposalMethod = body != null && body.get("disposalMethod") != null ? body.get("disposalMethod").toString() : null;
        BigDecimal newResidualValue = null;
        if (body != null && body.get("residualValue") != null) {
            Object rv = body.get("residualValue");
            if (rv instanceof Number) {
                newResidualValue = BigDecimal.valueOf(((Number) rv).doubleValue());
            }
        }
        Long approvalUserId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        String approvalUserName = approvalUserNameHeader != null ? approvalUserNameHeader.trim() : null;
        try {
            scrapService.approve(id, status, approvalUserId, approvalUserName, disposalMethod, newResidualValue);
            return ResponseEntity.ok(Result.success(status != null && status == 1 ? "已通过" : "已驳回", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }
}
