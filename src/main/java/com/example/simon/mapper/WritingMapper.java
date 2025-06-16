package com.example.simon.mapper;

import com.example.simon.entity.Writing;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WritingMapper {

    // 插入记录，加入 direction 字段
    @Insert("INSERT INTO writings (user_id, writing_topic, source_type, direction, created_at, updated_at) " +
            "VALUES (#{userId}, #{writingTopic}, #{sourceType}, #{direction}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "writingId")
    int insert(Writing writing);

    // 根据 writing_id 和 user_id 查询，包含 direction 字段
    @Select("SELECT * FROM writings WHERE writing_id = #{writingId} AND user_id = #{userId}")
    Writing selectById(@Param("writingId") Integer writingId, @Param("userId") Integer userId);

    // 根据 user_id 查询该用户所有记录，包含 direction 字段
    @Select("SELECT * FROM writings WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<Writing> selectByUserId(Integer userId);

    // 更新记录时加入 direction 字段
    @Update("UPDATE writings SET writing_topic = #{writingTopic}, source_type = #{sourceType}, direction = #{direction}, updated_at = #{updatedAt} " +
            "WHERE writing_id = #{writingId} AND user_id = #{userId}")
    int update(Writing writing);

    // 删除记录，无需变动
    @Delete("DELETE FROM writings WHERE writing_id = #{writingId} AND user_id = #{userId}")
    int delete(@Param("writingId") Integer writingId, @Param("userId") Integer userId);
}