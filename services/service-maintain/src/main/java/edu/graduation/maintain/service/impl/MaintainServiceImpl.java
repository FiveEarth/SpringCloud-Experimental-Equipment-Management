package edu.graduation.maintain.service.impl;

import edu.graduation.maintain.bean.Maintain;
import edu.graduation.maintain.dao.MaintainDao;
import edu.graduation.maintain.service.MaintainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MaintainServiceImpl implements MaintainService {

    @Autowired
    private MaintainDao maintainDao;

    @Override
    public long createMaintain(Maintain maintain) {
        if (maintain == null) {
            throw new IllegalArgumentException("创建维护记录失败：参数为空");
        }
        if (maintain.getEquipmentId() == null) {
            throw new IllegalArgumentException("创建维护记录失败：设备ID为空");
        }
        if (maintain.getApplyUserId() == null) {
            throw new IllegalArgumentException("创建维护记录失败：申请人ID为空");
        }
        if (maintain.getMaintainContent() == null || maintain.getMaintainContent().trim().isEmpty()) {
            throw new IllegalArgumentException("创建维护记录失败：维护/维修内容不能为空");
        }
        if (maintain.getProgressStatus() == null) {
            maintain.setProgressStatus(0);
        }
        int rows = maintainDao.insert(maintain);
        if (rows <= 0) {
            throw new RuntimeException("创建维护记录失败：数据库插入失败");
        }
        if (maintain.getAssetId() != null && deviceFeignClient != null) {
            try {
                deviceFeignClient.setAssetStatus(maintain.getAssetId(), 2);
                log.info("维修记录已关联实例，置为维修中 assetId={}", maintain.getAssetId());
            } catch (Exception e) {
                log.warn("更新实例状态失败", e);
            }
        }
        log.info("创建维护记录成功：id={}", maintain.getId());
        return maintain.getId();
    }

    @Override
    public List<Maintain> queryAll() {
        return maintainDao.queryAll();
    }

    @Override
    public List<Maintain> queryByEquipmentId(Long equipmentId) {
        return maintainDao.queryByEquipmentId(equipmentId);
    }

    @Override
    public List<Maintain> queryByApplyUserId(Long applyUserId) {
        return maintainDao.queryByApplyUserId(applyUserId);
    }

    @Override
    public List<Maintain> queryPending() {
        return maintainDao.queryPending();
    }

    @Override
    public List<Maintain> queryPendingAndInProgress() {
        return maintainDao.queryPendingAndInProgress();
    }

    @Override
    public List<Maintain> queryPendingOverdue24h() {
        return maintainDao.queryPendingOverdue24h();
    }

    @Override
    public java.util.Map<String, Object> maintainStats(String startTime, String endTime) {
        java.util.List<Maintain> list = maintainDao.queryByTimeRange(startTime, endTime, null, null);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("total", list.size());
        long completed = list.stream().filter(m -> m.getProgressStatus() != null && m.getProgressStatus() == 2).count();
        result.put("completed", completed);
        java.math.BigDecimal totalCost = list.stream()
                .filter(m -> m.getCost() != null)
                .reduce(java.math.BigDecimal.ZERO, (a, m) -> a.add(m.getCost()), java.math.BigDecimal::add);
        result.put("totalCost", totalCost);
        return result;
    }

    @Override
    public List<Maintain> queryAudit(Long equipmentId, Long userId, String startTime, String endTime) {
        return maintainDao.queryByTimeRange(startTime, endTime, equipmentId, userId);
    }

    @Override
    public void acceptOrder(Long id, Long assignUserId, String assignUserName) {
        Maintain maintain = new Maintain();
        maintain.setId(id);
        maintain.setAssignUserId(assignUserId);
        maintain.setAssignUserName(assignUserName);
        maintain.setProgressStatus(1);
        maintainDao.modify(maintain);
        Maintain full = maintainDao.selectById(id);
        if (full != null && full.getAssetId() != null && deviceFeignClient != null) {
            try {
                deviceFeignClient.setAssetStatus(full.getAssetId(), 2);
            } catch (Exception e) {
                log.warn("更新实例状态为维修中失败", e);
            }
        }
    }

    @Override
    public void rejectOrder(Long id, Long assignUserId, String reason) {
        Maintain maintain = new Maintain();
        maintain.setId(id);
        maintain.setAssignUserId(assignUserId);
        maintain.setProgressStatus(2);
        String content = reason == null ? "" : reason;
        maintain.setMaintainContent(content);
        maintainDao.modify(maintain);
    }

    @Override
    public void modifyMaintain(Maintain maintain) {
        if (maintain.getId() == null) {
            throw new IllegalArgumentException("修改维护记录失败：记录ID为空");
        }
        if (maintain.getProgressStatus() != null && maintain.getProgressStatus() == 4) {
            Maintain current = maintainDao.selectById(maintain.getId());
            if (current != null && maintain.getOriginalStatus() == null) {
                maintain.setOriginalStatus(current.getProgressStatus());
            }
        }
        maintainDao.modify(maintain);
    }

    @Override
    public void deleteMaintain(Long id) {
        maintainDao.delete(id);
    }

    @Override
    public void restoreMaintain(Long id) {
        if (id == null) throw new IllegalArgumentException("id 为空");
        Maintain m = maintainDao.selectById(id);
        if (m == null) throw new IllegalArgumentException("维护记录不存在");
        if (m.getProgressStatus() == null || m.getProgressStatus() != 4) throw new IllegalArgumentException("该记录不是已隐藏状态，无需恢复");
        if (m.getOriginalStatus() == null) throw new IllegalArgumentException("无法恢复：缺少原始状态");
        int n = maintainDao.restoreMaintain(id);
        if (n == 0) throw new IllegalStateException("恢复失败");
    }

    @Autowired(required = false)
    private edu.graduation.maintain.feign.DeviceFeignClient deviceFeignClient;
    @Autowired(required = false)
    private edu.graduation.maintain.feign.ScrapFeignClient scrapFeignClient;

    @Override
    public void completeRepair(Long maintainId, Boolean success, java.math.BigDecimal cost, String maintainContent) {
        Maintain maintain = maintainDao.selectById(maintainId);
        if (maintain == null) {
            throw new IllegalArgumentException("维护记录不存在");
        }
        maintain.setId(maintainId);
        maintain.setProgressStatus(2);
        maintain.setCost(cost);
        maintain.setMaintainContent(maintainContent != null ? maintainContent : maintain.getMaintainContent());
        maintain.setMaintainTime(new java.sql.Timestamp(System.currentTimeMillis()));
        maintainDao.modify(maintain);
        Long equipmentId = maintain.getEquipmentId();
        Long assetId = maintain.getAssetId();
        if (Boolean.TRUE.equals(success)) {
            if (assetId != null && deviceFeignClient != null) {
                try {
                    deviceFeignClient.setAssetStatus(assetId, 0);
                    log.info("维修完成，实例已恢复在库 assetId={}", assetId);
                } catch (Exception e) {
                    log.warn("更新实例状态失败", e);
                }
            }
            if (equipmentId != null && deviceFeignClient != null) {
                try {
                    deviceFeignClient.updateEquipmentStatus(equipmentId, 0);
                    if (assetId == null) {
                        deviceFeignClient.updateEquipmentCount(equipmentId, 1);
                        log.info("维修完成（无实例模式），已加回设备库存 equipmentId={}", equipmentId);
                    }
                } catch (Exception e) {
                    log.warn("更新设备状态/加回库存失败", e);
                }
            }
        } else {
            if ((equipmentId != null || assetId != null) && scrapFeignClient != null) {
                try {
                    edu.graduation.scrap.bean.Scrap scrap = new edu.graduation.scrap.bean.Scrap();
                    scrap.setEquipmentId(equipmentId);
                    scrap.setAssetId(assetId);
                    scrap.setScrapReason(maintainContent != null && !maintainContent.isEmpty() ? maintainContent : "维修无法修复，转报废");
                    scrap.setResidualValue(java.math.BigDecimal.ZERO);
                    scrap.setApplyUserId(maintain.getAssignUserId() != null ? maintain.getAssignUserId() : maintain.getApplyUserId());
                    scrap.setApprovalStatus(0);
                    scrapFeignClient.createScrap(scrap);
                } catch (Exception e) {
                    log.warn("提交报废申请失败", e);
                }
            }
        }
    }
}