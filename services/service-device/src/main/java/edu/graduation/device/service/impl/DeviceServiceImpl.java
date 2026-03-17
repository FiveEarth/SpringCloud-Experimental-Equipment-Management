package edu.graduation.device.service.impl;

import edu.graduation.device.bean.Asset;
import edu.graduation.device.bean.Device;
import edu.graduation.device.dao.AssetDao;
import edu.graduation.device.dao.DeviceDao;
import edu.graduation.device.service.DeviceService;
import edu.graduation.reserve.bean.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private AssetDao assetDao;

    @Override
    public Device getDeviceById(Long deviceId) {
        List<Device> list = deviceDao.getDevices();
        if (list == null) return null;
        return list.stream().filter(d -> deviceId.equals(d.getDeviceId())).findFirst().orElse(null);
    }

    @Override
    public List<Device> getDevices() {
        return deviceDao.getDevices();
    }

    @Override
    public List<Device> getDevicesBorrowable() {
        return deviceDao.getDevicesBorrowable();
    }

    @Override
    public void addEquipments(List<Equipment> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Equipment e : list) {
            int cnt = (e.getCount() != null && e.getCount() > 0) ? e.getCount() : 1;
            if (e.getStatus() == null) e.setStatus(0);
            if (e.getStatus_text() == null) e.setStatus_text("在库");
            deviceDao.insertOne(e);
            long equipmentId = (e.getId() != null && e.getId() > 0) ? e.getId().longValue() : 0;
            if (equipmentId <= 0) continue;
            // 实例号统一格式：{设备编号}-{2位序号}；设备编号为空时用 EQ{设备ID}
            String prefix = (e.getEquipment_code() != null && !e.getEquipment_code().trim().isEmpty())
                    ? e.getEquipment_code().trim() : ("EQ" + equipmentId);
            List<Asset> assets = new ArrayList<>();
            for (int i = 1; i <= cnt; i++) {
                Asset a = new Asset();
                a.setEquipmentId(equipmentId);
                a.setAssetCode(prefix + "-" + String.format("%02d", i));
                a.setStatus(0);
                assets.add(a);
            }
            if (!assets.isEmpty()) {
                assetDao.insertBatch(assets);
            }
        }
    }

    @Override
    public void deleteEquipments(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 仅当所有实例已归还且维修已完成时才允许删除
        int inUse = deviceDao.countAssetInUseByEquipmentIds(ids);
        if (inUse > 0) {
            throw new IllegalStateException("无法删除：存在实例仍在领用中，请先完成归还后再试");
        }
        int unreturned = deviceDao.countUnreturnedApplyByEquipmentIds(ids);
        if (unreturned > 0) {
            throw new IllegalStateException("无法删除：存在未归还的领用记录，请先完成归还审批后再试");
        }
        int unfinished = deviceDao.countUnfinishedMaintainByEquipmentIds(ids);
        if (unfinished > 0) {
            throw new IllegalStateException("无法删除：存在未完成的维修单（待处理或进行中），请先完成维修后再试");
        }
        try {
            assetDao.deleteByEquipmentIds(ids);
            deviceDao.deleteEquipmentBatch(ids);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("无法删除：该设备存在预约或报废等关联记录，请先取消预约或处理后再试");
        }
    }

    @Override
    public void changeEquipmentStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return;
        }
        deviceDao.updateEquipmentStatus(id, status);
    }

    @Override
    public void updateEquipmentCount(Long id, int delta) {
        if (id == null) {
            return;
        }
        if (delta < 0) {
            Integer current = deviceDao.selectCountById(id);
            if (current == null || current + delta < 0) {
                throw new IllegalStateException("设备库存不足，当前可借数量：" + (current != null ? current : 0));
            }
        }
        deviceDao.updateCount(id, delta);
    }

    @Override
    public void updateEquipment(Device device) {
        if (device == null || device.getDeviceId() <= 0) {
            return;
        }
        deviceDao.updateEquipment(device);
    }

    @Override
    public Long allocateAsset(Long equipmentId) {
        if (equipmentId == null) {
            throw new IllegalArgumentException("equipmentId 不能为空");
        }
        Asset a = assetDao.selectOneAvailable(equipmentId);
        if (a == null || a.getId() == null) {
            throw new IllegalStateException("该设备类型暂无在库实例，无法分配");
        }
        assetDao.updateStatus(a.getId(), 1);
        return a.getId();
    }

    @Override
    public void releaseAsset(Long assetId) {
        if (assetId == null) return;
        assetDao.updateStatus(assetId, 0);
    }

    @Override
    public void updateAssetStatus(Long assetId, Integer status) {
        if (assetId == null || status == null) return;
        assetDao.updateStatus(assetId, status);
    }

    @Override
    public List<Asset> listAssetsByEquipmentId(Long equipmentId) {
        if (equipmentId == null) return List.of();
        return assetDao.listByEquipmentId(equipmentId);
    }
}
