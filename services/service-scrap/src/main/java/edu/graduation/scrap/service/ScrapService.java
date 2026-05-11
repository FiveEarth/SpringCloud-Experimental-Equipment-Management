package edu.graduation.scrap.service;

import edu.graduation.scrap.bean.Scrap;

import java.util.List;

public interface ScrapService {

    long create(Scrap scrap);

    Scrap getById(Long id);

    List<Scrap> listPending();

    List<Scrap> listAll();

    /** 按审批状态筛选，null 表示全部 */
    List<Scrap> listFiltered(Integer approvalStatus);

    /** newResidualValue 非 null 时覆盖残值（审批核定） */
    void approve(Long id, Integer approvalStatus, Long approvalUserId, String approvalUserName,
                 String disposalMethod, java.math.BigDecimal newResidualValue);
}
