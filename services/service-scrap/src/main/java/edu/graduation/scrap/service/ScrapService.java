package edu.graduation.scrap.service;

import edu.graduation.scrap.bean.Scrap;

import java.util.List;

public interface ScrapService {

    long create(Scrap scrap);

    Scrap getById(Long id);

    List<Scrap> listPending();

    List<Scrap> listAll();

    void approve(Long id, Integer approvalStatus, Long approvalUserId, String disposalMethod);
}
