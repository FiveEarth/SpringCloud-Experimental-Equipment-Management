package edu.graduation.maintain.controller;

import edu.graduation.common.Result;
import edu.graduation.maintain.bean.Maintain;
import edu.graduation.maintain.service.MaintainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "维护维修管理", description = "设备维护与维修记录相关接口")
public class MaintainController {

    @Autowired
    private MaintainService maintainService;

    @PostMapping("/maintain")
    @Operation(summary = "创建维护/维修记录")
    public ResponseEntity<Result<Long>> createMaintain(@RequestBody Maintain maintain) {
        try {
            long id = maintainService.createMaintain(maintain);
            return ResponseEntity.ok(Result.success("创建维护记录成功", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("创建维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/maintains")
    @Operation(summary = "查询所有维护/维修记录（含用户软隐藏）", description = "仅管理员、教师")
    public ResponseEntity<Result<List<Maintain>>> getAllMaintains(
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isAdminOrTeacher(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            List<Maintain> list = maintainService.queryAll();
            return ResponseEntity.ok(Result.success("查询所有维护记录成功", list));
        } catch (Exception e) {
            log.error("查询所有维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/maintains/equipment/{equipmentId}")
    @Operation(summary = "根据设备ID查询维护/维修记录")
    public ResponseEntity<Result<List<Maintain>>> getMaintainsByEquipmentId(
            @PathVariable("equipmentId") Long equipmentId,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isAdminOrTeacher(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            List<Maintain> list = maintainService.queryByEquipmentId(equipmentId);
            return ResponseEntity.ok(Result.success("查询设备维护记录成功", list));
        } catch (Exception e) {
            log.error("查询设备维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/maintains/user/{userId}")
    @Operation(summary = "根据申请人ID查询维护/维修记录")
    public ResponseEntity<Result<List<Maintain>>> getMaintainsByApplyUser(
            @PathVariable("userId") Long userId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        long self = parseUserId(userIdStr);
        if (!isAdminOrTeacher(roles) && (self <= 0 || self != userId)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            List<Maintain> list = maintainService.queryByApplyUserId(userId);
            return ResponseEntity.ok(Result.success("查询用户维护记录成功", list));
        } catch (Exception e) {
            log.error("查询用户维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/maintains/mine")
    @Operation(summary = "当前登录用户提交的维修/维护记录", description = "供学生「维修申请」页使用，依据 X-User-Id")
    public ResponseEntity<Result<List<Maintain>>> myMaintains(
            @RequestHeader(value = "X-User-Id", required = false) String userIdStr) {
        long uid = parseUserId(userIdStr);
        if (uid <= 0) {
            return ResponseEntity.badRequest().body(Result.paramFail("缺少用户身份"));
        }
        List<Maintain> list = maintainService.queryByApplyUserIdVisible(uid);
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @PostMapping("/maintain/{id}/applicant-revoke")
    @Operation(summary = "申请人撤销维修单", description = "仅待处理且未接单；软隐藏并恢复实例为在库")
    public ResponseEntity<Result<Void>> applicantRevoke(@PathVariable("id") Long id,
                                                        @RequestHeader(value = "X-User-Id", required = false) String userIdStr) {
        long uid = parseUserId(userIdStr);
        if (uid <= 0) {
            return ResponseEntity.badRequest().body(Result.paramFail("缺少用户身份"));
        }
        try {
            maintainService.applicantRevokePending(id, uid);
            return ResponseEntity.ok(Result.success("已撤销", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PostMapping("/maintain/{id}/applicant-soft-hide")
    @Operation(summary = "申请人隐藏已完成维修单", description = "软隐藏，管理员/教师仍可见")
    public ResponseEntity<Result<Void>> applicantSoftHide(@PathVariable("id") Long id,
                                                          @RequestHeader(value = "X-User-Id", required = false) String userIdStr) {
        long uid = parseUserId(userIdStr);
        if (uid <= 0) {
            return ResponseEntity.badRequest().body(Result.paramFail("缺少用户身份"));
        }
        try {
            maintainService.applicantSoftHideCompleted(id, uid);
            return ResponseEntity.ok(Result.success("已从我的列表隐藏", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PutMapping("/maintain/restore/{id}")
    @Operation(summary = "恢复用户软隐藏的维修记录", description = "管理员、教师")
    public ResponseEntity<Result<String>> restoreMaintain(@PathVariable("id") Long id,
                                                          @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isAdminOrTeacher(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            maintainService.restoreMaintain(id);
            return ResponseEntity.ok(Result.success("已恢复"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("恢复维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/maintain")
    @Operation(summary = "修改维护/维修记录（含进度更新、指派人员）")
    public ResponseEntity<Result<Void>> modifyMaintain(@RequestBody Maintain maintain) {
        try {
            maintainService.modifyMaintain(maintain);
            return ResponseEntity.ok(Result.success("修改维护记录成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("修改维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/maintains/pending")
    @Operation(summary = "待维修设备列表", description = "progress_status=0")
    public ResponseEntity<Result<List<Maintain>>> getPending() {
        List<Maintain> list = maintainService.queryPending();
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @GetMapping("/maintains/repairer")
    @Operation(summary = "维修员工作列表", description = "待处理+进行中+已完成+已隐藏，用于维修员列表筛选展示")
    public ResponseEntity<Result<List<Maintain>>> getRepairerList() {
        List<Maintain> list = maintainService.queryPendingAndInProgress();
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @GetMapping("/maintains/pendingOverdue")
    @Operation(summary = "24h未接单提醒", description = "待处理且创建超过24小时")
    public ResponseEntity<Result<List<Maintain>>> getPendingOverdue() {
        List<Maintain> list = maintainService.queryPendingOverdue24h();
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @GetMapping("/maintains/stats")
    @Operation(summary = "维修设备统计", description = "按时间范围统计")
    public ResponseEntity<Result<java.util.Map<String, Object>>> stats(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        java.util.Map<String, Object> data = maintainService.maintainStats(startTime, endTime);
        return ResponseEntity.ok(Result.success("统计成功", data));
    }

    @GetMapping("/maintains/audit")
    @Operation(summary = "维修日志审计", description = "按设备/用户/时间范围查询维修记录")
    public ResponseEntity<Result<List<Maintain>>> audit(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isAdminOrTeacher(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        List<Maintain> list = maintainService.queryAudit(equipmentId, userId, startTime, endTime);
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @PostMapping("/maintain/{id}/accept")
    @Operation(summary = "维修人员接单")
    public ResponseEntity<Result<Void>> accept(@PathVariable("id") Long id,
                                               @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                               @RequestHeader(value = "X-User-Name", required = false) String assignUserName) {
        Long userId = parseUserId(userIdStr);
        maintainService.acceptOrder(id, userId, assignUserName);
        return ResponseEntity.ok(Result.success("接单成功", null));
    }

    @PostMapping("/maintain/{id}/reject")
    @Operation(summary = "维修人员拒绝接单")
    public ResponseEntity<Result<Void>> reject(@PathVariable("id") Long id,
                                               @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                               @RequestParam(value = "reason", required = false) String reason) {
        Long userId = parseUserId(userIdStr);
        maintainService.rejectOrder(id, userId, reason);
        return ResponseEntity.ok(Result.success("已拒绝接单", null));
    }

    @DeleteMapping("/maintain/{id}")
    @Operation(summary = "物理删除维修记录", description = "仅系统管理员")
    public ResponseEntity<Result<Void>> deleteMaintain(@PathVariable("id") Long id,
                                                       @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!isAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅管理员可彻底删除"));
        }
        try {
            maintainService.deleteMaintain(id);
            return ResponseEntity.ok(Result.success("已彻底删除", null));
        } catch (Exception e) {
            log.error("删除维护记录失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PostMapping("/maintain/{id}/complete")
    @Operation(summary = "维修员完成维修", description = "成功则设备恢复正常，失败则自动提交报废申请")
    public ResponseEntity<Result<Void>> complete(@PathVariable("id") Long id,
                                                 @RequestBody java.util.Map<String, Object> body,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("REPAIR")) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅维修员可操作"));
        }
        Boolean success = body != null && body.get("success") != null ? (Boolean) body.get("success") : null;
        java.math.BigDecimal cost = null;
        if (body != null && body.get("cost") != null) {
            Object c = body.get("cost");
            if (c instanceof Number) cost = java.math.BigDecimal.valueOf(((Number) c).doubleValue());
        }
        String maintainContent = body != null && body.get("maintainContent") != null ? body.get("maintainContent").toString() : null;
        java.math.BigDecimal scrapResidualValue = null;
        if (body != null && body.get("residualValue") != null) {
            Object rv = body.get("residualValue");
            if (rv instanceof Number) {
                scrapResidualValue = java.math.BigDecimal.valueOf(((Number) rv).doubleValue());
            }
        }
        try {
            maintainService.completeRepair(id, success, cost, maintainContent, scrapResidualValue);
            return ResponseEntity.ok(Result.success(Boolean.TRUE.equals(success) ? "维修完成，设备已恢复" : "已转报废申请", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    private static Long parseUserId(String userIdStr) {
        if (userIdStr == null || userIdStr.isBlank()) return 0L;
        try {
            return Long.parseLong(userIdStr.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static boolean isAdmin(String roles) {
        return roles != null && roles.contains("ADMIN");
    }

    private static boolean isAdminOrTeacher(String roles) {
        return roles != null && (roles.contains("ADMIN") || roles.contains("TEACHER"));
    }
}