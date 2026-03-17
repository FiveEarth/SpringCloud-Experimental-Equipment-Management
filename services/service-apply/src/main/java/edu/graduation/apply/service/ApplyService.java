package edu.graduation.apply.service;

import edu.graduation.reserve.bean.Apply;

import java.util.List;

public interface ApplyService {
    long createApply(Apply apply);

    List<Apply> queryAllApply();

    List<Apply> queryPending();

    List<Apply> queryApplyByUserId(Long userId);

    List<Apply> queryByTimeRange(Long equipmentId, Long userId, String startTime, String endTime);

    void modifyApply(Apply apply);

    void deleteApply(Long id);

    /** 恢复软删除的领用记录：将 status 恢复为 original_status */
    void restoreApply(Integer id);

    /**
     * 审批：1-通过（扣减库存），2-驳回；approvalUserName 用于冗余展示
     */
    void approveApply(Integer id, Integer approveStatus, Long approveUserId, String approvalUserName, String remarks);

    /**
     * 确认归还：填写归还时间，库存+1；equipmentStatus 0-正常 1-故障（故障时会将设备置为故障待修）
     */
    void confirmReturn(Integer id, Integer equipmentStatus);

    /**
     * 开始领用：预约通过后学生操作，根据预约生成领用记录（审批状态直接通过），扣减库存，标记预约已领用
     */
    long startBorrow(Long reserveId, Long userId, String userName);

    /**
     * 学生申请归还：将已通过的领用记录置为待归还审批
     */
    void applyReturn(Integer applyId, Long userId);

    /**
     * 待归还审批列表（return_status=1）
     */
    java.util.List<Apply> queryPendingReturn();

    /**
     * 归还审批：通过则 return_status=2、归还时间、库存+1；驳回则 return_status=0
     */
    void approveReturn(Integer id, boolean approved, Integer equipmentStatus, Long approveUserId, String approvalUserName, String remarks);
}
