package hello.jdbc.repository.exception;

// 리포지토리 계층의 "키 중복 문제"에 대한 런타임 예외
public class MyDuplicateKeyException extends MyDbException {
    
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
