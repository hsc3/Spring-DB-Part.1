package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * 체크 예외
 * 컨트롤러나 서비스의 입장에서 예외를 처리할 수 없고 던져야 한다.
 * 이로 인한 예외 의존관계 문제가 발생한다.
 * 이러한 예외들은 상위 단계에서 공통적으로 처리되어야 한다. (서블릿의 필터, 스프링의 인터셉터, ControllerAdvice)
 */
@Slf4j
public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller {

        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {

        NetworkClient networkClient = new NetworkClient();
        Repository repository = new Repository();

        public void logic() throws ConnectException, SQLException { // 의존관계의 문제 발생
            networkClient.call();
            repository.call();
        }

    }

    static class NetworkClient {

        public void call() throws ConnectException {
            throw new ConnectException();
        }
    }

    static class Repository {

        public void call() throws SQLException {
            throw new SQLException();
        }
    }
}
