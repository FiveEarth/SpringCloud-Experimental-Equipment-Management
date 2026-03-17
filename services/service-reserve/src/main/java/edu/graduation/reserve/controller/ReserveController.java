package edu.graduation.reserve.controller;

import edu.graduation.common.Result;
import edu.graduation.reserve.bean.Reserve;
import edu.graduation.reserve.properties.Reserveproperties;
import edu.graduation.reserve.service.ReserveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RefreshScope //自动刷新
//@RequestMapping("/api/reserve")
@RestController
@Slf4j
@Tag(name = "设备预约申请", description = "设备预约申请相关的管理接口")
public class ReserveController {
    @Autowired
    ReserveService reserveService;
    @Autowired
    Reserveproperties reserveproperties;
    //实际项目如下
    //创建预约申请
    @Operation(summary = "创建预约申请", description = "根据传入的设备信息创建一个新的预约申请")
    @PostMapping("/createReserve")
    public ResponseEntity<Result<Long>> createReserve(@RequestBody Reserve reserve) {
        try {
            log.debug("收到预约申请 equipmentId={}, userId={}", reserve != null ? reserve.getEquipmentId() : null, reserve != null ? reserve.getUserId() : null);
            Long reserveId = reserveService.createReserve(reserve);
            return ResponseEntity.ok(Result.success("创建预约成功", reserveId));
        } catch (IllegalArgumentException e) {
            log.warn("创建预约参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("创建预约失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }
    //查询该用户的所有预约申请,返回订单列表list
    @GetMapping("/getReserveById/{id}")
    @Operation(summary = "根据预约ID查询预约详情", description = "供领用服务开始领用时校验预约")
    public ResponseEntity<Result<Reserve>> getReserveById(@PathVariable("id") Long id) {
        try {
            Reserve reserve = reserveService.getById(id);
            return ResponseEntity.ok(Result.success("查询成功", reserve));
        } catch (Exception e) {
            log.error("查询预约失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/queryReserveByUserId")
    public ResponseEntity<Result<List<Reserve>>> queryReserve(@RequestParam("userId") Long userId) {
        try {
            List<Reserve> reserve = reserveService.queryReserveByUserId(userId);
            return ResponseEntity.ok(Result.success("查询订单成功", reserve));
        } catch (IllegalArgumentException e) {
            // 参数错误：400状态码
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            // 系统异常：500状态码
            log.error("查询订单失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }
    //查询所有用户的所有预约申请,返回订单列表list
    @Operation(summary = "查询所有预约申请", description = "查询所有预约申请,用于管理员查看所有预约申请")
    @GetMapping("/queryAllReserve")
    public ResponseEntity<Result<List<Reserve>>> queryAllReserve() {
        try {
            List<Reserve> reserve = reserveService.queryAllReserve();
            return ResponseEntity.ok(Result.success("查询所有订单成功", reserve));
        } catch (IllegalArgumentException e) {
            // 参数错误：400状态码
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            // 系统异常：500状态码
            log.error("查询所有订单失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }
    //通过id修改预约申请
    @Operation(summary = "修改预约申请", description = "根据预约申请Id修改预约申请")
    @PutMapping("/modifyReserve/{id}")
    public ResponseEntity<Result<String>> modifyReserve(@RequestBody Reserve reserve, @PathVariable int id) {
        try {
            reserve.setId(id);
            reserveService.modifyReserve(reserve);
            return ResponseEntity.ok(Result.success("修改订单成功"));
        } catch (IllegalArgumentException e) {
            // 参数错误：400状态码
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            // 系统异常：500状态码
            log.error("修改订单失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }
    //删除预约申请
    @Operation(summary = "删除预约申请", description = "根据预约申请Id删除预约申请")
    @DeleteMapping("/deleteReserve")
    public ResponseEntity<Result<String>> deleteReserve(@RequestParam("reserveId") Long reserveId) {
        try {
            reserveService.deleteReserve(reserveId);
            return ResponseEntity.ok(Result.success("删除订单成功"));
        } catch (IllegalArgumentException e) {
            // 参数错误：400状态码
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            // 系统异常：500状态码
            log.error("删除订单失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/restore/{id}")
    @Operation(summary = "恢复软删除的预约", description = "将 status=4 的预约恢复为 original_status")
    public ResponseEntity<Result<String>> restoreReserve(@PathVariable("id") Long id) {
        try {
            reserveService.restoreReserve(id);
            return ResponseEntity.ok(Result.success("已恢复"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.paramFail(e.getMessage()));
        } catch (Exception e) {
            log.error("恢复预约失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/markReserveUsed/{id}")
    @Operation(summary = "标记预约已领用", description = "领用审批通过后由领用服务调用，将预约标记为已领用")
    public ResponseEntity<Result<Void>> markReserveUsed(@PathVariable("id") Long id) {
        try {
            reserveService.markReserveUsed(id);
            return ResponseEntity.ok(Result.success("已标记", null));
        } catch (Exception e) {
            log.error("标记预约已领用失败", e);
            return ResponseEntity.status(500).body(Result.fail(e.getMessage()));
        }
    }

    @PutMapping("/approve/{id}")
    @Operation(summary = "审批预约申请", description = "管理员/教师修改预约状态 1-通过（锁定设备数量） 2-驳回（若曾通过则释放数量）")
    public ResponseEntity<Result<Void>> approve(@PathVariable("id") Integer id,
                                               @RequestParam("status") Integer status,
                                               @RequestHeader(value = "X-Roles", required = false) String roles) {
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("TEACHER"))) {
            return ResponseEntity.status(403).body(Result.fail(403, "没有审批权限"));
        }
        try {
            reserveService.approveReserve(id, status);
            return ResponseEntity.ok(Result.success("审批成功", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Result.fail(e.getMessage()));
        }
    }

    @GetMapping("/usageStats")
    @Operation(summary = "设备使用统计", description = "可选设备ID、用户ID、时间范围")
    public ResponseEntity<Result<java.util.Map<String, Object>>> usageStats(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        java.util.Map<String, Object> data = reserveService.usageStats(equipmentId, userId, startTime, endTime);
        return ResponseEntity.ok(Result.success("统计成功", data));
    }

    @GetMapping("/audit")
    @Operation(summary = "预约日志审计", description = "按设备/用户/时间范围查询预约记录")
    public ResponseEntity<Result<List<Reserve>>> audit(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<Reserve> list = reserveService.queryByTimeRange(equipmentId, userId, startTime, endTime);
        return ResponseEntity.ok(Result.success("查询成功", list));
    }

    //下面皆为测试
//    @GetMapping("/config")
//    public String config() {
//        return "Reserve.timeout=" + Reserveproperties.getTimeout() + ";" +
//                "Reserve.auto-confirm=" + Reserveproperties.getAutoConfirm() + ";" +
//                "Reserve.db-url=" + Reserveproperties.getDbUrl();
//    }
//
//    //创建订单
//    @GetMapping("/creater")
//    public Reserve creatReserve(@RequestParam("userId") Long userId,
//                            @RequestParam("deviceId") Long deviceId) {
//        Reserve Reserve = ReserveService.creatReserve(deviceId, userId);
//        return Reserve;
//
//    }
//
//    //创建秒杀订单
//    @GetMapping("/seckill")
//    @SentinelResource(value = "seckill-Reserve", fallback = "seckillFallback")
//    public Reserve seckill(@RequestParam("userId") Long userId,
//                         @RequestParam("deviceId") Long deviceId) {
//        Reserve Reserve = ReserveService.creatReserve(deviceId, userId);
//        Reserve.setReserveId((int) Long.MAX_VALUE);
//        return Reserve;
//    }
//
//    public Reserve seckillFallback(@RequestParam("userId") Long userId,
//                                 @RequestParam("deviceId") Long deviceId, Throwable exception) {
//        System.out.println("seckillFallback....");
//        Reserve Reserve = new Reserve();
//        Reserve.setDeviceId(deviceId);
//        Reserve.setUserId(userId);
//        Reserve.setUserName("异常信息:" + exception.getClass());
//        return Reserve;
//    }
//
//    @GetMapping("/writeDb")
//    public String writeDb() {
//        return "writeDb success...";
//    }
//
//    @GetMapping("/readDb")
//    public String readDb() {
//        log.info("readDb...");
//        return "readDb success...";
//    }
}

