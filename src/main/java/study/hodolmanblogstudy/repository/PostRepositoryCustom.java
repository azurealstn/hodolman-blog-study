package study.hodolmanblogstudy.repository;

import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
