package edu.graduation.apply.controller;

import edu.graduation.reserve.bean.Apply;
import edu.graduation.apply.service.ApplyService;
import edu.graduation.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "领用归还管理", description = "设备领用与归还申请相关接口")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @PostMapping("/apply")
    @Operation(summary = "创建领用/归还申请")
    public ResponseEntity<Result<Long>> createApply(@RequestBody Apply apply) {
        try {
            long id = applyService.createApply(apply);
            return ResponseEntity.ok(Result.success("创建申请成功", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("创建申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/applies")
    @Operation(summary = "查询所有领用/归还申请")
    public ResponseEntity<Result<List<Apply>>> getAllApplies() {
        try {
            List<Apply> applies = applyService.queryAllApply();
            return ResponseEntity.ok(Result.success("查询所有申请成功", applies));
        } catch (Exception e) {
            log.error("查询所有申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/applies/pending")
    @Operation(summary = "待审批领用申请列表")
    public ResponseEntity<Result<List<Apply>>> getPending() {
        List<Apply> list = applyService.queryPending();
        return ResponseEntity.ok(Result.success("成功", list));
    }

    @GetMapping("/applies/user/{userId}")
    @Operation(summary = "根据用户ID查询领用/归还申请")
    public ResponseEntity<Result<List<Apply>>> getAppliesByUserId(@PathVariable("userId") Long userId) {
        try {
            List<Apply> applies = applyService.queryApplyByUserId(userId);
            return ResponseEntity.ok(Result.success("查询用户申请成功", applies));
        } catch (Exception e) {
            log.error("查询用户申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/apply")
    @Operation(summary = "修改领用/归还申请（含审批）")
    public ResponseEntity<Result<Void>> modifyApply(@RequestBody Apply apply) {
        try {
            applyService.modifyApply(apply);
            return ResponseEntity.ok(Result.success("修改申请成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("修改申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/apply/restore/{id}")
    @Operation(summary = "恢复软删除的领用记录")
    public ResponseEntity<Result<String>> restoreApply(@PathVariable("id") Integer id) {
        try {
            applyService.restoreApply(id);
            return ResponseEntity.ok(Result.success("已恢复", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("恢复申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/apply/{id}")
    @Operation(summary = "删除领用/归还申请")
    public ResponseEntity<Result<Void>> deleteApply(@PathVariable("id") Long id) {
        try {
            applyService.deleteApply(id);
            return ResponseEntity.ok(Result.success("删除申请成功", null));
        } catch (Exception e) {
            log.error("删除申请失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/apply/audit")
    @Operation(summary = "领用归还日志审计", description = "按设备/用户/时间范围查询领用归还记录")
    public ResponseEntity<Result<List<Apply>>> audit(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<Apply> list = applyService.queryByTimeRange(equipmentId, userId, startTime, endTime);
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    @PostMapping("/apply/approve/{id}")
    @Operation(summary = "审批领用申请", description = "1-通过 2-驳回，通过后设备状态改为领用中")
    public ResponseEntity<Result<Void>> approve(@PathVariable("id") Integer id,
                                                @RequestBody java.util.Map<String, Object> body,
                                                @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                                @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无审批权限"));
        }
        Integer status = body != null && body.get("status") != null ? ((Number) body.get("status")).intValue() : null;
        String remarks = body != null && body.get("remarks") != null ? body.get("remarks").toString() : null;
        String approvalUserName = body != null && body.get("approvalUserName") != null ? body.get("approvalUserName").toString() : null;
        Long approveUserId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        try {
            applyService.approveApply(id, status, approveUserId, approvalUserName, remarks);
            return ResponseEntity.ok(Result.success(status != null && status == 1 ? "已通过" : "已驳回", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PostMapping("/apply/return/{id}")
    @Operation(summary = "确认归还", description = "填写归还时间，库存+1；body 可传 equipmentStatus：0-正常，1-故障（故障时设备置为故障待修）")
    public ResponseEntity<Result<Void>> confirmReturn(@PathVariable("id") Integer id,
                                                        @RequestBody(required = false) java.util.Map<String, Object> body,
                                                        @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        Integer equipmentStatus = null;
        if (body != null && body.get("equipmentStatus") != null) {
            equipmentStatus = ((Number) body.get("equipmentStatus")).intValue();
        }
        try {
            applyService.confirmReturn(id, equipmentStatus);
            return ResponseEntity.ok(Result.success("归还确认成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PostMapping("/apply/startBorrow")
    @Operation(summary = "开始领用", description = "预约通过后学生操作，根据预约ID生成领用记录并扣减库存")
    public ResponseEntity<Result<Long>> startBorrow(@RequestBody java.util.Map<String, Object> body,
                                                     @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                                     @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Object rid = body != null ? body.get("reserveId") : null;
        Long reserveId = rid instanceof Number ? ((Number) rid).longValue() : null;
        if (reserveId == null && body != null && body.get("reserveId") != null) {
            try { reserveId = Long.parseLong(body.get("reserveId").toString()); } catch (Exception ignored) {}
        }
        if (reserveId == null) {
            return ResponseEntity.badRequest().body(Result.paramFail("缺少预约ID reserveId"));
        }
        Long userId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        if (userId == null) {
            return ResponseEntity.badRequest().body(Result.paramFail("请先登录"));
        }
        try {
            long applyId = applyService.startBorrow(reserveId, userId, userName != null ? userName : "");
            return ResponseEntity.ok(Result.success("开始领用成功", applyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("开始领用失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PostMapping("/apply/return/apply/{id}")
    @Operation(summary = "申请归还", description = "学生对已通过的领用提交归还申请，进入待归还审批")
    public ResponseEntity<Result<Void>> applyReturn(@PathVariable("id") Integer id,
                                                     @RequestHeader(value = "X-User-Id", required = false) String userIdStr) {
        Long userId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        if (userId == null) {
            return ResponseEntity.badRequest().body(Result.paramFail("请先登录"));
        }
        try {
            applyService.applyReturn(id, userId);
            return ResponseEntity.ok(Result.success("已提交归还申请", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @GetMapping("/applies/pendingReturn")
    @Operation(summary = "待归还审批列表", description = "return_status=1 的领用记录，供管理员/教师审批归还")
    public ResponseEntity<Result<List<Apply>>> getPendingReturn() {
        List<Apply> list = applyService.queryPendingReturn();
        return ResponseEntity.ok(Result.success("成功", list));
    }

    @PostMapping("/apply/return/approve/{id}")
    @Operation(summary = "归还审批", description = "通过或驳回归还申请；body: approved, equipmentStatus(0-正常 1-故障), approvalUserName, remarks")
    public ResponseEntity<Result<Void>> approveReturn(@PathVariable("id") Integer id,
                                                       @RequestBody java.util.Map<String, Object> body,
                                                       @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
                                                       @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "无归还审批权限"));
        }
        Boolean approved = body != null && body.get("approved") != null ? (Boolean) body.get("approved") : null;
        if (approved == null && body != null && body.get("approved") instanceof Boolean) {
            approved = (Boolean) body.get("approved");
        }
        Integer equipmentStatus = body != null && body.get("equipmentStatus") != null ? ((Number) body.get("equipmentStatus")).intValue() : null;
        String approvalUserName = body != null && body.get("approvalUserName") != null ? body.get("approvalUserName").toString() : null;
        String remarks = body != null && body.get("remarks") != null ? body.get("remarks").toString() : null;
        Long approveUserId = userIdStr != null && !userIdStr.isEmpty() ? Long.parseLong(userIdStr) : null;
        try {
            applyService.approveReturn(id, Boolean.TRUE.equals(approved), equipmentStatus, approveUserId, approvalUserName, remarks);
            return ResponseEntity.ok(Result.success(Boolean.TRUE.equals(approved) ? "归还通过" : "已驳回归还", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }
}
