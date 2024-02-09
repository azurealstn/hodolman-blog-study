package study.hodolmanblogstudy.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import study.hodolmanblogstudy.exception.GlobalException;
import study.hodolmanblogstudy.exception.InvalidRequest;
import study.hodolmanblogstudy.resposne.ErrorResponse;

@RestControllerAdvice
public class ExceptionController {

    /**
     * @Valid 애너테이션으로 데이터를 검증하고,
     * 해당 데이터에 에러가 있을 경우 스프링에서 정의한 에러, BindingResult를 상속한
     * MethodArgumentNotValidException 에러 글래스를 반환시킨다.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
//            FieldError fieldError = e.getFieldError();
//            String field = fieldError.getField();
//            String message = fieldError.getDefaultMessage();
//        ErrorResponse response = new ErrorResponse("400", "잘못된 요청입니다.");
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return response;
    }

    /**
     * 리소스를 찾지 못했으므로 404 에러 반환
     *
     * @ResponseStatus(HttpStatus.NOT_FOUND)
     * -> 동적으로 status 코드를 반환하지 못함
     */
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> globalException(GlobalException e) {
        int statusCode = e.getStatus();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

//        ResponseEntity<ErrorResponse> response = (ResponseEntity<ErrorResponse>) ResponseEntity
//                .status(statusCode)
//                .body(body);

        /**
         * {"code":"400","message":"잘못된 요청입니다.","validation":{}}
         * 어떤 필드에 어떤 값이 잘못되었는지 validation 필드에 추가해준다.
         *
         * 하지만 이러면 나중에 if문이 굉장히 많아질 수도 있다.
         * 따라서 위에 ErrorResponse.builder()에 직접 validation을 추가하면 깔끔하게 된다.
         */
//        if (e instanceof InvalidRequest) {
//            InvalidRequest invalidRequest = (InvalidRequest) e;
//            String fieldName = invalidRequest.getFieldName();
//            String message = invalidRequest.getMessage();
//            body.addValidation(fieldName, message);
//        }

        return new ResponseEntity<>(body, HttpStatusCode.valueOf(statusCode));
    }
}
