// 실제 구현 위치 예: exception/GlobalExceptionHandler.java
// 목표: 전역 예외 핸들러를 거의 백지에서. TRD 3.9.
// 막히면 starter/16-global-handler, answers.md 16장 참고.

// TODO 01: 모든 @RestController 예외를 가로채 JSON 으로 변환하는 어노테이션.
@____
@Slf4j
public class GlobalExceptionHandler {

    // TODO 02: 비즈니스 예외 → ErrorResponse.
    @ExceptionHandler(____.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        log.warn("BusinessException: {} - {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    // TODO 03: @Valid body 검증 실패 예외 타입은?
    @ExceptionHandler(____.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldErrorItem> errors = e.getBindingResult().getFieldErrors().stream()
            // TODO 04: FieldError → FieldErrorItem (field 추출 메서드는?)
            .map(fe -> new ErrorResponse.FieldErrorItem(fe.____(), fe.getDefaultMessage()))
            .toList();
        return ResponseEntity.badRequest().body(ErrorResponse.validation(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    // TODO 05: 최종 안전망. stack trace 는 로그로만.
    @ExceptionHandler(____.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
            .status(HttpStatus.____)
            .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "예기치 못한 오류가 발생했습니다."));
    }
}

// 학습 질문 (직접 답):
// Q1. @RestControllerAdvice 와 @ControllerAdvice 의 차이는?
//     A:
// Q2. 마지막 Exception 핸들러를 두지 않으면 어떤 일이?
//     A:
// Q3. Security 인증/인가 예외는 왜 여기서 못 잡을 수 있고, 어디서 변환해야 하나?
//     A:

// 자가 채점:
// □ @RestControllerAdvice  □ @ExceptionHandler(BusinessException)  □ MethodArgumentNotValidException
// □ fe.getField()  □ @ExceptionHandler(Exception)  □ INTERNAL_SERVER_ERROR
// □ 구체 타입 먼저 → Exception 마지막  □ Security 예외는 EntryPoint/AccessDeniedHandler 에서 변환
