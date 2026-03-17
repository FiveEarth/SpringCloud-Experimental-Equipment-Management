package edu.graduation.apply.service.impl;


import edu.graduation.apply.dao.ApplyDao;
import edu.graduation.apply.feign.DeviceFeignClient;
import edu.graduation.apply.feign.MaintainFeignClient;
import edu.graduation.apply.feign.ReserveFeignClient;
import edu.graduation.apply.service.ApplyService;
import edu.graduation.common.Result;
import edu.graduation.reserve.bean.Apply;
import edu.graduation.reserve.bean.Reserve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ApplyServiceImpl implements ApplyService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ApplyDao applyDao;

    @Autowired(required = false)
    private DeviceFeignClient deviceFeignClient;

    @Autowired(required = false)
    private ReserveFeignClient reserveFeignClient;

    @Autowired(required = false)
    private MaintainFeignClient maintainFeignClient;

    @Override
    public long createApply(Apply apply) {
        if (apply == null) {
            throw new IllegalArgumentException("创建领用/归还申请失败：参数为空");
        }
        if (apply.getUser_id() == null) {
            throw new IllegalArgumentException("创建领用/归还申请失败：用户ID为空");
        }
        if (apply.getEquipment_id() == null) {
            throw new IllegalArgumentException("创建领用/归还申请失败：设备ID为空");
        }
        if (apply.getApply_type() == null) {
            apply.setApply_type(0);
        }
        int rows = applyDao.insert(apply);
        if (rows <= 0) {
            throw new RuntimeException("创建领用/归还申请失败：数据库插入失败");
        }
        log.info("创建领用/归还申请成功：id={}", apply.getId());
        return apply.getId();
    }

    @Override
    public List<Apply> queryAllApply() {
        return applyDao.queryAllApply();
    }

    @Override
    public List<Apply> queryPending() {
        return applyDao.queryPending();
    }

    @Override
    public List<Apply> queryApplyByUserId(Long userId) {
        return applyDao.queryApplyByUserId(userId);
    }

    @Override
    public List<Apply> queryByTimeRange(Long equipmentId, Long userId, String startTime, String endTime) {
        return applyDao.queryByTimeRange(equipmentId, userId, startTime, endTime);
    }

    @Override
    public void modifyApply(Apply apply) {
        if (apply.getId() == null) {
            throw new IllegalArgumentException("修改申请失败：申请ID为空");
        }
        if (apply.getStatus() != null && apply.getStatus() == 4) {
            Apply current = applyDao.selectById(apply.getId());
            if (current != null && apply.getOriginal_status() == null) {
                // 软删除：记录当前状态到 original_status，便于恢复（已归还可视为业务状态 2）
                Integer prev = current.getStatus() != null ? current.getStatus() : null;
                if (prev == null && current.getReturn_status() != null && current.getReturn_status() == 2) {
                    prev = 2;
                }
                if (prev == null && current.getApprove_status() != null) {
                    prev = current.getApprove_status();
                }
                apply.setOriginal_status(prev);
            }
        }
        applyDao.modifyApply(apply);
    }

    @Override
    public void deleteApply(Long id) {
        applyDao.deleteApply(id);
    }

    @Override
    public void restoreApply(Integer id) {
        if (id == null) throw new IllegalArgumentException("id 为空");
        Apply a = applyDao.selectById(id);
        if (a == null) throw new IllegalArgumentException("申请不存在");
        if (a.getStatus() == null || a.getStatus() != 4) throw new IllegalArgumentException("该记录不是已隐藏状态，无需恢复");
        int n = applyDao.restoreApply(id);
        if (n == 0) throw new IllegalStateException("恢复失败");
    }

    @Override
    public void approveApply(Integer id, Integer approveStatus, Long approveUserId, String approvalUserName, String remarks) {
        Apply apply = applyDao.selectById(id);
        if (apply == null) throw new IllegalArgumentException("申请不存在");
        if (apply.getApprove_status() != null && apply.getApprove_status() != 0) {
            throw new IllegalArgumentException("该申请已审批");
        }
        apply.setId(id);
        apply.setApprove_status(approveStatus);
        apply.setApprove_user_id(approveUserId != null ? approveUserId.intValue() : null);
        apply.setApproval_user_name(approvalUserName);
        apply.setStatus_text(approveStatus != null && approveStatus == 1 ? "通过" : "驳回");
        apply.setApprove_time(LocalDateTime.now().format(DT));
        apply.setRemarks(remarks);
        // 审批通过：优先分配设备实例（asset），实现设备粒度；若无实例则不再扣减 count
        if (approveStatus != null && approveStatus == 1) {
            Long eqId = apply.getEquipment_id();
            if (eqId != null && eqId > 0L && deviceFeignClient != null) {
                try {
                    edu.graduation.common.Result<Long> alloc = deviceFeignClient.allocateAsset(eqId);
                    if (alloc != null && alloc.getCode() != null && alloc.getCode() == 200 && alloc.getData() != null) {
                        apply.setAsset_id(alloc.getData());
                        applyDao.modifyApply(apply);
                        log.info("领用审批通过，已分配实例 assetId={}", apply.getAsset_id());
                    } else {
                        deviceFeignClient.updateEquipmentCount(eqId, -1);
                        applyDao.modifyApply(apply);
                        log.info("领用审批通过，已扣减设备 id={} 数量 -1（无实例时兼容）", eqId);
                    }
                } catch (Exception e) {
                    log.error("分配实例/扣减数量失败 equipmentId={}", eqId, e);
                    throw new RuntimeException("设备分配失败，请稍后重试", e);
                }
            } else {
                applyDao.modifyApply(apply);
            }
            if (apply.getReserve_id() != null && reserveFeignClient != null) {
                try {
                    reserveFeignClient.markReserveUsed(apply.getReserve_id());
                } catch (Exception e) {
                    log.warn("标记预约已领用失败", e);
                }
            }
        } else {
            applyDao.modifyApply(apply);
        }
    }

    @Override
    public void confirmReturn(Integer id, Integer equipmentStatus) {
        Apply apply = applyDao.selectById(id);
        if (apply == null) throw new IllegalArgumentException("申请不存在");
        if (apply.getApprove_status() == null || apply.getApprove_status() != 1) {
            throw new IllegalArgumentException("仅能对已通过的领用确认归还");
        }
        if (apply.getReturn_time() != null && !apply.getReturn_time().isEmpty()) {
            throw new IllegalArgumentException("该申请已归还");
        }
        apply.setId(id);
        apply.setReturn_time(LocalDateTime.now().format(DT));
        if (equipmentStatus != null) {
            apply.setEquipment_status(equipmentStatus.toString());
        }
        applyDao.modifyApply(apply);
        if (deviceFeignClient != null) {
            try {
                if (apply.getAsset_id() != null) {
                    if (equipmentStatus != null && equipmentStatus == 1) {
                        deviceFeignClient.setAssetStatus(apply.getAsset_id(), 2);
                        log.info("确认归还选故障，实例置为维修中 assetId={}，不加回库存", apply.getAsset_id());
                    } else {
                        deviceFeignClient.releaseAsset(apply.getAsset_id());
                    }
                } else {
                    Long eqId = apply.getEquipment_id();
                    if (eqId != null && eqId > 0L) {
                        if (equipmentStatus != null && equipmentStatus == 1) {
                            deviceFeignClient.updateEquipmentStatus(eqId, 2);
                            log.info("确认归还选故障，设备置为故障待修 equipmentId={}，不加回库存", eqId);
                        } else {
                            deviceFeignClient.updateEquipmentCount(eqId, 1);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("释放实例/加回库存失败", e);
                throw new RuntimeException("加回设备失败，请稍后重试", e);
            }
        }
    }

    @Override
    public long startBorrow(Long reserveId, Long userId, String userName) {
        if (reserveId == null || userId == null) {
            throw new IllegalArgumentException("预约ID和用户ID不能为空");
        }
        if (reserveFeignClient == null) {
            throw new RuntimeException("预约服务不可用");
        }
        Result<Reserve> result = reserveFeignClient.getReserveById(reserveId);
        if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
            throw new IllegalArgumentException("预约不存在或已失效");
        }
        Reserve r = result.getData();
        if (r.getStatus() == null || r.getStatus() != 1) {
            throw new IllegalArgumentException("仅能对已通过的预约开始领用");
        }
        if (r.getIsUsed() != null && r.getIsUsed() == 1) {
            throw new IllegalArgumentException("该预约已领用");
        }
        if (r.getUserId() == null || !r.getUserId().equals(userId)) {
            throw new IllegalArgumentException("仅能对自己的预约开始领用");
        }
        Apply apply = new Apply();
        apply.setReserve_id(reserveId);
        apply.setEquipment_id(r.getEquipmentId());
        apply.setEquipment_name(r.getEquipmentName());
        apply.setUser_id(userId != null ? userId.intValue() : null);
        apply.setUser_name(userName);
        apply.setApply_type(0);
        int applyQty = (r.getReserveQuantity() != null && r.getReserveQuantity() >= 1) ? r.getReserveQuantity() : 1;
        apply.setApply_quantity(applyQty);
        apply.setApprove_status(1);
        apply.setApply_time(new Date());
        apply.setReturn_status(0);
        apply.setUse_time(new Date());
        apply.setPurpose(r.getPurpose() != null ? r.getPurpose() : "");
        Long eqId = r.getEquipmentId();
        // 数量已在预约审批通过时锁定，领用时不再扣减 count；仅当数量为 1 时尝试分配一台实例便于追踪
        if (eqId != null && eqId > 0L && deviceFeignClient != null && applyQty == 1) {
            try {
                edu.graduation.common.Result<Long> alloc = deviceFeignClient.allocateAsset(eqId);
                if (alloc != null && alloc.getCode() != null && alloc.getCode() == 200 && alloc.getData() != null) {
                    apply.setAsset_id(alloc.getData());
                    log.info("开始领用，已分配实例 assetId={}", apply.getAsset_id());
                }
            } catch (Exception e) {
                log.warn("分配实例失败（不影响领用）: {}", e.getMessage());
            }
        }
        applyDao.insert(apply);
        try {
            reserveFeignClient.markReserveUsed(reserveId);
        } catch (Exception e) {
            log.warn("标记预约已领用失败", e);
        }
        return apply.getId();
    }

    @Override
    public void applyReturn(Integer applyId, Long userId) {
        Apply apply = applyDao.selectById(applyId);
        if (apply == null) throw new IllegalArgumentException("申请不存在");
        if (apply.getUser_id() == null || userId == null || apply.getUser_id().longValue() != userId) {
            throw new IllegalArgumentException("仅能对自己的领用申请归还");
        }
        if (apply.getApprove_status() == null || apply.getApprove_status() != 1) {
            throw new IllegalArgumentException("仅能对已通过的领用申请归还");
        }
        if (apply.getReturn_time() != null && !apply.getReturn_time().isEmpty()) {
            throw new IllegalArgumentException("该领用已归还");
        }
        if (apply.getReturn_status() != null && apply.getReturn_status() == 1) {
            throw new IllegalArgumentException("已提交归还申请，请等待审批");
        }
        if (Integer.valueOf(2).equals(apply.getReturn_status())) {
            throw new IllegalArgumentException("该领用已归还");
        }
        apply.setReturn_status(1);
        apply.setReturn_apply_time(LocalDateTime.now().format(DT));
        applyDao.modifyApply(apply);
    }

    @Override
    public List<Apply> queryPendingReturn() {
        return applyDao.queryPendingReturn();
    }

    @Override
    public void approveReturn(Integer id, boolean approved, Integer equipmentStatus, Long approveUserId, String approvalUserName, String remarks) {
        Apply apply = applyDao.selectById(id);
        if (apply == null) throw new IllegalArgumentException("申请不存在");
        if (apply.getReturn_status() == null || apply.getReturn_status() != 1) {
            throw new IllegalArgumentException("仅能对待归还审批的记录进行审批");
        }
        apply.setReturn_status(approved ? 2 : 0);
        apply.setApproval_user_name(approvalUserName);
        apply.setRemarks(remarks);
        if (approved) {
            apply.setReturn_time(LocalDateTime.now().format(DT));
            if (equipmentStatus != null) {
                apply.setEquipment_status(equipmentStatus.toString());
            }
            applyDao.modifyApply(apply);
            if (deviceFeignClient != null) {
                try {
                    if (apply.getAsset_id() != null) {
                        if (equipmentStatus != null && equipmentStatus == 1) {
                            deviceFeignClient.setAssetStatus(apply.getAsset_id(), 2);
                            log.info("归还审批选故障，实例置为维修中 assetId={}，不加回库存", apply.getAsset_id());
                        } else {
                            deviceFeignClient.releaseAsset(apply.getAsset_id());
                            log.info("归还审批通过，已释放实例 assetId={}", apply.getAsset_id());
                        }
                    } else {
                        Long eqId = apply.getEquipment_id();
                        if (eqId != null && eqId > 0L) {
                            if (equipmentStatus != null && equipmentStatus == 1) {
                                deviceFeignClient.updateEquipmentStatus(eqId, 2);
                                log.info("归还审批选故障，设备置为故障待修 equipmentId={}，不加回库存", eqId);
                            } else {
                                int qty = apply.getApply_quantity() != null && apply.getApply_quantity() >= 1 ? apply.getApply_quantity() : 1;
                                deviceFeignClient.updateEquipmentCount(eqId, qty);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("释放实例/加回库存失败", e);
                    throw new RuntimeException("加回设备失败，请稍后重试", e);
                }
            }
            if (approved && equipmentStatus != null && equipmentStatus == 1 && maintainFeignClient != null) {
                try {
                    java.util.Map<String, Object> maintainBody = new java.util.HashMap<>();
                    maintainBody.put("equipmentId", apply.getEquipment_id());
                    maintainBody.put("assetId", apply.getAsset_id());
                    maintainBody.put("equipmentName", apply.getEquipment_name() != null ? apply.getEquipment_name() : "");
                    maintainBody.put("maintainType", 1);
                    maintainBody.put("applyUserId", apply.getUser_id() != null ? Long.valueOf(apply.getUser_id()) : 0L);
                    maintainBody.put("applyUserName", apply.getUser_name() != null ? apply.getUser_name() : "");
                    maintainBody.put("maintainContent", "归还时发现故障，需维修");
                    maintainBody.put("progressStatus", 0);
                    Result<Long> createResult = maintainFeignClient.createMaintain(maintainBody);
                    if (createResult != null && createResult.getCode() == 200 && createResult.getData() != null) {
                        log.info("归还审批选故障，已创建维修记录 maintainId={}", createResult.getData());
                    }
                } catch (Exception e) {
                    log.warn("创建维修记录失败（归还审批选故障）", e);
                }
            }
        } else {
            applyDao.modifyApply(apply);
        }
    }
}
