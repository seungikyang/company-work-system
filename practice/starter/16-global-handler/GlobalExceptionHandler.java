// 실제 구현 위치 예: src/main/java/com/example/companywork/exception/GlobalExceptionHandler.java
// 목표: 모든 Controller 의 예외를 일관된 응답으로 변환하세요. TRD 3.9 참고.

// 초보자 안내 (16장): 전역 예외 처리
// 개념: 전역 예외 처리기는 여러 Controller에서 발생한 예외를 모아 응답으로 바꿉니다.
// 사용 이유: Controller마다 같은 try/catch와 응답 생성 코드를 반복하지 않게 합니다.
// 동작 흐름: @RestControllerAdvice 안의 @ExceptionHandler가 대응하는 예외를 받아 HTTP 상태와 ErrorResponse를 만듭니다. 입력 오류와 업무 예외의 처리 경로를 나누어 읽어 보세요.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
//
// 심화 노트 (면접 답변 포인트):
// - @RestControllerAdvice = @ControllerAdvice + @ResponseBody → 예외 변환 결과를 JSON 으로 응답.
// - 핸들러 매칭은 "더 구체적인 예외 타입" 우선. BusinessException → MethodArgumentNotValid →
//   ConstraintViolation → Exception(최종 안전망) 순으로 좁은 것부터 둔다.
// - Security 예외는 필터 단계(ExceptionTranslationFilter)에서 발생해 Advice 보다 앞선다 →
//   AuthenticationEntryPoint(401) / AccessDeniedHandler(403) 에서 같은 ErrorResponse 포맷으로 변환해야 일관성 유지.
// - 최종 Exception 핸들러: stack trace 는 log.error 로만 남기고 응답엔 INTERNAL_ERROR(500) 만. 내부정보 노출 금지.
