package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class UncheckedTest {

    @Test
    @DisplayName("예외 처리")
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    @DisplayName("예외 던지기")
    void unchecked_throw() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUncheckedException.class);
    }
    
    /**
     * RuntimeException을 상속받는 언체크 예외 생성
     */
    static class MyUncheckedException extends RuntimeException {

        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 언체크 예외는 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 처리하지 않으면 자동으로 밖으로 던져진다.
     */
    static class Service {

        Repository repository = new Repository();

        // 필요할 경우 예외를 직접 처리
        public void callCatch() {

            try {
                repository.call();
            } catch(MyUncheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        // 예외를 throws로 던지지 않아도 자동으로 상위에 예외가 던져진다.
        public void callThrow() {
            repository.call();
        }
    }
    
    static class Repository {

        public void call() {
            throw new MyUncheckedException("언체크 예외");
        }
    }
}
