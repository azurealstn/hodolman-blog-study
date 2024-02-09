package study.hodolmanblogstudy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.repository.PostRepository;
import study.hodolmanblogstudy.request.PostCreate;
import study.hodolmanblogstudy.request.PostEdit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void before() {
        postRepository.deleteAll();
    }

//    @Test
    @DisplayName("/posts 요청시 Hello World 출력")
    void test() throws Exception {
        // 글 제목
        // 글 내용
        // 사용자 (id, name, role)
        // x-www-form-urlencoded: title=xxx&content=xxx&userId=xxx&userName=xxx&userRole=xxx
        // x-www-form-urlencoded 방식은 보기가 불편하다.
        // 하지만 applicatino/json 방식은 한 눈에 들어온다.

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"title\": \"글 제목입니다.\",\n" +
                                "  \"content\": \"글 내요입니다.\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print()); //HTTP 요청에 대한 Handler 요약
    }

    /**
     * 데이터 검증시에는 의심되는 부분들을 모두 테스트 해봐야한다.
     * {title: ""}
     * {title: "     "}
     * {title: " ... 수십억 글자"}
     * {title: null}
     * <p>
     * jsonPath 구글 검색하기
     * <p>
     * "잘못된 요청입니다.", "제목을 입력해주세요."와 같이 하드코딩된 부분들은 고민을 해봐야한다.
     * 왜냐하면 메시지를 변경하게 되면 테스트 코드 등 모든 곳에서 변경을 시도해야 한다.
     * 이게 변경 부분이 많다면 상당히 까다로울 수 있다.
     * 그래서 스프링 메시지를 사용하면 이를 해결할 수 있다. (스프링 메시지 사용 고려)
     */
    @Test
    @DisplayName("/posts 요청시 title 값은 필수다")
    void test2() throws Exception {
        // expected
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"title\": \"\",\n" +
                                "  \"content\": \"글 내용입니다.\"\n" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print()); //HTTP 요청에 대한 Handler 요약
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {
        // when
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"title\": \"글 제목입니다.\",\n" +
                                "  \"content\": \"글 내용입니다.\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andDo(print()); //HTTP 요청에 대한 Handler 요약

        // then
        assertThat(postRepository.count()).isEqualTo(1);

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("글 제목입니다.");
        assertThat(post.getContent()).isEqualTo("글 내용입니다.");

    }

    /**
     * ObjectMapper는 실무에서 많이 사용함으로 꼭 알아두는 것이 좋다.
     * ObjectMapper는 @Autoired 주입을 받아도 된다.
     * PostCreate request = new PostCreate("글 제목입니다.", "글 내용입니다.");
     * 와 같이 생성자를 직접 생성하면 문제가 생긴다.
     * 생성자에서 메서드 파라미터 순서를 모르고 변경시키면 전혀 다른 결과가 나오는 것을 확인할 수 있다.
     * 그래서 Builder를 사용하는 것이 맞다.
     */
    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다. 클래스 분리")
    void test4() throws Exception {
        //given
//        PostCreate request = new PostCreate("글 제목입니다.", "글 내용입니다.");
        PostCreate request = PostCreate.builder()
                .title("글 제목입니다.")
                .content("글 내용입니다.")
                .build();

        //ObjectMapper는 실무에서 많이 사용함으로 꼭 알아두는 것이 좋다.
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print()); //HTTP 요청에 대한 Handler 요약

        // then
        assertThat(postRepository.count()).isEqualTo(1);

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("글 제목입니다.");
        assertThat(post.getContent()).isEqualTo("글 내용입니다.");

    }

    @Test
    @DisplayName("글 1개 조회")
    void test5() throws Exception {
        // given
        Post post = Post.builder()
                .title("글 제목입니다.")
                .content("글 내용입니다.")
                .build();
        postRepository.save(post);

        // expected (when + then)
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("글 제목입니다."))
                .andExpect(jsonPath("$.content").value("글 내용입니다."))
                .andDo(print());

    }

//    @Test
    @DisplayName("글 여러 조회")
    void test6() throws Exception {
        // given
        Post post1 = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();

        Post post2 = Post.builder()
                .title("foo2")
                .content("bar2")
                .build();

        postRepository.save(post1);
        postRepository.save(post2);

        // expected (when + then)
        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                //직접 써주는 것이 낫다. get Method에서는 어떤 행위를 할지 모른다.
                .andExpect(jsonPath("$[0].title").value("foo1"))
                .andExpect(jsonPath("$[0].content").value("bar1"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test7() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build())
                .toList();

        postRepository.saveAll(requestPosts);

        // expected (when + then)
        mockMvc.perform(get("/posts?page=1&sort=id,desc&size=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                //primary_id: 1부터 시작하는 것을 보장하지 않기 문에 obj.getId()로 가져와야 한다.
                //DB에 대한 롤백은 이루어지지는 auto_increment로 생성한 값까지 초기화되지는 않기 때문이다.
                .andExpect(jsonPath("$[0].id").value(requestPosts.get(29).getId()))
                .andExpect(jsonPath("$[0].title").value("제목 - 30"))

                .andExpect(jsonPath("$[0].content").value("내용 - 30"))
                .andDo(print());

//        mockMvc.perform(get("/posts?page=1&sort=id,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());

    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("글 제목 수정")
    void test8() throws Exception {
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

        // expected (when + then)
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("글 삭제")
    void test9() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        // expected (when + then)
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 글 조회")
    void test10() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        postRepository.save(post);

        // expected (when + then)
        mockMvc.perform(get("/posts/{postId}", post.getId() + 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("글 작성시 제목에 '바보'는 포함될 수 없다.")
    void test11() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("글 제목입니다.바보")
                .content("글 내용입니다.")
                .build();

        //ObjectMapper는 실무에서 많이 사용함으로 꼭 알아두는 것이 좋다.
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print()); //HTTP 요청에 대한 Handler 요약

    }
}