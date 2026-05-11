package edu.graduation.scrap.dao;

import edu.graduation.scrap.bean.Scrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScrapDao {

    int insert(Scrap scrap);

    Scrap selectById(@Param("id") Long id);

    List<Scrap> listPending();

    List<Scrap> listAll();

    List<Scrap> listFiltered(@Param("approvalStatus") Integer approvalStatus);

    int update(Scrap scrap);
}
