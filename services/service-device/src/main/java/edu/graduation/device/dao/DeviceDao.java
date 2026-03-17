package edu.graduation.device.dao;

import edu.graduation.device.bean.Device;
import edu.graduation.reserve.bean.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceDao {

    List<Device> getDevices();

    List<Device> getDevicesBorrowable();

    /** 单条插入设备并回填主键 id（用于新增时同步创建 asset 实例） */
    void insertOne(Equipment item);

    @Deprecated
    void insertEquipmentBatch(@Param("list") List<Equipment> list);

    void deleteEquipmentBatch(@Param("ids") List<Long> ids);

    void updateEquipmentStatus(@Param("id") Long id, @Param("status") Integer status);

    Integer selectCountById(@Param("id") Long id);

    void updateCount(@Param("id") Long id, @Param("delta") int delta);

    void updateEquipment(Device device);

    /** 统计这些设备类型下仍为「领用中」(status=1) 的实例数，用于删除前校验 */
    int countAssetInUseByEquipmentIds(@Param("ids") List<Long> ids);

    /** 统计这些设备类型下已通过但未归还的领用记录数 */
    int countUnreturnedApplyByEquipmentIds(@Param("ids") List<Long> ids);

    /** 统计这些设备类型下未完成的维修单数（0-待处理 1-进行中） */
    int countUnfinishedMaintainByEquipmentIds(@Param("ids") List<Long> ids);
}
