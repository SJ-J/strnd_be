package com.strnd.api.common.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.List;

// List<String> <->> JSON 컬럼 변환 TypeHandler
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonStringListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // List<String> -> JSON 문자열로 직렬화하여 PreparedStatement에 세팅
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("List<String> JSON 직렬화 실패", e);
        }
    }

    // 컬럼명으로 ResultSet에서 값 조회 후 역직렬화
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    // 컬럼 인덱스로 ResultSet에서 값 조회 후 역직렬화
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    // CallableStatement에서 값 조회 후 역직렬화
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    // JSON 문자열 -> List<String> 역직렬화
    private List<String> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new SQLException("List<String> JSON 역직렬화 실패", e);
        }
    }
}