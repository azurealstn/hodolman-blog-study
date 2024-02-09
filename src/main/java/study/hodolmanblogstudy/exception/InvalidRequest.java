package study.hodolmanblogstudy.exception;

import lombok.Getter;

/**
 * 클라이언트 요구 정책 추가
 * PostController -> postV4 메서드
 *
 * status: 400 (클라이언트 잘못)
 */
@Getter
public class InvalidRequest extends GlobalException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    private String fieldName;
    private String message;

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String fieldName, String message) {
        super(MESSAGE);
//        this.fieldName = fieldName;
//        this.message = message;
        addValidation(fieldName, message);
    }

    public InvalidRequest(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}
