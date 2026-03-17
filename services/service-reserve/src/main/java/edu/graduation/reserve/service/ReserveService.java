package edu.graduation.reserve.service;


import edu.graduation.reserve.bean.Reserve;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReserveService {
    long createReserve(Reserve reserve);

    Reserve getById(Long id);

    List<Reserve> queryReserveByUserId(Long userId);

    List<Reserve> queryAllReserve();

    void modifyReserve(Reserve reserve);

    /** 仅更新预约状态（审批通过/驳回） */
    void updateReserveStatus(Integer id, Integer status);

    /** 审批预约：通过时扣减设备可用数量（锁定），驳回/取消时若原已通过则加回数量 */
    void approveReserve(Integer id, Integer newStatus);

    void deleteReserve(Long reserveId);

    /** 恢复软删除的预约：将 status 从 4 恢复为 original_status */
    void restoreReserve(Long id);

    void markReserveUsed(Long reserveId);

    java.util.Map<String, Object> usageStats(Long equipmentId, Long userId, String startTime, String endTime);

    List<Reserve> queryByTimeRange(Long equipmentId, Long userId, String startTime, String endTime);
    //下面是实际项目中用到的接口方法
//    Reserve createReserve(String equipmentCode, Long userId, String equipmentName, String equipmentModel, int labId, String purchaseDate, String specification, String manualUrl, Integer status, BigDecimal residualValue);
}
