package com.example.simon.mapper;

import com.example.simon.entity.WritingVersion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WritingVersionMapper {

    @Insert("INSERT INTO writing_versions (writing_id, version_number, user_draft, corrected_draft, created_at) " +
            "VALUES (#{writingId}, #{versionNumber}, #{userDraft}, #{correctedDraft}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "versionId")
    int insert(WritingVersion version);

    @Select("SELECT v.* FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.version_id = #{versionId} AND w.user_id = #{userId}")
    WritingVersion selectById(@Param("versionId") Integer versionId, @Param("userId") Integer userId);

    @Select("SELECT v.* FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.writing_id = #{writingId} AND w.user_id = #{userId} ORDER BY version_number")
    List<WritingVersion> selectByWritingId(@Param("writingId") Integer writingId, @Param("userId") Integer userId);

    @Delete("DELETE v FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.version_id = #{versionId} AND w.user_id = #{userId}")
    int deleteById(@Param("versionId") Integer versionId, @Param("userId") Integer userId);

    @Delete("DELETE v FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.writing_id = #{writingId} AND w.user_id = #{userId}")
    int deleteByWritingId(@Param("writingId") Integer writingId, @Param("userId") Integer userId);
}
