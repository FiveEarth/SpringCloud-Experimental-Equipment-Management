package edu.graduation.device.dao;

import edu.graduation.device.bean.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetDao {

    List<Asset> listByEquipmentId(@Param("equipmentId") Long equipmentId);

    /** 取一台在库实例（status=0），按 id 升序 */
    Asset selectOneAvailable(@Param("equipmentId") Long equipmentId);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    Asset selectById(@Param("id") Long id);

    /** 统计某设备类型下在库实例数 */
    int countAvailableByEquipmentId(@Param("equipmentId") Long equipmentId);

    /** 批量插入设备实例（新增设备类型时按 count 生成实例编号 equipmentCode-01, 02, ...） */
    void insertBatch(@Param("list") java.util.List<Asset> list);

    /** 按设备类型批量删除实例（删除设备前先删子表，避免外键约束） */
    int deleteByEquipmentIds(@Param("ids") List<Long> ids);
}
