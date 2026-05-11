package edu.graduation.maintain.dao;

import edu.graduation.maintain.bean.Maintain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MaintainDao {
    int insert(Maintain maintain);

    Maintain selectById(@Param("id") Long id);

    List<Maintain> queryAll();

    List<Maintain> queryByEquipmentId(Long equipmentId);

    List<Maintain> queryByApplyUserId(Long applyUserId);

    /** 申请人可见列表（排除用户软隐藏 progress_status=4） */
    List<Maintain> queryByApplyUserIdVisible(@Param("applyUserId") Long applyUserId);

    List<Maintain> queryPending();

    /** 维修员视角：待处理(0) + 进行中(1) */
    List<Maintain> queryPendingAndInProgress();

    List<Maintain> queryPendingOverdue24h();

    List<Maintain> queryByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                   @Param("equipmentId") Long equipmentId, @Param("userId") Long userId);

    void modify(Maintain maintain);

    void delete(Long id);

    /** 恢复软删除：将 progress_status 恢复为 original_status */
    int restoreMaintain(@Param("id") Long id);

    /** 申请人撤销：待处理且未指派时置为隐藏(4)，original_status=0 */
    int applicantSoftHideRevoke(@Param("id") Long id, @Param("applyUserId") Long applyUserId);

    /** 申请人软删已完成单：progress_status=4，original_status=2 */
    int applicantSoftHideCompleted(@Param("id") Long id, @Param("applyUserId") Long applyUserId);
}
