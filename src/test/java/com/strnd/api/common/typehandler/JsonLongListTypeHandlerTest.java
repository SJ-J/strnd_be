package com.strnd.api.common.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonLongListTypeHandlerTest {

    private JsonLongListTypeHandler handler;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @Mock
    private CallableStatement cs;

    @BeforeEach
    void setUp() {
        handler = new JsonLongListTypeHandler();
    }

    @Test
    @DisplayName("List<Long> → JSON 직렬화 후 PreparedStatement에 세팅")
    void setNonNullParameter_serializesToJson() throws SQLException {
        // given
        List<Long> input = List.of(1L, 2L, 3L);

        // when
        handler.setNonNullParameter(ps, 1, input, JdbcType.VARCHAR);

        // then: JSON 문자열로 직렬화되어 setString 호출 확인
        verify(ps).setString(1, "[1,2,3]");
    }

    @Test
    @DisplayName("컬럼명으로 JSON 조회 → List<Long> 역직렬화")
    void getNullableResult_byColumnName_deserializesList() throws SQLException {
        // given
        when(rs.getString("ids")).thenReturn("[10,20,30]");

        // when
        List<Long> result = handler.getNullableResult(rs, "ids");

        // then
        assertThat(result).containsExactly(10L, 20L, 30L);
    }

    @Test
    @DisplayName("컬럼 인덱스로 JSON 조회 → List<Long> 역직렬화")
    void getNullableResult_byColumnIndex_deserializesList() throws SQLException {
        // given
        when(rs.getString(1)).thenReturn("[5,6]");

        // when
        List<Long> result = handler.getNullableResult(rs, 1);

        // then
        assertThat(result).containsExactly(5L, 6L);
    }

    @Test
    @DisplayName("CallableStatement로 JSON 조회 → List<Long> 역직렬화")
    void getNullableResult_byCallableStatement_deserializesList() throws SQLException {
        // given
        when(cs.getString(1)).thenReturn("[100,200]");

        // when
        List<Long> result = handler.getNullableResult(cs, 1);

        // then
        assertThat(result).containsExactly(100L, 200L);
    }

    @Test
    @DisplayName("null 값 조회 → null 반환")
    void getNullableResult_nullJson_returnsNull() throws SQLException {
        // given
        when(rs.getString("ids")).thenReturn(null);

        // when
        List<Long> result = handler.getNullableResult(rs, "ids");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("빈 문자열 조회 → null 반환")
    void getNullableResult_blankJson_returnsNull() throws SQLException {
        // given
        when(rs.getString("ids")).thenReturn("   ");

        // when
        List<Long> result = handler.getNullableResult(rs, "ids");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("잘못된 JSON 형식 → SQLException 발생")
    void getNullableResult_invalidJson_throwsSqlException() throws SQLException {
        // given
        when(rs.getString("ids")).thenReturn("not-json");

        // when & then
        assertThatThrownBy(() -> handler.getNullableResult(rs, "ids"))
            .isInstanceOf(SQLException.class)
            .hasMessageContaining("List<Long> JSON 역직렬화 실패");
    }
}
