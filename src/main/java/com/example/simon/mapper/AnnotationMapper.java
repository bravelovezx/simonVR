package com.example.simon.mapper;

import com.example.simon.entity.Annotation;
import com.example.simon.entity.Position;
import com.example.simon.typehandler.PositionTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnotationMapper {

    @Insert("INSERT INTO annotations (user_id, position_json, original, annotation_content, created_at, updated_at) " +
            "VALUES (#{userId}, #{position, typeHandler=com.example.simon.typehandler.PositionTypeHandler}, #{original}, #{annotationContent}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "annotationId")
    int insert(Annotation annotation);

    @Select("SELECT annotation_id, user_id, position_json as position, original, annotation_content, created_at, updated_at FROM annotations WHERE annotation_id = #{annotationId} AND user_id = #{userId}")
    @Results({
        @Result(property = "position", column = "position", typeHandler = PositionTypeHandler.class)
    })
    Annotation selectByIdAndUserId(@Param("annotationId") Integer annotationId, @Param("userId") Integer userId);

    @Select("SELECT annotation_id, user_id, position_json as position, original, annotation_content, created_at, updated_at FROM annotations WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "position", column = "position", typeHandler = PositionTypeHandler.class)
    })
    List<Annotation> selectByUserId(@Param("userId") Integer userId);

    /**
     * 通过position中的module和refid查询对应批注
     */
    @Select("SELECT annotation_id, user_id, position_json as position, original, annotation_content, created_at, updated_at " +
            "FROM annotations WHERE user_id = #{userId} AND " +
            "JSON_EXTRACT(position_json, '$.module') = #{module} AND " +
            "JSON_EXTRACT(position_json, '$.refId') = #{refId} " +
            "ORDER BY JSON_EXTRACT(position_json, '$.startPos') ASC")
    @Results({
        @Result(property = "position", column = "position", typeHandler = PositionTypeHandler.class)
    })
    List<Annotation> selectByUserIdAndModuleAndRefId(@Param("userId") Integer userId, 
                                                     @Param("module") String module, 
                                                     @Param("refId") Integer refId);

    @Update("UPDATE annotations SET position_json = #{position, typeHandler=com.example.simon.typehandler.PositionTypeHandler}, original = #{original}, " +
            "annotation_content = #{annotationContent}, updated_at = #{updatedAt} " +
            "WHERE annotation_id = #{annotationId} AND user_id = #{userId}")
    int update(Annotation annotation);

    @Delete("DELETE FROM annotations WHERE annotation_id = #{annotationId} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("annotationId") Integer annotationId, @Param("userId") Integer userId);

    @Delete("DELETE FROM annotations WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM annotations WHERE annotation_id = #{annotationId} AND user_id = #{userId}")
    int countByIdAndUserId(@Param("annotationId") Integer annotationId, @Param("userId") Integer userId);
} 