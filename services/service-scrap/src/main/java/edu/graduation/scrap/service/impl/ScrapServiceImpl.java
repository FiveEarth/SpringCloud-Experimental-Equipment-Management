package edu.graduation.scrap.service.impl;

import edu.graduation.scrap.bean.Scrap;
import edu.graduation.scrap.dao.ScrapDao;
import edu.graduation.scrap.feign.DeviceFeignClient;
import edu.graduation.scrap.service.ScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new IllegalArgumentException("设备ID不能为空");
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
        scrapDao.insert(scrap);
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
    public void approve(Long id, Integer approvalStatus, Long approvalUserId, String disposalMethod) {
        Scrap scrap = scrapDao.selectById(id);
        if (scrap == null) {
            throw new IllegalArgumentException("报废记录不存在");
        }
        if (scrap.getApprovalStatus() != null && scrap.getApprovalStatus() != 0) {
            throw new IllegalArgumentException("该记录已审批");
        }
        scrap.setId(id);
        scrap.setApprovalStatus(approvalStatus);
        scrap.setApprovalUserId(approvalUserId);
        scrap.setDisposalMethod(disposalMethod);
        if (approvalStatus != null && approvalStatus == 1) {
            scrap.setDisposalTime(java.time.LocalDateTime.now());
        }
        scrapDao.update(scrap);
        if (approvalStatus != null && approvalStatus == 1 && deviceFeignClient != null && scrap.getEquipmentId() != null) {
            try {
                deviceFeignClient.updateEquipmentStatus(scrap.getEquipmentId(), 3);
                log.info("报废审批通过，已置设备类型 equipmentId={} 为已报废", scrap.getEquipmentId());
            } catch (Exception e) {
                log.warn("更新设备状态失败", e);
            }
        }
    }
}
