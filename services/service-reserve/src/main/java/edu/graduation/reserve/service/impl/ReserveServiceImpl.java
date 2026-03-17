package edu.graduation.reserve.service.impl;


import edu.graduation.reserve.bean.Reserve;
import edu.graduation.reserve.dao.ReserveDao;
import edu.graduation.reserve.feign.DeviceFeignClient;
import edu.graduation.reserve.service.ReserveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReserveServiceImpl implements ReserveService {

    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    RestTemplate restTemplate;
    @Autowired //一定导入spring-cloud-starter-loadbalancer
    LoadBalancerClient loadBalancerClient;
    @Autowired
    private DeviceFeignClient deviceFeignClient;
    @Autowired
    private ReserveDao reserveDao;

//    @SentinelResource(value = "creatReserve",blockHandler = "createReserveFallBack")
//    @Override
//    public Reserve creatReserve(Long deviceId, Long userId) {
////        Device device = getDeviceFromRemoteWithLoadBalanceAnnotation(deviceId);
//
//        //使用Feign完成远程调用
//        Device device = deviceFeignClient.getDeviceId(deviceId);
//        Reserve Reserve = new Reserve();
//        Reserve.setReserveId(1);
//        //总金额
//        Reserve.setTotalAmount(device.getPrice().multiply(new BigDecimal(device.getNum())));
//        Reserve.setUserId(userId);
//        Reserve.setUserName("zhangsan");
//        Reserve.setStatus(1);
//        //远程查询商品列表
//        Reserve.setDreviceList(Arrays.asList(device));
////        try {
////            SphU.entry("haha");
////        } catch (BlockException e) {
////            //编码处理
////        }
//        return Reserve;
//    }

    /**
     * @param reserve
     * @return
     */
    @Override
    public long createReserve(Reserve reserve) {
        // ========== 第一步：检查参数是否合法 ==========
        //
        if (reserve == null) {
            log.error("创建订单失败：参数为空");
            throw new RuntimeException("创建预约申请失败：参数为空");
        }
        Long userId = reserve.getUserId();
        if (userId == null) {
            log.error("创建预约失败：用户ID为空");
            throw new IllegalArgumentException("创建预约申请失败：用户ID为空，请重新登录");
        }
        long equipmentId = reserve.getEquipmentId();
        if (equipmentId <= 0) {
            log.error("创建预约失败：设备ID无效 equipmentId={}", equipmentId);
            throw new IllegalArgumentException("请选择要预约的设备");
        }
        if (reserve.getReserveDate() == null) {
            throw new IllegalArgumentException("请选择预约日期");
        }
        if (reserve.getStartTime() == null || reserve.getEndTime() == null) {
            throw new IllegalArgumentException("请选择开始时段和结束时段");
        }
        int quantity = reserve.getReserveQuantity() != null && reserve.getReserveQuantity() >= 1 ? reserve.getReserveQuantity() : 1;
        reserve.setReserveQuantity(quantity);
        if (deviceFeignClient != null) {
            try {
                edu.graduation.common.Result<edu.graduation.device.bean.Device> res = deviceFeignClient.getDeviceById(equipmentId);
                if (res != null && res.getCode() != null && res.getCode() == 200 && res.getData() != null) {
                    edu.graduation.device.bean.Device device = res.getData();
                    // 可用数量 = 当前 count（已扣除已通过未领用的预约数量），不足时用 totalCount 兜底
                    int available = device.getCount() != null ? device.getCount() : (device.getTotalCount() != null ? device.getTotalCount() : 0);
                    if (available <= 0) throw new IllegalArgumentException("该设备暂无可用数量");
                    if (quantity > available) throw new IllegalArgumentException("预约数量不能超过当前可用数量（当前可用：" + available + "）");
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                log.warn("校验设备数量失败，继续创建预约", e);
            }
        }
        // ========== 第三步：调用DAO层执行数据库插入 ==========
        log.info("创建预约：equipmentId={}, userId={}, reserveDate={}", equipmentId, userId, reserve.getReserveDate());
        int affectRows = reserveDao.insert(reserve);
        if (affectRows <= 0) {
            log.error("创建订单失败：数据库插入失败");
            throw new RuntimeException("创建订单失败：数据库插入失败");
        }

        Long id = Long.valueOf(reserve.getId());
        log.info("创建预约成功：reserveId={}", id);
        return id != null ? id.longValue() : 0L;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Reserve getById(Long id) {
        if (id == null) return null;
        return reserveDao.selectById(id);
    }

    @Override
    public List<Reserve> queryReserveByUserId(Long userId) {
        return reserveDao.queryReserveByUserId(userId);
    }

    /**
     * @return
     */
    @Override
    public List<Reserve> queryAllReserve() {
        return reserveDao.queryAllReserve();
    }

    /**
     * @param reserve
     */
    @Override
    public void modifyReserve(Reserve reserve) {
        Integer id = reserve.getId();
        if (id == null) {
            log.error("修改订单失败：参数为空");
            throw new RuntimeException("修改订单失败：参数为空");
        }
        Reserve current = reserveDao.selectById(id.longValue());
        if (reserve.getStatus() != null && reserve.getStatus() == 4 && current != null && current.getOriginalStatus() == null) {
            reserve.setOriginalStatus(current.getStatus());
        }
        if (reserve.getStatus() != null && (reserve.getStatus() == 2 || reserve.getStatus() == 4)) {
            boolean wasApprovedNotUsed = current != null && current.getStatus() != null && current.getStatus() == 1
                    && (current.getIsUsed() == null || current.getIsUsed() != 1);
            if (wasApprovedNotUsed && deviceFeignClient != null) {
                Long eqId = current.getEquipmentId();
                int qty = current.getReserveQuantity() != null && current.getReserveQuantity() >= 1 ? current.getReserveQuantity() : 1;
                try {
                    deviceFeignClient.updateEquipmentCount(eqId, qty);
                    log.info("预约取消/软删除，已加回设备 id={} 数量 {}", eqId, qty);
                } catch (Exception e) {
                    log.error("加回设备数量失败", e);
                    throw new RuntimeException("加回设备数量失败，请稍后重试", e);
                }
            }
        }
        reserveDao.modifyReserve(reserve);
    }

    @Override
    public void approveReserve(Integer id, Integer newStatus) {
        if (id == null || newStatus == null) {
            throw new RuntimeException("参数为空");
        }
        Reserve reserve = reserveDao.selectById(id.longValue());
        if (reserve == null) {
            throw new IllegalArgumentException("预约不存在");
        }
        Integer oldStatus = reserve.getStatus();
        Long eqId = reserve.getEquipmentId();
        int qty = reserve.getReserveQuantity() != null && reserve.getReserveQuantity() >= 1 ? reserve.getReserveQuantity() : 1;
        if (newStatus == 1) {
            if (deviceFeignClient != null) {
                try {
                    deviceFeignClient.updateEquipmentCount(eqId, -qty);
                    log.info("预约审批通过，已锁定设备 id={} 数量 {}", eqId, qty);
                } catch (Exception e) {
                    log.error("锁定设备数量失败，当前可能可用数量不足", e);
                    throw new IllegalStateException("设备可用数量不足，无法通过该预约（当前需 " + qty + " 台）", e);
                }
            }
            reserveDao.updateStatus(id, 1);
        } else if (newStatus == 2 || newStatus == 4) {
            boolean wasApprovedNotUsed = oldStatus != null && oldStatus == 1
                    && (reserve.getIsUsed() == null || reserve.getIsUsed() != 1);
            if (wasApprovedNotUsed && deviceFeignClient != null) {
                try {
                    deviceFeignClient.updateEquipmentCount(eqId, qty);
                    log.info("预约驳回/取消，已释放设备 id={} 数量 {}", eqId, qty);
                } catch (Exception e) {
                    log.error("释放设备数量失败", e);
                    throw new RuntimeException("释放设备数量失败，请稍后重试", e);
                }
            }
            reserveDao.updateStatus(id, newStatus);
        } else {
            reserveDao.updateStatus(id, newStatus);
        }
    }

    @Override
    public void updateReserveStatus(Integer id, Integer status) {
        if (id == null || status == null) {
            log.error("更新预约状态失败：id 或 status 为空");
            throw new RuntimeException("更新预约状态失败：参数为空");
        }
        reserveDao.updateStatus(id, status);
    }

    /**
     * @param id
     */
    @Override
    public void deleteReserve(Long id) {
        reserveDao.deleteReserve(id);
    }

    @Override
    public void restoreReserve(Long id) {
        if (id == null) throw new IllegalArgumentException("id 为空");
        Reserve r = reserveDao.selectById(id);
        if (r == null) throw new IllegalArgumentException("预约不存在");
        if (r.getStatus() == null || r.getStatus() != 4) throw new IllegalArgumentException("该记录不是已隐藏状态，无需恢复");
        Integer orig = r.getOriginalStatus();
        if (orig == null) throw new IllegalArgumentException("无法恢复：缺少原始状态");
        reserveDao.updateStatus(id.intValue(), orig);
    }

    @Override
    public void markReserveUsed(Long reserveId) {
        if (reserveId == null) return;
        reserveDao.updateIsUsed(reserveId, 1);
    }

    @Override
    public Map<String, Object> usageStats(Long equipmentId, Long userId, String startTime, String endTime) {
        List<Reserve> list = reserveDao.queryByTimeRange(equipmentId, userId, startTime, endTime);
        Map<String, Object> result = new HashMap<>();
        result.put("total", list.size());
        Map<Long, Long> byEquip = list.stream().collect(Collectors.groupingBy(r -> Long.valueOf(r.getEquipmentId()), Collectors.counting()));
        List<Map<String, Object>> byEquipment = new ArrayList<>();
        byEquip.forEach((eid, cnt) -> {
            String name = list.stream().filter(r -> r.getEquipmentId() == eid).findFirst().map(Reserve::getEquipmentName).orElse(null);
            Map<String, Object> m = new HashMap<>();
            m.put("equipmentId", eid);
            m.put("equipmentName", name);
            m.put("count", cnt.intValue());
            byEquipment.add(m);
        });
        result.put("byEquipment", byEquipment);
        Map<Long, Long> byUsr = list.stream().filter(r -> r.getUserId() != null)
                .collect(Collectors.groupingBy(Reserve::getUserId, Collectors.counting()));
        List<Map<String, Object>> byUser = new ArrayList<>();
        byUsr.forEach((uid, cnt) -> {
            String name = list.stream().filter(r -> uid.equals(r.getUserId())).findFirst().map(Reserve::getUserName).orElse(null);
            Map<String, Object> m = new HashMap<>();
            m.put("userId", uid);
            m.put("userName", name);
            m.put("count", cnt.intValue());
            byUser.add(m);
        });
        result.put("byUser", byUser);
        return result;
    }

    @Override
    public List<Reserve> queryByTimeRange(Long equipmentId, Long userId, String startTime, String endTime) {
        return reserveDao.queryByTimeRange(equipmentId, userId, startTime, endTime);
    }
//
//
//    private Device getDeviceFromRemote(Long deviceId){
//        //1.获取到商品服务所在的所有机器ip+port
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-device");
//        ServiceInstance instance = instances.get(0);
//        //RCurl
//        String url = "http://"+instance.getHost()+":"+instance.getPort()+"/device/"+deviceId;
//        log.info("远程请求:{}",url);
//        //2.给远程发送请求
//        Device device = restTemplate.getForObject(url,Device.class);
//
//        return device;
//    }
//    //兜底回调
//    public Reserve createReserveFallBack(Long deviceId, Long userId, BlockException e) {
//        Reserve Reserve = new Reserve();
//        Reserve.setReserveId(0);
//        Reserve.setTotalAmount(new BigDecimal("0"));
//        Reserve.setUserId(userId);
//        Reserve.setUserName("未知用户,异常信息:"+e.getClass());
//        Reserve.setStatus(0);
//        return Reserve;
//    }
//    //进阶2:完成负载均衡发送请求
//    private Device getDeviceFromRemoteWithLoadBalance(Long deviceId){
//        //1.获取到商品服务所在的所有机器ip+port
//        ServiceInstance choose = loadBalancerClient.choose("service-device");
//        //远程url
//        String url = "http://"+choose.getHost()+":"+choose.getPort()+"/device/"+deviceId;
//        log.info("远程请求:{}",url);
//        //2.给远程发送请求
//        Device device = restTemplate.getForObject(url,Device.class);
//
//        return device;
//    }
//    //进阶3:基于注解的负载均衡
//    private Device getDeviceFromRemoteWithLoadBalanceAnnotation(Long deviceId){
//        String url = "http://service-device/device/"+deviceId;
//        //2.给远程发送请求; service-device会被动态替换
//        Device device = restTemplate.getForObject(url,Device.class);
//
//        return device;
//    }
}
