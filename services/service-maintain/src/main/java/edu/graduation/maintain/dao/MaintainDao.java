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
}
