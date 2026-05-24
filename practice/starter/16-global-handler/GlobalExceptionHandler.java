// 실제 구현 위치 예: src/main/java/com/example/companywork/exception/GlobalExceptionHandler.java
// 목표: 모든 Controller 의 예외를 일관된 응답으로 변환하세요. TRD 3.9 참고.

// TODO 01: 모든 @RestController 에 적용되는 전역 예외 핸들러임을 알리는 어노테이션.
@____
@Slf4j
public class GlobalExceptionHandler {

    // TODO 02: 비즈니스 예외를 ErrorResponse 로 변환.
    @ExceptionHandler(____.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        log.warn("BusinessException: {} - {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    // TODO 03: @Valid 검증 실패. body 검증 실패는 어떤 예외 타입일까요?
    @ExceptionHandler(____.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldErrorItem> errors = e.getBindingResult().getFieldErrors().stream()
            // TODO 04: FieldError -> FieldErrorItem 으로 변환.
            .map(fe -> new ErrorResponse.FieldErrorItem(fe.____(), fe.getDefaultMessage()))
            .toList();

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.validation(errors));
    }

    // TODO 05: ConstraintViolationException(쿼리 파라미터/PathVariable 검증) 도 처리하면 좋습니다.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    // TODO 06: 마지막 안전망. stack trace 는 응답에 노출하지 않고 로그로만 남깁니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
            .status(HttpStatus.____)
            .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "예기치 못한 오류가 발생했습니다."));
    }
}

// 학습 질문:
// Q1. @RestControllerAdvice 와 @ControllerAdvice 의 차이는?
//     A:
// Q2. 마지막 Exception 핸들러를 두지 않으면 어떤 일이 생기나?
//     A:
// Q3. Spring Security 의 인증/인가 예외(AccessDeniedException, AuthenticationException) 는
//     ExceptionTranslationFilter 가 먼저 잡기 때문에 여기서 못 잡을 수 있다. 어디서 변환해야 할까?
//     A:
