package edu.graduation.lab.controller;

import edu.graduation.common.Result;
import edu.graduation.lab.bean.Lab;
import edu.graduation.lab.service.LabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "实验室管理", description = "实验室批量新增/删除接口")
@RequiredArgsConstructor
public class UserController {

    private final LabService labService;

    @GetMapping("/lab/list")
    @Operation(summary = "查询所有实验室")
    public ResponseEntity<Result<List<Lab>>> list() {
        List<Lab> labs = labService.listAll();
        return ResponseEntity.ok(Result.success("查询实验室成功", labs));
    }

    @PostMapping("/lab/batchAdd")
    @Operation(summary = "批量添加实验室")
    public ResponseEntity<Result<Void>> batchAdd(@RequestBody List<Lab> labs,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        labService.addLabs(labs);
        return ResponseEntity.ok(Result.success("批量添加实验室成功", null));
    }

    @PostMapping("/lab/batchDelete")
    @Operation(summary = "批量删除实验室")
    public ResponseEntity<Result<Void>> batchDelete(@RequestBody List<Long> ids,
                                                    @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        labService.deleteLabs(ids);
        return ResponseEntity.ok(Result.success("批量删除实验室成功", null));
    }

    private boolean hasTeacherOrAdmin(String roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.contains("ADMIN") || roles.contains("TEACHER");
    }
}
