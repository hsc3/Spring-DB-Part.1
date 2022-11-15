package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    @Test
    @DisplayName("예외 처리")
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }
    
    @Test
    @DisplayName("예외 던지기")
    void checked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception을 상속받은 체크 예외 생성
     */
    static class MyCheckedException extends Exception {

        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 체크 예외는 잡아서 처리하거나, 던지거나 둘중 하나를 필수로 선택
     */
    static class Service {

        Repository repository = new Repository();


        // 예외를 잡아서 처리하는 코드 (try-catch)
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        // 예외를 밖으로 던지는 코드 (throws 작성)
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {

        public void call() throws MyCheckedException { // 예외를 던질 시, 메서드에 throws 예외를 필수로 작성
            throw new MyCheckedException("체크 예외");
        }
    }
}
