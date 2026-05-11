package edu.graduation.lab.dao;

import edu.graduation.lab.bean.Lab;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LabDao {

    List<Lab> selectAll();

    void insertBatch(@Param("list") List<Lab> list);

    void deleteBatch(@Param("ids") List<Long> ids);

    int updateOne(Lab lab);
}

