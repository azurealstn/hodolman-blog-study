package study.hodolmanblogstudy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.domain.PostEditor;
import study.hodolmanblogstudy.exception.InvalidRequest;
import study.hodolmanblogstudy.request.PostCreate;
import study.hodolmanblogstudy.request.PostEdit;
import study.hodolmanblogstudy.request.PostSearch;
import study.hodolmanblogstudy.resposne.PostResponse;
import study.hodolmanblogstudy.service.PostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    // HTTP METHOD
    // GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT
    // 글 등록
    // POST Method

    // 데이터를 검증하는 이유
    // 1. client 개발자가 깜박할 수 있다. 실수로 값을 안보낼 수 있다.
    // 2. client bug로 값이 누락될 수 있다.
    // 3. 외부에 나쁜 사람이 값을 임의로 조작해서 보낼 수 있다.
    // 4. DB에 값을 저장할 때 의도치 않은 오류가 발생할 수 있다.
    // 5. 서버 개발자의 편안함을 위해서 (검증이 되었기 때문에)

    //    @PostMapping("/posts")
    public String postV1(@RequestBody PostCreate params) throws Exception {

        // 이러한 검증의 문제점
        // 1. 빡세다. (노가다)
        // 2. 개발팀 -> 무언가 3번이상 반복작업을 할 때 내가 뭔가 잘못하고 있는건 아닐지 의심한다.
        // 3. 누락 가능성
        // 4. 생각보다 검증해야할 게 많다. (꼼꼼하지 않을 수 있다.)
        // 5. 뭔가 개발자스럽지 않다. -> 간지 x

        String title = params.getTitle();
        if (title == null || title.equals("")) {
            // 4. 생각보다 검증해야할 게 많다. (꼼꼼하지 않을 수 있다.)
            // {"title": ""}
            // {"title": "                     "}
            // {"title": "...........수십억글자  "}
            throw new Exception("타이틀 값이 없어요!");
        }

        String content = params.getContent();
        if (content == null || content.equals("")) {
            // error
        }

        log.info("title = {}, content = {}", params.getTitle(), params.getContent());
        return "Hello World";
    }

    /**
     * postV1보다는 에러처리가 간단하지만 매번 메서드마다 이렇게 작성하는 것은 불편하다.
     * (무언가 3번이상 반복작업을 할 때 내가 뭔가 잘못하고 있는건 아닐지 의심한다.)
     * 1. 매번 메서드마다 값을 검증 해야한다.
     * -> 개발자가 까먹을 수 있다.
     * -> 검증 부분에서 버그가 발생할 여지가 높다.
     * -> 지겹다. (간지가 안난다.)
     * 2. 응답값에 HashMap -> 응답 클래스를 만들어주는 것이 좋다.
     * 3. 여러 개의 에러처리 힘들다. (현재는 title에 대한 에러만 처리했다.)
     * 4. 세 번 이상의 반복적인 작업은 피해야 한다.
     * -> 코드뿐만 아니라 개발 프로세스에 관한 모든 것이 포함된다. (자동화 고려)
     * <p>
     * 위의 문제를 @RestControllerAdvice 를 통해 해결할 수 있다.
     */
//    @PostMapping("/posts")
    public Map<String, String> postV2(@RequestBody @Valid PostCreate params, BindingResult result) throws Exception {
        log.info("title = {}, content = {}", params.getTitle(), params.getContent());
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            FieldError fieldError = fieldErrors.get(0);
            String fieldName = fieldError.getField(); // title
            String errorMessage = fieldError.getDefaultMessage();// ..에러 메시지

            Map<String, String> error = new HashMap<>();
            error.put(fieldName, errorMessage);
            return error;
        }
        return Map.of();
    }

    //    @PostMapping("/posts")
    public Map<String, String> postV3(@RequestBody @Valid PostCreate params) throws Exception {
        log.info("title = {}, content = {}", params.getTitle(), params.getContent());

        return Map.of();
    }

    @PostMapping("/posts")
    public Long postV4(@RequestBody @Valid PostCreate request) throws Exception {
        // Case 1. 저장된 데이터 Entity -> response로 응답하기
        // Case 2. 저장한 데이터의 primary_id -> response로 응답하기
        //      Client에서는 수신한 id를 글 조회 API를 통해서 데이터를 수신받음
        // Case 3. 응답 필요없음 -> Client에서 모든 POST(글) 데이터 context를 잘 관리함
        // Bad Case: 서버에서 -> 반드시 이렇게 할겁니다! (fix)
        //      -> 서버에서 차라리 유연하게 대응하는게 좋다. -> 코드를 잘 작성해야 한다.
        //      -> 한 번에 일괄적으로 잘 처리되는 케이스가 없다. -> 잘 관리되는 형태가 굉장히 중요하다.

        // 아래처럼 데이터를 꺼내와서 조합하여 검증하는 것을 되도록 하지 않는 것이 좋다.
//        if (request.getTitle().contains("바보")) {
//            throw new InvalidRequest();
//        }

        // request DTO에서 바로 검증을 처리하는 것이 좋다.
        request.validate();

        return postService.write(request);
    }

    /**
     * /posts/{postId} -> 글 한개만 조회
     */
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long id) {
        // Request 클래스
        // Response 클래스

        PostResponse post = postService.get(id);
        return post;
    }

    /**
     * /posts -> 글 전체 조회 (검색 + 페이징)
     * 글이 너무 많은 경우 -> 비용이 많이 든다.
     * 글이 -> 10,000,000 -> DB 글 모두 조회하는 경우 -> DB가 뻗을 수 있다.
     * DB -> 애플리케이션 서버로 전달하는 시간(비용), 트래픽 비용 등이 많이 발생할 수 있다.
     * 페이징 처리를 하면 위 문제를 해결할 수 있다.
     *
     * 아래에 Pageable pageable 파라미터를 받는 식으로는 잘 사욯하지 않는다.
     */
//    @GetMapping("/posts")
//    public List<PostResponse> getListV1(Pageable pageable) {
//        return postService.getList(pageable);
//    }

    @GetMapping("/posts")
    public List<PostResponse> getListV2(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    /**
     * post 수정
     */
    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable(name = "postId") Long id, @RequestBody PostEdit postEditor) {
        postService.edit(id, postEditor);
    }

    /**
     * post 삭제
     */
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable(name = "postId") Long id) {
        postService.delete(id);
    }
}
