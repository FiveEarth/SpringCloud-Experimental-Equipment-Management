package edu.graduation.user.service.impl;

import edu.graduation.common.JwtUtils;
import edu.graduation.user.dao.UserDao;
import edu.graduation.user.model.*;
import edu.graduation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        SysUser user = userDao.selectByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在或已被禁用");
        }
        String stored = user.getPassword();
        boolean match = false;
        if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$"))) {
            match = passwordEncoder.matches(request.getPassword(), stored);
        } else {
            match = request.getPassword().equals(stored);
        }
        if (!match) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        List<String> roleCodes = userDao.selectRoleCodesByUserId(user.getId());
        List<String> permissionCodes = userDao.selectPermissionCodesByUserId(user.getId());

        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roles", roleCodes);
        claims.put("perms", permissionCodes);
        String token = JwtUtils.generateToken(user.getUsername(), claims);

        return UserLoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roleCodes)
                .permissions(permissionCodes)
                .token(token)
                .build();
    }

    /** 默认注册角色：学生 */
    private static final long DEFAULT_REGISTER_ROLE_ID = 3L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long register(RegisterRequest request) {
        if (request == null || request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (userDao.selectByUsernameForAdmin(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName() != null && !request.getRealName().isBlank()
                ? request.getRealName() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userDao.insert(user);
        String userName = user.getRealName();
        RoleVO role = userDao.selectRoleById(DEFAULT_REGISTER_ROLE_ID);
        String roleName = role != null ? role.getRoleName() : "学生";
        userDao.insertUserRole(user.getId(), userName, DEFAULT_REGISTER_ROLE_ID, roleName);
        return user.getId();
    }

    @Override
    public List<UserManageVO> listUsers(Boolean includeDisabled) {
        Integer status = Boolean.TRUE.equals(includeDisabled) ? null : 1;
        List<SysUser> users = userDao.selectAll(status);
        List<UserManageVO> result = new ArrayList<>();
        for (SysUser u : users) {
            UserManageVO vo = new UserManageVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setRealName(u.getRealName());
            vo.setPhone(u.getPhone());
            vo.setEmail(u.getEmail());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            vo.setRoleIds(userDao.selectRoleIdsByUserId(u.getId()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addUser(UserAddRequest request) {
        if (request == null || request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (userDao.selectByUsernameForAdmin(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName() != null ? request.getRealName() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        userDao.insert(user);
        String userName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                RoleVO role = userDao.selectRoleById(roleId);
                String roleName = role != null ? role.getRoleName() : null;
                userDao.insertUserRole(user.getId(), userName, roleId, roleName);
            }
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        SysUser user = new SysUser();
        user.setId(request.getId());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        userDao.updateById(user);
        if (request.getRealName() != null) {
            userDao.updateUserRoleUserName(request.getId(), request.getRealName());
        }
        if (request.getRoleIds() != null) {
            userDao.deleteUserRolesByUserId(request.getId());
            String userName = request.getRealName();
            if (userName == null) {
                SysUser existing = userDao.selectById(request.getId());
                userName = existing != null ? existing.getRealName() : "";
            }
            for (Long roleId : request.getRoleIds()) {
                RoleVO role = userDao.selectRoleById(roleId);
                String roleName = role != null ? role.getRoleName() : null;
                userDao.insertUserRole(request.getId(), userName, roleId, roleName);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (id == null) throw new IllegalArgumentException("用户ID不能为空");
        userDao.deleteUserRolesByUserId(id);
        userDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null) throw new IllegalArgumentException("用户ID不能为空");
        userDao.deleteUserRolesByUserId(userId);
        if (roleIds != null) {
            SysUser user = userDao.selectById(userId);
            String userName = user != null ? user.getRealName() : "";
            for (Long roleId : roleIds) {
                RoleVO role = userDao.selectRoleById(roleId);
                String roleName = role != null ? role.getRoleName() : null;
                userDao.insertUserRole(userId, userName, roleId, roleName);
            }
        }
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("用户ID不能为空");
        if (newPassword == null || newPassword.isBlank()) throw new IllegalArgumentException("新密码不能为空");
        userDao.updatePasswordById(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public List<RoleVO> listRoles() {
        return userDao.selectAllRoles();
    }
}
