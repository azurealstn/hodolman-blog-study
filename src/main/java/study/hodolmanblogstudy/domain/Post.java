package study.hodolmanblogstudy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

//    public PostEditor.PostEditorBuilder toEditor() {
//        return PostEditor.builder()
//                .title(title)
//                .content(content);
//    }

//    public void edit(PostEditor postEditor) {
//        title = postEditor.getTitle();
//        content = postEditor.getContent();
//    }

    /**
     * 클라이언트의 요구사항
     * title의 길이 제한을 10자로 제한해주세요.
     * 이러한 서비스의 정책을 넣지 말아야 한다. 절대로!
     */
//    public String getTitle() {
//        return this.title.substring(0, 10);
//    }
}
