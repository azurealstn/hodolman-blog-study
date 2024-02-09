package study.hodolmanblogstudy.resposne;

import lombok.Builder;
import lombok.Getter;
import study.hodolmanblogstudy.domain.Post;

/**
 * 서비스 정책에 맞는 응답 클래스를 만든다.
 */
@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
