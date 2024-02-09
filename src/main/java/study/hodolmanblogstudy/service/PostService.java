package study.hodolmanblogstudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.domain.PostEditor;
import study.hodolmanblogstudy.exception.PostNotFound;
import study.hodolmanblogstudy.repository.PostRepository;
import study.hodolmanblogstudy.request.PostCreate;
import study.hodolmanblogstudy.request.PostEdit;
import study.hodolmanblogstudy.request.PostSearch;
import study.hodolmanblogstudy.resposne.PostResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    public Long write(PostCreate postCreate) {
        // postCreate -> Entity

//        Post post = new Post(postCreate.getTitle(), postCreate.getContent());
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        return postRepository.save(post).getId();
    }

    public PostResponse get(Long id) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        // response로 변환하는 작업을 service에서 하는 것이 맞을까?
        // PostController -> WebPostService -> Repository
        //                -> PostService
        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();


        return response;
    }

    /**
     * 컬럼이 많아지면 보기 힘들어진다.
     */
//    public List<PostResponse> getList() {
//        return postRepository.findAll().stream()
//                .map(post -> PostResponse.builder()
//                        .id(post.getId())
//                        .title(post.getTitle())
//                        .content(post.getContent())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    public List<PostResponse> getList(Pageable pageable) {
//        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")));
//        return postRepository.findAll(pageable).stream()
//                .map(PostResponse::new)
//                .collect(Collectors.toList());
//    }
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

//        post.changePost(postEdit.getTitle(), postEdit.getContent());

//        PostEditor.PostEditorBuilder editor = post.toEditor();
//        PostEditor postEditor = editor
//                .title(postEdit.getTitle())
//                .content(postEdit.getContent())
//                .build();

        /**
         * 클라이언트 요구사항
         * 내용만 수정하려고 하는데 제목은 null로 보낼테니 그대로 유지시켜줘.
         * 혹은 제목만 수정하려고 하는데 내용은 null로 보낼테니 그대로 유지시켜줘.
         */
        post.changePost(
                postEdit.getTitle() != null ? postEdit.getTitle() : post.getTitle(),
                postEdit.getContent() != null ? postEdit.getContent() : post.getContent());
    }

    public void delete(Long id) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        postRepository.deleteById(post.getId());
    }
}
