// 중학생 눈높이: 공통 오류 응답
// 여러 곳에서 생긴 오류를 한곳에서 같은 모양의 안내로 바꿉니다.
// HTTP 상태 코드는 요청 결과의 번호입니다. 잘못된 입력과 서버 내부 오류를 구별할 수 있습니다.
// 오류 받기 → 오류 종류 확인 → 알맞은 응답 만들기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
    // BusinessException이 MVC 예외 처리로 전달되면 이 메서드가 상태 코드와 JSON 본문을 만듭니다.
    // 핸들러를 파일에 나열한 순서대로 실행하는 것이 아니라 예외 타입 등 매칭 규칙으로 선택합니다.
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        log.warn("BusinessException: {} - {}", e.getErrorCode(), e.getMessage());
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-16
//
// Q1. @RestControllerAdvice와 @ControllerAdvice의 차이로 알맞은 것은? [chapter-v1-16-04]
// ① ControllerAdvice는 모든 필터 예외를 무조건 처리한다.
// ② RestControllerAdvice는 HTML view 이름만 반환할 수 있다.
// ③ RestControllerAdvice는 ControllerAdvice와 ResponseBody 의미를 함께 제공해 반환값을 응답 본문으로 처리한다.
// ④ 두 어노테이션은 예외 처리와 관련이 없다.
// 정답: ③
// 해설: ControllerAdvice에서도 ResponseBody를 지정할 수 있다. REST 조언은 본문 응답을 기본으로 쓰는 합성 어노테이션이다.
//
// Q2. 최종 Exception 핸들러를 두는 목적과 한계는? [chapter-v1-16-05]
// ① 최종 핸들러가 있으면 예외 발생 자체가 없어지는 것이다.
// ② 예상하지 못한 MVC 오류의 응답 형식과 서버 로그를 일관되게 관리하되 모든 계층의 예외를 잡는다고 가정하지 않는다.
// ③ 핸들러가 없으면 어떤 서버에서도 응답이 절대로 반환되지 않는다.
// ④ 최종 핸들러가 모든 오류를 성공 200으로 변환해야 한다.
// 정답: ②
// 해설: 기본 오류 처리도 있을 수 있으나 프로젝트 계약과 다를 수 있다. 구체적 오류와 최종 오류의 응답·로그 정책을 정한다.
//
// Q3. Security 필터 단계의 인증·인가 실패 응답은 어디서 조정하는가? [chapter-v1-16-06]
// ① AuthenticationEntryPoint와 AccessDeniedHandler 등 Security의 실패 처리 경로에서 조정한다.
// ② 모든 실패가 반드시 MVC ControllerAdvice까지 도달한다고 가정한다.
// ③ JPA Auditing listener에서 HTTP 인증 오류를 변환한다.
// ④ Entity의 toString에서 응답 코드를 정한다.
// 정답: ①
// 해설: DispatcherServlet 밖의 필터에서 발생한 오류는 MVC 조언으로 처리되지 않을 수 있다. Security 예외 변환 경로를 구성한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - @RestControllerAdvice = @ControllerAdvice + @ResponseBody → 예외 변환 결과를 JSON 으로 응답.
// - 핸들러는 발생한 예외에 맞춰 선택되며 파일의 나열 순서가 실행 순서는 아니다.
//   서로 다른 예외 타입용 핸들러와 Exception 최종 안전망을 구분해서 읽는다.
// - Security 예외는 필터 단계(ExceptionTranslationFilter)에서 발생해 Advice 보다 앞선다 →
//   AuthenticationEntryPoint(401) / AccessDeniedHandler(403) 에서 같은 ErrorResponse 포맷으로 변환해야 일관성 유지.
// - 최종 Exception 핸들러: stack trace 는 log.error 로만 남기고 응답엔 INTERNAL_ERROR(500) 만. 내부정보 노출 금지.
