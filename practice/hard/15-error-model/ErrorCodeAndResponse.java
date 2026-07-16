// 실제 구현 위치 예: exception/ErrorCode.java, BusinessException.java, ErrorResponse.java
// 목표: 공통 에러 모델을 거의 백지에서. HttpStatus 매핑은 루트 PRD/TRD 3.9와 일치시킵니다.
// 막히면 starter/15-error-model, answers.md 15장 참고.

// ===== ErrorCode ===== (각 코드의 HttpStatus 를 직접 채우세요)
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.____, "잘못된 요청입니다."),
    ACCESS_DENIED(HttpStatus.____, "접근 권한이 없습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.____, "로그인이 필요합니다."),
    INTERNAL_ERROR(HttpStatus.____, "서버 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.____, "사용자를 찾을 수 없습니다."),
    EMPLOYEE_NOT_FOUND(HttpStatus.____, "직원을 찾을 수 없습니다."),
    DEPARTMENT_NOT_FOUND(HttpStatus.____, "부서를 찾을 수 없습니다."),
    LEAVE_NOT_FOUND(HttpStatus.____, "휴가 신청을 찾을 수 없습니다."),
    APPROVAL_NOT_FOUND(HttpStatus.____, "결재 문서를 찾을 수 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.____, "____"),

    DUPLICATE_EMAIL(HttpStatus.____, "____"),
    DUPLICATE_EMPLOYEE_NUMBER(HttpStatus.____, "이미 사용 중인 사번입니다."),
    DUPLICATE_DEPARTMENT_NAME(HttpStatus.____, "이미 존재하는 부서명입니다."),
    DEPARTMENT_HAS_EMPLOYEES(HttpStatus.____, "소속 직원이 있는 부서는 삭제할 수 없습니다."),

    INVALID_DATE_RANGE(HttpStatus.____, "잘못된 날짜 범위입니다."),
    INVALID_STATUS(HttpStatus.____, "____");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
    public HttpStatus getStatus() { return status; }
    public String getDefaultMessage() { return defaultMessage; }
}

// ===== BusinessException =====
// TODO 01: 무엇을 상속해야 트랜잭션 기본 rollback 대상이 되나?
public class BusinessException extends ____ {

    private final ErrorCode errorCode;
    private final String message;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage());
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
    public ErrorCode getErrorCode() { return errorCode; }
    @Override public String getMessage() { return message; }
}

// ===== ErrorResponse =====
// TODO 02: 공통 응답 4필드 + 검증 errors.
public record ErrorResponse(
    int status,
    String code,
    String message,
    ____ timestamp,
    List<FieldErrorItem> errors
) {
    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(
            code.getStatus().value(),
            // TODO 03: 클라이언트가 분기할 비즈니스 코드 — enum 이름 그대로.
            code.____(),
            message,
            LocalDateTime.now(),
            List.of()
        );
    }
    public static ErrorResponse validation(List<FieldErrorItem> errors) {
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ErrorCode.INVALID_INPUT.name(),
            ErrorCode.INVALID_INPUT.getDefaultMessage(),
            LocalDateTime.now(),
            errors
        );
    }
    public record FieldErrorItem(String field, String message) { }
}

// 학습 질문 (직접 답):
// Q1. status(HTTP) 와 code(비즈니스) 를 따로 두는 이유는?
//     A:
// Q2. ErrorResponse 에 stack trace 를 넣지 않는 이유는?
//     A:
// Q3. BusinessException 을 Checked 로 만들면 어떤 불편이 생기나?
//     A:

// 자가 채점 (HttpStatus): NOT_FOUND ×6 / BAD_REQUEST: INVALID_INPUT·DUPLICATE_*·DEPARTMENT_HAS_EMPLOYEES·INVALID_DATE_RANGE·INVALID_STATUS
// □ ACCESS_DENIED=FORBIDDEN  □ AUTHENTICATION_REQUIRED=UNAUTHORIZED  □ INTERNAL_ERROR=INTERNAL_SERVER_ERROR
// □ extends RuntimeException  □ timestamp=LocalDateTime  □ code.name()
