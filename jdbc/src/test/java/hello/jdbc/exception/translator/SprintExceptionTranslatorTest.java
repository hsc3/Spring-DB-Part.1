package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 스프링의 예외 변환 기능 (스프링이 제공하는 추상화 예외)
 */
@Slf4j
public class SprintExceptionTranslatorTest {

    DriverManagerDataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "SELECT BAD GRAMMAR";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode={}", errorCode); // SQL 문법 오류 errorCode : 42122
            log.info("error", e); 
        }
    }

    @Test
    void sqlExceptionTranslation() {
        String sql = "SELECT BAD GRAMMAR";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException translatedEx = exTranslator.translate("select", sql, e); // 예외 변환
            assertThat(translatedEx.getClass()).isEqualTo(BadSqlGrammarException.class);
            log.info("translatedEx", translatedEx);
        }
    }

}
