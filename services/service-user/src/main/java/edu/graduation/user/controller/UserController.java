package edu.graduation.user.controller;

import edu.graduation.common.Result;
import edu.graduation.user.model.RegisterRequest;
import edu.graduation.user.model.*;
import edu.graduation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 网关将 /api/user/login 重写为 /login，故此处映射 /login */
    @PostMapping("/login")
    public Result<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse resp = userService.login(request);
        return Result.success("登录成功", resp);
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterRequest request) {
        try {
            long id = userService.register(request);
            return Result.success("注册成功", id);
        } catch (IllegalArgumentException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @GetMapping("/roles")
    public Result<List<RoleVO>> listRoles(@RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return Result.fail(403, "无权限");
        }
        return Result.success("成功", userService.listRoles());
    }

    @GetMapping("/users")
    public Result<List<UserManageVO>> listUsers(
            @RequestParam(value = "includeDisabled", defaultValue = "false") Boolean includeDisabled,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return Result.fail(403, "无权限");
        }
        return Result.success("成功", userService.listUsers(includeDisabled));
    }

    @PostMapping("/users")
    public ResponseEntity<Result<Long>> addUser(@RequestBody UserAddRequest request,
                                                @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            long id = userService.addUser(request);
            return ResponseEntity.ok(Result.success("新增成功", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Result<Void>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request,
                                                   @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        request.setId(id);
        try {
            userService.updateUser(request);
            return ResponseEntity.ok(Result.success("修改成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable Long id,
                                                    @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Result.success("删除成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<Result<Void>> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds,
                                                     @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        try {
            userService.assignRoles(id, roleIds);
            return ResponseEntity.ok(Result.success("角色分配成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Result<Void>> resetPassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> body,
                                                        @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "无权限"));
        }
        String newPassword = body != null ? body.get("newPassword") : null;
        try {
            userService.resetPassword(id, newPassword);
            return ResponseEntity.ok(Result.success("密码重置成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    private boolean hasAdmin(String roles) {
        return roles != null && roles.contains("ADMIN");
    }
}
