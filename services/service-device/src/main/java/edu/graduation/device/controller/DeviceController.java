package edu.graduation.device.controller;

import edu.graduation.common.Result;
import edu.graduation.device.bean.Asset;
import edu.graduation.device.bean.Device;
import edu.graduation.device.service.DeviceService;
import edu.graduation.reserve.bean.Equipment;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/device/{id}")
    @Operation(summary = "按ID查询设备（含总数，供预约校验等）")
    public ResponseEntity<Result<Device>> getDevice(@PathVariable("id") Long id) {
        Device d = deviceService.getDeviceById(id);
        if (d == null) return ResponseEntity.ok(Result.fail(404, "设备不存在"));
        if (d.getTotalCount() == null && d.getCount() != null) d.setTotalCount(d.getCount());
        return ResponseEntity.ok(Result.success(d));
    }

    @GetMapping("/devices")
    @Operation(summary = "查询所有设备信息", description = "查询所有设备信息,用于用户/管理员查看设备列表")
    public ResponseEntity<Result<List<Device>>> getDevices(
            @RequestParam(value = "borrowable", required = false) Boolean borrowable) {
        try {
            List<Device> devices = Boolean.TRUE.equals(borrowable)
                    ? deviceService.getDevicesBorrowable()
                    : deviceService.getDevices();
            return ResponseEntity.ok(Result.success("查询设备信息成功", devices));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("查询设备信息失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PostMapping("/equipment/batchAdd")
    @Operation(summary = "批量添加设备")
    public ResponseEntity<Result<Void>> batchAdd(@RequestBody List<Equipment> list,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        deviceService.addEquipments(list);
        return ResponseEntity.ok(Result.success("批量添加设备成功", null));
    }

    @PostMapping("/equipment/batchDelete")
    @Operation(summary = "批量删除设备")
    public ResponseEntity<Result<Void>> batchDelete(@RequestBody List<Long> ids,
                                                    @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        try {
            deviceService.deleteEquipments(ids);
            return ResponseEntity.ok(Result.success("批量删除设备成功", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Result.fail(400, e.getMessage()));
        }
    }

    @PutMapping("/equipment/{id}/status")
    @Operation(summary = "修改设备状态")
    public ResponseEntity<Result<Void>> changeStatus(@PathVariable("id") Long id,
                                                     @RequestParam("status") Integer status,
                                                     @RequestHeader(value = "X-Roles", required = false) String roles,
                                                     @RequestHeader(value = "X-Internal", required = false) String internal) {
        if (!"apply".equals(internal) && !"maintain".equals(internal) && !hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        deviceService.changeEquipmentStatus(id, status);
        return ResponseEntity.ok(Result.success("修改设备状态成功", null));
    }

    @PutMapping("/equipment/{id}/count")
    @Operation(summary = "增减设备库存数量（兼容旧逻辑；推荐使用实例分配/释放）")
    public ResponseEntity<Result<Void>> updateCount(@PathVariable("id") Long id,
                                                    @RequestParam("delta") int delta,
                                                    @RequestHeader(value = "X-Internal", required = false) String internal) {
        if (internal == null || (!"apply".equals(internal) && !"scrap".equals(internal) && !"reserve".equals(internal))) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅允许内部服务调用"));
        }
        deviceService.updateEquipmentCount(id, delta);
        return ResponseEntity.ok(Result.success("更新数量成功", null));
    }

    @PostMapping("/equipment/{equipmentId}/allocateAsset")
    @Operation(summary = "分配一台在库实例（领用流程调用，内部接口）")
    public ResponseEntity<Result<Long>> allocateAsset(@PathVariable("equipmentId") Long equipmentId,
                                                     @RequestHeader(value = "X-Internal", required = false) String internal) {
        if (!"apply".equals(internal)) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅允许领用服务调用"));
        }
        try {
            Long assetId = deviceService.allocateAsset(equipmentId);
            return ResponseEntity.ok(Result.success("分配成功", assetId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        }
    }

    @PostMapping("/asset/{assetId}/release")
    @Operation(summary = "释放实例（归还后置为在库，内部接口）")
    public ResponseEntity<Result<Void>> releaseAsset(@PathVariable("assetId") Long assetId,
                                                    @RequestHeader(value = "X-Internal", required = false) String internal) {
        if (!"apply".equals(internal)) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅允许领用服务调用"));
        }
        deviceService.releaseAsset(assetId);
        return ResponseEntity.ok(Result.success("释放成功", null));
    }

    @PutMapping("/asset/{assetId}/status")
    @Operation(summary = "更新实例状态（维修/报废等，内部接口）")
    public ResponseEntity<Result<Void>> setAssetStatus(@PathVariable("assetId") Long assetId,
                                                      @RequestParam("status") Integer status,
                                                      @RequestHeader(value = "X-Internal", required = false) String internal) {
        if (internal == null || (!"apply".equals(internal) && !"maintain".equals(internal) && !"scrap".equals(internal))) {
            return ResponseEntity.status(403).body(Result.fail(403, "仅允许内部服务调用"));
        }
        deviceService.updateAssetStatus(assetId, status);
        return ResponseEntity.ok(Result.success("更新成功", null));
    }

    @GetMapping("/equipment/{equipmentId}/assets")
    @Operation(summary = "查询某设备类型下的所有实例")
    public ResponseEntity<Result<java.util.List<Asset>>> listAssets(@PathVariable("equipmentId") Long equipmentId) {
        return ResponseEntity.ok(Result.success("成功", deviceService.listAssetsByEquipmentId(equipmentId)));
    }

    @PutMapping("/equipment/{id}")
    @Operation(summary = "编辑设备信息（含 count、equipment_code）")
    public ResponseEntity<Result<Void>> updateEquipment(@PathVariable("id") Long id,
                                                         @RequestBody Device device,
                                                         @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (!hasTeacherOrAdmin(roles)) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有权限"));
        }
        if (device == null) {
            return ResponseEntity.badRequest().body(Result.paramFail("请求体不能为空"));
        }
        device.setDeviceId(id);
        deviceService.updateEquipment(device);
        return ResponseEntity.ok(Result.success("更新设备成功", null));
    }

    private boolean hasTeacherOrAdmin(String roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.contains("ADMIN") || roles.contains("TEACHER");
    }
}

