package study.hodolmanblogstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.hodolmanblogstudy.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
