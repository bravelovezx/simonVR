package com.example.simon.mapper;

import com.example.simon.entity.Writing;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WritingMapper {

    @Insert("INSERT INTO writings (user_id, writing_topic, source_type, created_at, updated_at) " +
            "VALUES (#{userId}, #{writingTopic}, #{sourceType}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "writingId")
    int insert(Writing writing);

    @Select("SELECT * FROM writings WHERE writing_id = #{writingId} AND user_id = #{userId}")
    Writing selectById(@Param("writingId") Integer writingId, @Param("userId") Integer userId);

    @Select("SELECT * FROM writings WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<Writing> selectByUserId(Integer userId);

    @Update("UPDATE writings SET writing_topic = #{writingTopic}, source_type = #{sourceType}, updated_at = #{updatedAt} " +
            "WHERE writing_id = #{writingId} AND user_id = #{userId}")
    int update(Writing writing);

    @Delete("DELETE FROM writings WHERE writing_id = #{writingId} AND user_id = #{userId}")
    int delete(@Param("writingId") Integer writingId, @Param("userId") Integer userId);
}
