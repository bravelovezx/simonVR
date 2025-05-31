package com.example.simon.mapper;

import com.example.simon.entity.Accumulation;
import com.example.simon.typehandler.PositionTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface AccumulationMapper {

    /**
     * 插入积累记录
     */
    @Insert("INSERT INTO accumulations (user_id, type, content, meaning, position_json, created_at) " +
            "VALUES (#{userId}, #{type}, #{content}, #{meaning}, #{position,typeHandler=com.example.simon.typehandler.PositionTypeHandler}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "accumulationId")
    void insert(Accumulation accumulation);

    /**
     * 根据ID和用户ID查询积累记录（权限验证）
     */
    @Select("SELECT accumulation_id, user_id, type, content, meaning, position_json, created_at " +
            "FROM accumulations WHERE accumulation_id = #{accumulationId} AND user_id = #{userId}")
    @Results(id = "accumulationResultMap", value = {
            @Result(property = "accumulationId", column = "accumulation_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "type", column = "type", jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "content", jdbcType = JdbcType.VARCHAR),
            @Result(property = "meaning", column = "meaning", jdbcType = JdbcType.VARCHAR),
            @Result(property = "position", column = "position_json", jdbcType = JdbcType.VARCHAR, typeHandler = PositionTypeHandler.class),
            @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP)
    })
    Accumulation selectByIdAndUserId(Integer accumulationId, Integer userId);

    /**
     * 根据用户ID查询积累记录列表
     */
    @Select("SELECT accumulation_id, user_id, type, content, meaning, position_json, created_at " +
            "FROM accumulations WHERE user_id = #{userId} ORDER BY created_at DESC")
    @ResultMap("accumulationResultMap")
    List<Accumulation> selectByUserId(Integer userId);

    /**
     * 根据用户ID和类型查询积累记录列表
     */
    @Select("SELECT accumulation_id, user_id, type, content, meaning, position_json, created_at " +
            "FROM accumulations WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    @ResultMap("accumulationResultMap")
    List<Accumulation> selectByUserIdAndType(Integer userId, String type);

    /**
     * 更新积累记录
     */
    @Update("UPDATE accumulations SET type = #{type}, content = #{content}, meaning = #{meaning}, " +
            "position_json = #{position,typeHandler=com.example.simon.typehandler.PositionTypeHandler} WHERE accumulation_id = #{accumulationId}")
    void update(Accumulation accumulation);

    /**
     * 根据ID和用户ID删除积累记录（权限验证）
     */
    @Delete("DELETE FROM accumulations WHERE accumulation_id = #{accumulationId} AND user_id = #{userId}")
    int deleteByIdAndUserId(Integer accumulationId, Integer userId);

    /**
     * 根据用户ID删除所有积累记录
     */
    @Delete("DELETE FROM accumulations WHERE user_id = #{userId}")
    int deleteAllByUserId(Integer userId);
} 