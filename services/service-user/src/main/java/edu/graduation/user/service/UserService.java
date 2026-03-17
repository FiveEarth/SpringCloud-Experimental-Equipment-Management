package edu.graduation.user.service;

import edu.graduation.user.model.*;

import java.util.List;

public interface UserService {

    UserLoginResponse login(UserLoginRequest request);

    long register(RegisterRequest request);

    List<UserManageVO> listUsers(Boolean includeDisabled);

    long addUser(UserAddRequest request);

    void updateUser(UserUpdateRequest request);

    void deleteUser(Long id);

    void assignRoles(Long userId, List<Long> roleIds);

    void resetPassword(Long userId, String newPassword);

    List<RoleVO> listRoles();
}
