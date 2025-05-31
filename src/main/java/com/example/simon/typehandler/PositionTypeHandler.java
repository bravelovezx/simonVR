package com.example.simon.typehandler;

import com.example.simon.entity.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Position类型处理器
 * 处理Position对象与数据库JSON字段之间的转换
 */
@MappedTypes(Position.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PositionTypeHandler extends BaseTypeHandler<Position> {
    
    private static final Logger logger = LoggerFactory.getLogger(PositionTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Position parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            logger.debug("Converting Position to JSON: {} -> {}", parameter, json);
            ps.setString(i, json);
        } catch (JsonProcessingException e) {
            logger.error("Error converting Position to JSON: {}", parameter, e);
            throw new SQLException("Error converting Position to JSON", e);
        }
    }

    @Override
    public Position getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parsePosition(json);
    }

    @Override
    public Position getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parsePosition(json);
    }

    @Override
    public Position getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parsePosition(json);
    }
    
    /**
     * 解析JSON字符串为Position对象
     */
    private Position parsePosition(String json) {
        if (json == null || json.trim().isEmpty()) {
            logger.debug("JSON string is null or empty, returning null Position");
            return null;
        }
        
        try {
            Position position = objectMapper.readValue(json, Position.class);
            logger.debug("Converting JSON to Position: {} -> {}", json, position);
            
            // 验证转换后的Position对象
            if (!position.isValid()) {
                logger.warn("Parsed Position is invalid: {}", position);
            }
            
            return position;
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON to Position: {}", json, e);
            
            // 尝试创建默认的Position对象
            logger.warn("Creating default Position due to JSON parsing error");
            Position defaultPosition = new Position();
            defaultPosition.setModule("unknown");
            defaultPosition.setRefId(0);
            defaultPosition.setRow(1);
            defaultPosition.setColumn(1);
            return defaultPosition;
        }
    }
} 