package edu.graduation.device.service;

import edu.graduation.device.bean.Asset;
import edu.graduation.device.bean.Device;
import edu.graduation.reserve.bean.Equipment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DeviceService {

    Device getDeviceById(Long deviceId);

    List<Device> getDevices();

    List<Device> getDevicesBorrowable();

    /** 分配一台在库实例用于领用，返回实例ID，无可用则抛异常 */
    Long allocateAsset(Long equipmentId);

    /** 释放实例（归还后置为在库） */
    void releaseAsset(Long assetId);

    /** 更新实例状态（如维修中2、已报废3） */
    void updateAssetStatus(Long assetId, Integer status);

    List<Asset> listAssetsByEquipmentId(Long equipmentId);

    void addEquipments(List<Equipment> list);

    void deleteEquipments(List<Long> ids);

    void changeEquipmentStatus(Long id, Integer status);

    void updateEquipmentCount(Long id, int delta);

    void updateEquipment(Device device);
}
