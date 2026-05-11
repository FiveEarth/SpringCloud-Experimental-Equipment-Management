package edu.graduation.lab.service;

import edu.graduation.lab.bean.Lab;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LabService {

    List<Lab> listAll();

    void addLabs(List<Lab> labs);

    void deleteLabs(List<Long> ids);

    void updateLab(Lab lab);
}

