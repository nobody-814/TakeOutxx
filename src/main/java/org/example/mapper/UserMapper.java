package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.User;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    // ==================== 查询操作 ====================

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM User WHERE id = #{id}")
    User findById(long id);

    /**
     * 根据用户名或手机号查找用户（用于登录）
     */
    @Select("SELECT * FROM User WHERE username = #{account} OR phone_number = #{account}")
    User findByUsernameOrPhone(String account);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM User WHERE username = #{username}")
    int countByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM User WHERE phone_number = #{phoneNumber}")
    int countByPhoneNumber(String phoneNumber);

    // 只查用户名
    @Select("SELECT * FROM User WHERE username = #{username}")
    User findByUsername(String username);

    // 只查手机号
    @Select("SELECT * FROM User WHERE phone_number = #{phoneNumber}")
    User findByPhone(String phoneNumber);

    // ==================== 插入操作 ====================

    /**
     * 新增用户
     */
    @Insert("INSERT INTO User (username, password, phone_number, role, avatar, status, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{phoneNumber}, #{role}, #{avatar}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // ==================== 更新操作 ====================

    /**
     * 用户自主更新基础信息（不含role和status）
     */
    @Update("UPDATE User SET username = #{username}, avatar = #{avatar}, update_at = #{updateAt} " +
            "WHERE id = #{id}")
    int updateBasicInfo(User user);

    /**
     * 修改密码
     */
    @Update("UPDATE User SET password = #{newPassword}, update_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") long id, @Param("newPassword") String newPassword);

    /**
     * 更新用户状态（禁用/启用）
     */
    @Update("UPDATE User SET status = #{status}, update_at = #{updateAt} WHERE id = #{id}")
    int updateStatus(@Param("id") long id, @Param("status") int status,
                     @Param("updateAt") LocalDateTime updateAt);

    /**
     * 更新手机号（需验证旧手机号）
     */
    @Update("UPDATE User SET phone_number = #{newPhone}, update_at = #{updateAt} " +
            "WHERE id = #{id} AND phone_number = #{oldPhone}")
    int updatePhone(@Param("id") long id, @Param("oldPhone") String oldPhone,
                    @Param("newPhone") String newPhone, @Param("updateAt") LocalDateTime updateAt);

    /**
     * 更新头像
     */
    @Update("UPDATE User SET avatar = #{avatar}, update_at = #{updateAt} WHERE id = #{id}")
    int updateAvatar(@Param("id") long id, @Param("avatar") String avatar,
                     @Param("updateAt") LocalDateTime updateAt);

    /**
     * 管理员修改用户角色
     */
    @Update("UPDATE User SET role = #{newRole}, update_at = #{updateAt} WHERE id = #{userId}")
    int adminUpdateRole(@Param("userId") long userId, @Param("newRole") int newRole,
                        @Param("updateAt") LocalDateTime updateAt);

    // ==================== 删除操作 ====================

    /**
     * 删除用户
     */
    @Delete("DELETE FROM User WHERE id = #{id}")
    int delete(long id);
}