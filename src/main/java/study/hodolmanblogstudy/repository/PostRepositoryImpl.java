package study.hodolmanblogstudy.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import study.hodolmanblogstudy.domain.Post;
import study.hodolmanblogstudy.domain.QPost;
import study.hodolmanblogstudy.request.PostSearch;

import java.util.List;

import static study.hodolmanblogstudy.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return query
                .selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }

}
