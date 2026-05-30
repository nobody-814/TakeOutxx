package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.User;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    // ==================== 查询操作 ====================
    @Select("SELECT * FROM User WHERE id = #{id}")
    User findById(long id);

    @Select("SELECT * FROM User WHERE username = #{account} OR phone_number = #{account}")
    User findByUsernameOrPhone(String account);

    @Select("SELECT COUNT(*) FROM User WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM User WHERE phone_number = #{phoneNumber}")
    int countByPhoneNumber(String phoneNumber);

    @Select("SELECT * FROM User WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM User WHERE phone_number = #{phoneNumber}")
    User findByPhone(String phoneNumber);

    // ==================== 插入操作 ====================
    @Insert("INSERT INTO User (username, password, phone_number, role, avatar, status, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{phoneNumber}, #{role}, #{avatar}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // ==================== 更新操作 ====================

    /**
     * 用户自主更新基础信息（不含role和status）
     * 修正：将 #{updateAt} 改为 #{updatedAt}，匹配实体类字段名
     */
    @Update("UPDATE User SET username = #{username}, phone_number = #{phoneNumber}, avatar = #{avatar}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateBasicInfo(User user);

    /**
     * 修改密码
     */
    @Update("UPDATE User SET password = #{newPassword}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") long id, @Param("newPassword") String newPassword);

    /**
     * 更新用户状态（禁用/启用）
     */
    @Update("UPDATE User SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") long id, @Param("status") int status,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新手机号（需验证旧手机号）
     */
    @Update("UPDATE User SET phone_number = #{newPhone}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND phone_number = #{oldPhone}")
    int updatePhone(@Param("id") long id, @Param("oldPhone") String oldPhone,
                    @Param("newPhone") String newPhone, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新头像
     */
    @Update("UPDATE User SET avatar = #{avatar}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateAvatar(@Param("id") long id, @Param("avatar") String avatar,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 管理员修改用户角色
     */
    @Update("UPDATE User SET role = #{newRole}, updated_at = #{updatedAt} WHERE id = #{userId}")
    int adminUpdateRole(@Param("userId") long userId, @Param("newRole") int newRole,
                        @Param("updatedAt") LocalDateTime updatedAt);

    // ==================== 删除操作 ====================
    @Delete("DELETE FROM User WHERE id = #{id}")
    int delete(long id);
}