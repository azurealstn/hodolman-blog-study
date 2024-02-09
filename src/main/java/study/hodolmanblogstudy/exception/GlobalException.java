package study.hodolmanblogstudy.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * abstract로 클래스를 생성한 이유
 * 1. public abstract int statusCode();
 *  -> status 코드에 대해 강제 구현
 * 2. 다른 곳에서 new로 객체 생성을 막는다.
 */
@Getter
public abstract class GlobalException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatus();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
