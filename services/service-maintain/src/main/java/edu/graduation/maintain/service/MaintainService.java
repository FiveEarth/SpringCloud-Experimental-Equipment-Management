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

    /** 申请人列表页：不含用户软隐藏(4) */
    List<Maintain> queryByApplyUserIdVisible(Long applyUserId);

    List<Maintain> queryPending();

    List<Maintain> queryPendingAndInProgress();

    List<Maintain> queryPendingOverdue24h();

    java.util.Map<String, Object> maintainStats(String startTime, String endTime);

    List<Maintain> queryAudit(Long equipmentId, Long userId, String startTime, String endTime);

    void acceptOrder(Long id, Long assignUserId, String assignUserName);

    void rejectOrder(Long id, Long assignUserId, String reason);

    void modifyMaintain(Maintain maintain);

    void deleteMaintain(Long id);

    /** 申请人撤销：仅待处理且未接单 */
    void applicantRevokePending(Long maintainId, Long applicantUserId);

    /** 申请人软删：仅已完成，管理员仍可见 */
    void applicantSoftHideCompleted(Long maintainId, Long applicantUserId);

    /** 恢复软删除的维修记录：将 progress_status 恢复为 original_status */
    void restoreMaintain(Long id);

    /**
     * 维修员完成维修：成功则设备恢复正常；失败则提交报废申请。
     * @param scrapResidualValue 转报废时写入报废单的残值（可为 null，按 0 处理）
     */
    void completeRepair(Long maintainId, Boolean success, java.math.BigDecimal cost, String maintainContent,
                        java.math.BigDecimal scrapResidualValue);
}