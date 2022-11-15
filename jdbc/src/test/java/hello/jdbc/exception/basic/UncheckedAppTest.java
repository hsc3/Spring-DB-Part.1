package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

/**
 * 언체크 예외
 * 복구 불가능한 예외의 공통 처리, 예외 의존관계 문제 해결
 */
@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(RuntimeConnectException.class);
    }

    @Test
    void printError() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch(Exception ex) {
            log.info("message={}", ex.getMessage(), ex);
        }
    }

    static class Controller {

        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {

        NetworkClient networkClient = new NetworkClient();
        Repository repository = new Repository();

        public void logic() {
            networkClient.call();
            repository.call();
        }
    }

    static class NetworkClient {

        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {

        public void call(){ // 발생한 체크 예외를 런타임 예외로 전환해서 던진다.
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e); // 기존 예외를 포함함으로써, 예외 출력시 스택 트레이스에서 기존 예외를 함께 확인할 수 있다.
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("예외 발생");
        }
    }

    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }
}
