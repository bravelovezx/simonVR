package com.example.simon.mapper;

import com.example.simon.entity.WritingVersion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WritingVersionMapper {

    // 插入新版本（已改字段名）
    @Insert("INSERT INTO writing_versions (writing_id, version_name, content, created_at) " +
            "VALUES (#{writingId}, #{versionName}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "versionId")
    int insert(WritingVersion version);

    // 根据 versionId 和 userId 查询（用于权限验证）
    @Select("SELECT v.* FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.version_id = #{versionId} AND w.user_id = #{userId}")
    WritingVersion selectById(@Param("versionId") Integer versionId, @Param("userId") Integer userId);

    // 查询某篇作文的所有版本（已移除 version_number 排序）
    @Select("SELECT v.* FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.writing_id = #{writingId} AND w.user_id = #{userId} ORDER BY v.created_at ASC")
    List<WritingVersion> selectByWritingId(@Param("writingId") Integer writingId, @Param("userId") Integer userId);

    // 更新作文版本内容（权限校验）
    @Update("UPDATE writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "SET v.content = #{version.content}, v.version_name = #{version.versionName} " +
            "WHERE v.version_id = #{version.versionId} AND w.user_id = #{userId}")
    int updateById(@Param("version") WritingVersion version, @Param("userId") Integer userId);

    // 删除指定版本（权限校验）
    @Delete("DELETE v FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.version_id = #{versionId} AND w.user_id = #{userId}")
    int deleteById(@Param("versionId") Integer versionId, @Param("userId") Integer userId);

    // 删除某篇作文的所有版本（权限校验）
    @Delete("DELETE v FROM writing_versions v INNER JOIN writings w ON v.writing_id = w.writing_id " +
            "WHERE v.writing_id = #{writingId} AND w.user_id = #{userId}")
    int deleteByWritingId(@Param("writingId") Integer writingId, @Param("userId") Integer userId);
}
