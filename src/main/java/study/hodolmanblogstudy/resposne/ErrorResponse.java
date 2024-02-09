package study.hodolmanblogstudy.resposne;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 에러 응답은 회사마다, 팀마다 다를 수 있다.
 * {
 *     "code": "400",
 *     "message": "잘못된 요청입니다.",
 *     "validation": {
 *         "title": "값을 입력해주세요",
 *         ...
 *     }
 * }
 *
 * @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
 * 비어있는 데이터는 나타내지 않는 설정
 * 개인적으로 비어있는 데이터도 데이터이기 때문에 사용을 권장하지 않는다.
 */
//@RequiredArgsConstructor
@Getter
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final String code;
    private final String message;

    //TODO
    //추 후 Map을 클래스 형태로 변경하는 작업이 필요하다.
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation != null ? validation : new HashMap<>();
    }

    public void addValidation(String fieldName, String errorMessage) {
        this.validation.put(fieldName, errorMessage);
    }
}
