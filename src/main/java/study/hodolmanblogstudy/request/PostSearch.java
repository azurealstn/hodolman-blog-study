package study.hodolmanblogstudy.request;

import lombok.Builder;
import lombok.Data;

@Data
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    private Integer page = 1;
    private Integer size = 5;

    @Builder
    public PostSearch(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        return (long) (Math.max(1, page) - 1) * Math.min(size, MAX_SIZE);
    }
}
