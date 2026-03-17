package edu.graduation.maintain.service;

import edu.graduation.maintain.bean.Maintain;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MaintainService {
    long createMaintain(Maintain maintain);

    List<Maintain> queryAll();

    List<Maintain> queryByEquipmentId(Long equipmentId);

    List<Maintain> queryByApplyUserId(Long applyUserId);

    List<Maintain> queryPending();

    List<Maintain> queryPendingAndInProgress();

    List<Maintain> queryPendingOverdue24h();

    java.util.Map<String, Object> maintainStats(String startTime, String endTime);

    List<Maintain> queryAudit(Long equipmentId, Long userId, String startTime, String endTime);

    void acceptOrder(Long id, Long assignUserId, String assignUserName);

    void rejectOrder(Long id, Long assignUserId, String reason);

    void modifyMaintain(Maintain maintain);

    void deleteMaintain(Long id);

    /** 恢复软删除的维修记录：将 progress_status 恢复为 original_status */
    void restoreMaintain(Long id);

    /** 维修员完成维修：成功则设备恢复正常(status=0)，失败则自动提交报废申请 */
    void completeRepair(Long maintainId, Boolean success, java.math.BigDecimal cost, String maintainContent);
}