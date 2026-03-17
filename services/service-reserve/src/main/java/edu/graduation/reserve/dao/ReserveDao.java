package edu.graduation.reserve.dao;

import edu.graduation.reserve.bean.Reserve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReserveDao {
    int insert(Reserve reserve);

    Reserve selectById(@Param("id") Long id);

    List<Reserve> queryReserveByUserId(Long userId);

    List<Reserve> queryAllReserve();

    void modifyReserve(Reserve reserve);

    /** 仅更新预约状态（审批通过/驳回），不修改 equipment_id 等字段，避免外键错误 */
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    void updateIsUsed(@Param("id") Long id, @Param("isUsed") Integer isUsed);

    void deleteReserve(Long id);

    List<Reserve> queryByTimeRange(@Param("equipmentId") Long equipmentId, @Param("userId") Long userId,
                                   @Param("startTime") String startTime, @Param("endTime") String endTime);
}
