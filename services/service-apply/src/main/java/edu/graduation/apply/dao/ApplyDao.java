package edu.graduation.apply.dao;

import edu.graduation.reserve.bean.Apply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplyDao {
    int insert(Apply apply);

    List<Apply> queryAllApply();

    List<Apply> queryPending();

    List<Apply> queryApplyByUserId(Long userId);

    List<Apply> queryByTimeRange(@Param("equipmentId") Long equipmentId, @Param("userId") Long userId,
                                @Param("startTime") String startTime, @Param("endTime") String endTime);

    Apply selectById(@Param("id") Integer id);

    List<Apply> queryPendingReturn();

    void modifyApply(Apply apply);

    void deleteApply(Long id);

    /** 恢复软删除：将 status 恢复为 original_status */
    int restoreApply(@Param("id") Integer id);
}
