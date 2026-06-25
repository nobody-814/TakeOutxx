package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.User;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    // ==================== 鏌ヨ鎿嶄綔 ====================
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

    // ==================== 鎻掑叆鎿嶄綔 ====================
    @Insert("INSERT INTO User (username, password, phone_number, role, avatar, status, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{phoneNumber}, #{role}, #{avatar}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // ==================== 鏇存柊鎿嶄綔 ====================

    /**
     * 鐢ㄦ埛鑷富鏇存柊鍩虹淇℃伅锛堜笉鍚玶ole鍜宻tatus锛?
     * 淇锛氬皢 #{updateAt} 鏀逛负 #{updatedAt}锛屽尮閰嶅疄浣撶被瀛楁鍚?
     */
    @Update("UPDATE User SET username = #{username}, phone_number = #{phoneNumber}, avatar = #{avatar}, address = #{address}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateBasicInfo(User user);

    /**
     * 淇敼瀵嗙爜
     */
    @Update("UPDATE User SET password = #{newPassword}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") long id, @Param("newPassword") String newPassword);

    /**
     * 鏇存柊鐢ㄦ埛鐘舵€侊紙绂佺敤/鍚敤锛?
     */
    @Update("UPDATE User SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") long id, @Param("status") int status,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 鏇存柊鎵嬫満鍙凤紙闇€楠岃瘉鏃ф墜鏈哄彿锛?
     */
    @Update("UPDATE User SET phone_number = #{newPhone}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND phone_number = #{oldPhone}")
    int updatePhone(@Param("id") long id, @Param("oldPhone") String oldPhone,
                    @Param("newPhone") String newPhone, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 鏇存柊澶村儚
     */
    @Update("UPDATE User SET avatar = #{avatar}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateAvatar(@Param("id") long id, @Param("avatar") String avatar,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 绠＄悊鍛樹慨鏀圭敤鎴疯鑹?
     */
    @Update("UPDATE User SET role = #{newRole}, updated_at = #{updatedAt} WHERE id = #{userId}")
    int adminUpdateRole(@Param("userId") long userId, @Param("newRole") int newRole,
                        @Param("updatedAt") LocalDateTime updatedAt);

    // ==================== 鍒犻櫎鎿嶄綔 ====================
    @Delete("DELETE FROM User WHERE id = #{id}")
    int delete(long id);
}