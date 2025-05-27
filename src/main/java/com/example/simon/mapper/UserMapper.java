package com.example.simon.mapper;

import com.example.simon.entity.User;
import com.example.simon.typehandler.ProfileInfoTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface UserMapper {
        @Insert("INSERT INTO users (username, email, password_hash, profile_info, created_at, updated_at) " +
                        "VALUES (#{username}, #{email}, #{passwordHash}, #{profileInfo,typeHandler=com.example.simon.typehandler.ProfileInfoTypeHandler}, #{createdAt}, #{updatedAt})")
        @Options(useGeneratedKeys = true, keyProperty = "userId")
        void insert(User user);

        @Select("SELECT user_id, username, email, password_hash, profile_info, created_at, updated_at " +
                        "FROM users WHERE username = #{username}")
        @Results(id = "userResultMap", value = {
                        @Result(property = "userId", column = "user_id", jdbcType = JdbcType.INTEGER),
                        @Result(property = "username", column = "username", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "email", column = "email", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "passwordHash", column = "password_hash", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "profileInfo", column = "profile_info", jdbcType = JdbcType.VARCHAR, typeHandler = ProfileInfoTypeHandler.class),
                        @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
                        @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
        })
        User findByUsername(String username);

        @Select("SELECT user_id, username, email, password_hash, profile_info, created_at, updated_at " +
                        "FROM users WHERE user_id = #{userId}")
        @ResultMap("userResultMap")
        User selectById(Integer userId);

        @Update("UPDATE users SET username = #{username}, email = #{email}, " +
                        "password_hash = #{passwordHash}, profile_info = #{profileInfo,typeHandler=com.example.simon.typehandler.ProfileInfoTypeHandler}, "
                        +
                        "updated_at = #{updatedAt} WHERE user_id = #{userId}")
        void update(User user);

        @Delete("DELETE FROM users WHERE user_id = #{userId}")
        void deleteById(Integer userId);

        @Results(id = "userResultMap", value = {
                        @Result(property = "userId", column = "user_id", jdbcType = JdbcType.INTEGER),
                        @Result(property = "username", column = "username", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "email", column = "email", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "passwordHash", column = "password_hash", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "profileInfo", column = "profile_info", jdbcType = JdbcType.VARCHAR),
                        @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
                        @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
        })
        void dummy(); // 这个方法只是用来定义可重用的ResultMap
}