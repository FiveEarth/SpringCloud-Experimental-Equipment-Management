package edu.graduation.scrap.service.impl;

import edu.graduation.scrap.bean.Scrap;
import edu.graduation.scrap.dao.ScrapDao;
import edu.graduation.scrap.feign.DeviceFeignClient;
import edu.graduation.scrap.service.ScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScrapServiceImpl implements ScrapService {

    @Autowired
    private ScrapDao scrapDao;

    @Autowired(required = false)
    private DeviceFeignClient deviceFeignClient;

    @Override
    public long create(Scrap scrap) {
        if (scrap.getEquipmentId() == null) {
            throw new IllegalArgumentException("设备类型ID不能为空");
        }
        if (scrap.getAssetId() == null) {
            throw new IllegalArgumentException("设备实例ID（assetId）不能为空，报废须精确到单台设备");
        }
        if (scrap.getApplyUserId() == null) {
            throw new IllegalArgumentException("申请人ID不能为空");
        }
        if (scrap.getResidualValue() == null) {
            scrap.setResidualValue(java.math.BigDecimal.ZERO);
        }
        if (scrap.getApprovalStatus() == null) {
            scrap.setApprovalStatus(0);
        }
        try {
            scrapDao.insert(scrap);
        } catch (DataIntegrityViolationException e) {
            String root = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : "";
            if (root != null && root.contains("uk_asset_id")) {
                throw new IllegalArgumentException("该设备实例已存在报废申请，请勿重复提交");
            }
            if (root != null && (root.contains("fk_scrap_asset") || root.contains("foreign key"))) {
                throw new IllegalArgumentException("设备实例不存在或已被删除，无法提交报废");
            }
            throw new IllegalArgumentException("报废申请提交失败：数据冲突，请检查设备与实例信息");
        }
        return scrap.getId();
    }

    @Override
    public Scrap getById(Long id) {
        return scrapDao.selectById(id);
    }

    @Override
    public List<Scrap> listPending() {
        return scrapDao.listPending();
    }

    @Override
    public List<Scrap> listAll() {
        return scrapDao.listAll();
    }

    @Override
    public List<Scrap> listFiltered(Integer approvalStatus) {
        return scrapDao.listFiltered(approvalStatus);
    }

    @Override
    public void approve(Long id, Integer approvalStatus, Long approvalUserId, String approvalUserName,
                        String disposalMethod, java.math.BigDecimal newResidualValue) {
        if (approvalStatus == null || (approvalStatus != 1 && approvalStatus != 2)) {
            throw new IllegalArgumentException("审批状态须为 1（通过）或 2（驳回）");
        }
        Scrap scrap = scrapDao.selectById(id);
        if (scrap == null) {
            throw new IllegalArgumentException("报废记录不存在");
        }
        if (scrap.getApprovalStatus() != null && scrap.getApprovalStatus() != 0) {
            throw new IllegalArgumentException("该记录已审批");
        }
        Scrap upd = new Scrap();
        upd.setId(id);
        upd.setApprovalStatus(approvalStatus);
        upd.setApprovalUserId(approvalUserId);
        if (approvalUserName != null && !approvalUserName.isBlank()) {
            upd.setApprovalUserName(approvalUserName.trim());
        }
        upd.setDisposalMethod(disposalMethod);
        if (approvalStatus == 1) {
            upd.setDisposalTime(java.time.LocalDateTime.now());
        }
        if (newResidualValue != null) {
            upd.setResidualValue(newResidualValue);
        }
        scrapDao.update(upd);
        if (approvalStatus == 1 && deviceFeignClient != null && scrap.getEquipmentId() != null) {
            try {
                // 实例粒度报废：只报废该 asset，不把整类设备直接置为已报废
                if (scrap.getAssetId() != null) {
                    deviceFeignClient.setAssetStatus(scrap.getAssetId(), 3);
                    deviceFeignClient.updateEquipmentCount(scrap.getEquipmentId(), -1);
                    log.info("报废审批通过，实例已报废 assetId={}，equipmentId={} count-1", scrap.getAssetId(), scrap.getEquipmentId());
                } else {
                    deviceFeignClient.updateEquipmentStatus(scrap.getEquipmentId(), 3);
                    log.info("报废审批通过，已置设备类型 equipmentId={} 为已报废", scrap.getEquipmentId());
                }
            } catch (Exception e) {
                log.warn("更新设备状态失败", e);
            }
        }
    }
}
