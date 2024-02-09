package study.hodolmanblogstudy.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.exception.PostNotFound;
import study.hodolmanblogstudy.repository.PostRepository;
import study.hodolmanblogstudy.request.PostCreate;
import study.hodolmanblogstudy.request.PostEdit;
import study.hodolmanblogstudy.request.PostSearch;
import study.hodolmanblogstudy.resposne.PostResponse;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("글 제목입니다.")
                .content("글 내용입니다.")
                .build();

        //Repository를 주입해야 한다.
        //Mock Repository를 사용하는 방법과 @Autowired로 주입받는 방법이 있다. (선택)
//        PostService postService = new PostService();

        // when
        postService.write(postCreate);

        // then
        assertThat(postRepository.count()).isEqualTo(1);

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("글 제목입니다.");
        assertThat(post.getContent()).isEqualTo("글 내용입니다.");
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        // given
        Post requestPost = Post.builder()
                .title("글 제목입니다.")
                .content("글 내용입니다.")
                .build();

        postRepository.save(requestPost);

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo("글 제목입니다.");
        assertThat(response.getContent()).isEqualTo("글 내용입니다.");

    }

    @Test
    @DisplayName("글 여러개 조회")
    void test3() {
        // given
        Post requestPost1 = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();

        Post requestPost2 = Post.builder()
                .title("foo2")
                .content("bar2")
                .build();

        postRepository.save(requestPost1);
        postRepository.save(requestPost2);

        // when
//        List<PostResponse> posts = postService.getList(1);
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
//        List<PostResponse> posts = postService.getList(pageable);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(5)
                .build();

        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertThat(posts.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test4() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build())
                .toList();

        postRepository.saveAll(requestPosts);

        //when
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
//        List<PostResponse> posts = postService.getList(pageable);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(5)
                .build();

        List<PostResponse> posts = postService.getList(postSearch);

        //then
        assertThat(posts.size()).isEqualTo(5);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목 - 30");
        assertThat(posts.get(4).getTitle()).isEqualTo("제목 - 26");

    }

    @Test
    @DisplayName("글 제목 수정")
    void test5() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("foo2")
                .content("bar2")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));

        assertThat(changedPost.getTitle()).isEqualTo("foo2");
        assertThat(changedPost.getContent()).isEqualTo("bar2");
    }

    /**
     * 클라이언트 요구사항
     * 내용만 수정하려고 하는데 제목은 null로 보낼테니 그대로 유지시켜줘.
     * 혹은 제목만 수정하려고 하는데 내용은 null로 보낼테니 그대로 유지시켜줘.
     */
    @Test
    @DisplayName("글 내용 수정 - 제목은 그대로 유지")
    void test6() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title(null)
                .content("bar2")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));

        assertThat(changedPost.getTitle()).isEqualTo("foo1");
        assertThat(changedPost.getContent()).isEqualTo("bar2");
    }

    @Test
    @DisplayName("글 삭제")
    void test7() {
        // given
        Post post = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        //when
        postService.delete(post.getId());

        // then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("글 1개 조회 실패")
    void test8() {
        // given
        Post requestPost = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();

        postRepository.save(requestPost);

        // when

        // then
        //primary_id: 1부터 시작하는 것을 보장하지 않기 문에 obj.getId()로 가져와야 한다.
        //DB에 대한 롤백은 이루어지지는 auto_increment로 생성한 값까지 초기화되지는 않기 때문이다.
//        assertThatThrownBy(() -> postService.get(requestPost.getId() + 1L))
//                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> postService.get(requestPost.getId() + 1L))
                .isInstanceOf(PostNotFound.class);
    }
}