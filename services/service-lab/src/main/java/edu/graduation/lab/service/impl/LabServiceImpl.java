package edu.graduation.lab.service.impl;

import edu.graduation.lab.bean.Lab;
import edu.graduation.lab.dao.LabDao;
import edu.graduation.lab.service.LabService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabServiceImpl implements LabService {

    private final LabDao labDao;

    @Override
    public List<Lab> listAll() {
        return labDao.selectAll();
    }

    @Override
    public void addLabs(List<Lab> labs) {
        if (labs == null || labs.isEmpty()) {
            return;
        }
        labDao.insertBatch(labs);
    }

    @Override
    public void deleteLabs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        labDao.deleteBatch(ids);
    }

    @Override
    public void updateLab(Lab lab) {
        if (lab == null || lab.getId() == null) {
            return;
        }
        labDao.updateOne(lab);
    }
}

