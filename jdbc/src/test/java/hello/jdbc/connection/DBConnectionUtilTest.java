package hello.jdbc.connection;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.assertj.core.api.Assertions.*;

class DBConnectionUtilTest {

    @Test // DB 연결 테스트
    void connection() {

        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }

}