package study.hodolmanblogstudy.exception;

/**
 * status: 404 (리소스 못찾음)
 */
public class PostNotFound extends GlobalException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}
