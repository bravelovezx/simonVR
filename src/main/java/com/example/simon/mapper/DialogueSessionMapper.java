package com.example.simon.mapper;

import com.example.simon.entity.DialogueSession;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DialogueSessionMapper {

        @Insert("INSERT INTO dialogue_sessions (user_id, scene, started_at, ended_at) " +
                        "VALUES (#{userId}, #{scene}, #{startedAt}, #{endedAt})")
        @Options(useGeneratedKeys = true, keyProperty = "sessionId")
        int insert(DialogueSession session);

        @Select("SELECT * FROM dialogue_sessions WHERE session_id = #{sessionId} AND user_id = #{userId}")
        @Results(id = "sessionResultMap", value = {
                        @Result(property = "sessionId", column = "session_id"),
                        @Result(property = "userId", column = "user_id"),
                        @Result(property = "scene", column = "scene"),
                        @Result(property = "startedAt", column = "started_at"),
                        @Result(property = "endedAt", column = "ended_at"),
        })
        DialogueSession selectByIdAndUserId(@Param("sessionId") Integer sessionId, @Param("userId") Integer userId);

        @Select("SELECT * FROM dialogue_sessions WHERE user_id = #{userId}")
        @ResultMap("sessionResultMap")
        List<DialogueSession> selectByUserId(Integer userId);

        @Update("UPDATE dialogue_sessions SET scene = #{scene}, ended_at = #{endedAt} " +
                        "WHERE session_id = #{sessionId} AND user_id = #{userId}")
        int update(DialogueSession session);

        @Delete("DELETE FROM dialogue_sessions WHERE session_id = #{sessionId} AND user_id = #{userId}")
        int deleteById(@Param("sessionId") Integer sessionId, @Param("userId") Integer userId);
}