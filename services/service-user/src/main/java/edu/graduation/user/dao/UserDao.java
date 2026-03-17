package edu.graduation.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.graduation.user.model.RoleVO;
import edu.graduation.user.model.SysUser;

import java.util.List;

@Mapper
public interface UserDao {

    SysUser selectById(@Param("id") Long id);

    SysUser selectByUsername(@Param("username") String username);

    /** 管理员用：按用户名查询，不限制 status */
    SysUser selectByUsernameForAdmin(@Param("username") String username);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    List<SysUser> selectAll(@Param("status") Integer status);

    int insert(SysUser user);

    int updateById(SysUser user);

    int updatePasswordById(@Param("id") Long id, @Param("password") String password);

    int deleteById(@Param("id") Long id);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    int deleteUserRolesByUserId(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("userName") String userName,
                      @Param("roleId") Long roleId, @Param("roleName") String roleName);

    int updateUserRoleUserName(@Param("userId") Long userId, @Param("userName") String userName);

    RoleVO selectRoleById(@Param("roleId") Long roleId);

    List<RoleVO> selectAllRoles();
}
