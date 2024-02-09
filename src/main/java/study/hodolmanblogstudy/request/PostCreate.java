package study.hodolmanblogstudy.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import study.hodolmanblogstudy.exception.InvalidRequest;

@Data
public class PostCreate {

    /**
     * 변수를 private final String title 처럼 final 키워드로 해두는 것도 좋은 방법이다.
     * 하지만 final 키워드일 경우 setter로 값 변경이 안되기 때문에 새로 객체를 만들어서 처리해야 한다.
     * 관련된 내용은 검색해보자.
     */
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    /**
     * 클래스 레벨에서 @Builder를 사용할 수도 있다.
     * 하지만 몇가지 문제가 발생할 수 있다.
     * 자세한 내용은 빌더패턴 불변성, 빌더패턴 장점에 대해 검색해보자.
     * 가능하면 @Builder는 생성자에 달아두는 것이 좋다. (이유에 대해 검색해보고, 실험해보자.)
     */
    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * {"code":"400","message":"잘못된 요청입니다.","validation":{}}
     * 어떤 필드에 어떤 값이 잘못되었는지 파라미터로 넘긴다.
     */
    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest("title", "제목에 바보를 포함할 수 없습니다.");
        }
    }
}
