package com.example.simon.mapper;

import com.example.simon.entity.DialogueTurn;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DialogueTurnMapper {

        @Insert("INSERT INTO dialogue_turns (session_id, turn_number, speaker, raw_text, corrected_text, created_at) " +
                        "VALUES (#{sessionId}, #{turnNumber}, #{speaker}, #{rawText}, #{correctedText}, #{createdAt})")
        @Options(useGeneratedKeys = true, keyProperty = "turnId")
        int insert(DialogueTurn turn);

        @Select("SELECT t.* FROM dialogue_turns t " +
                        "INNER JOIN dialogue_sessions s ON t.session_id = s.session_id " +
                        "WHERE t.turn_id = #{turnId} AND s.user_id = #{userId}")
        @Results(id = "turnResultMap", value = {
                        @Result(property = "turnId", column = "turn_id"),
                        @Result(property = "sessionId", column = "session_id"),
                        @Result(property = "turnNumber", column = "turn_number"),
                        @Result(property = "speaker", column = "speaker"),
                        @Result(property = "rawText", column = "raw_text"),
                        @Result(property = "correctedText", column = "corrected_text"),
                        @Result(property = "createdAt", column = "created_at")
        })
        DialogueTurn selectByIdAndUserId(@Param("turnId") Integer turnId, @Param("userId") Integer userId);

        @Select("SELECT t.* FROM dialogue_turns t " +
                        "INNER JOIN dialogue_sessions s ON t.session_id = s.session_id " +
                        "WHERE t.session_id = #{sessionId} AND s.user_id = #{userId} " +
                        "ORDER BY t.turn_number")
        @ResultMap("turnResultMap")
        List<DialogueTurn> selectBySessionIdAndUserId(@Param("sessionId") Integer sessionId,
                        @Param("userId") Integer userId);

        @Update("UPDATE dialogue_turns t " +
                        "INNER JOIN dialogue_sessions s ON t.session_id = s.session_id " +
                        "SET t.raw_text = #{rawText}, t.corrected_text = #{correctedText} " +
                        "WHERE t.turn_id = #{turnId} AND s.user_id = #{userId}")
        int update(@Param("turnId") Integer turnId,
                        @Param("userId") Integer userId,
                        @Param("rawText") String rawText,
                        @Param("correctedText") String correctedText);

        @Delete("DELETE t FROM dialogue_turns t " +
                        "INNER JOIN dialogue_sessions s ON t.session_id = s.session_id " +
                        "WHERE t.turn_id = #{turnId} AND s.user_id = #{userId}")
        int deleteById(@Param("turnId") Integer turnId, @Param("userId") Integer userId);

        @Delete("DELETE t FROM dialogue_turns t " +
                        "INNER JOIN dialogue_sessions s ON t.session_id = s.session_id " +
                        "WHERE t.session_id = #{sessionId} AND s.user_id = #{userId}")
        int deleteBySessionId(@Param("sessionId") Integer sessionId, @Param("userId") Integer userId);
}