// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/exception/ErrorCode.java
//   - src/main/java/com/example/companywork/exception/BusinessException.java
//   - src/main/java/com/example/companywork/exception/ErrorResponse.java
// 목표: 공통 에러 모델을 채우세요. TRD 3.9 참고.

// ===== ErrorCode =====
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // TODO 01: 도메인별 NOT_FOUND 6종을 채우세요. (User / Employee / Department / Leave / Approval / Notice)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMPLOYEE_NOT_FOUND(____, "직원을 찾을 수 없습니다."),
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부서를 찾을 수 없습니다."),
    LEAVE_NOT_FOUND(HttpStatus.NOT_FOUND, "휴가 신청을 찾을 수 없습니다."),
    APPROVAL_NOT_FOUND(HttpStatus.NOT_FOUND, "결재 문서를 찾을 수 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "____"),

    // TODO 02: 중복/무결성 에러.
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "____"),
    DUPLICATE_EMPLOYEE_NUMBER(HttpStatus.BAD_REQUEST, "이미 사용 중인 사번입니다."),
    DUPLICATE_DEPARTMENT_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 부서명입니다."),
    DEPARTMENT_HAS_EMPLOYEES(HttpStatus.BAD_REQUEST, "소속 직원이 있는 부서는 삭제할 수 없습니다."),

    // TODO 03: 휴가 신청 시 시작일 > 종료일 같은 잘못된 날짜.
    INVALID_DATE_RANGE(HttpStatus.____, "잘못된 날짜 범위입니다."),

    // TODO 04: 처리할 수 없는 상태(예: 이미 승인된 휴가를 다시 승인 시도).
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "____");

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
// TODO 05: RuntimeException 을 상속한 이유는? (트랜잭션 rollback 기본 정책과 관련)
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
public record ErrorResponse(
    int status,
    String code,
    String message,
    LocalDateTime timestamp,
    List<FieldErrorItem> errors // Validation 실패 시에만 채워집니다.
) {
    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(
            code.getStatus().value(),
            // TODO 06: 클라이언트가 분기할 수 있는 비즈니스 코드. enum 이름을 그대로 노출하면 좋습니다.
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

// 학습 질문:
// Q1. status(HTTP) 와 code(비즈니스) 를 따로 두는 이유는?
//     A:
// Q2. ErrorResponse 에 stack trace 를 넣지 않는 이유는?
//     A:
// Q3. BusinessException 을 Checked Exception 으로 만들면 어떤 불편이 생길까?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - status(HTTP) = 프록시/모니터링/브라우저가 보는 전송계층 신호. code(비즈니스) = 같은 404 안에서도
//   EMPLOYEE_NOT_FOUND vs DEPARTMENT_NOT_FOUND 처럼 원인을 구분하는 신호. 클라이언트는 code 로 분기.
// - BusinessException extends RuntimeException → @Transactional 기본 롤백 대상.
//   Checked 로 만들면 호출부마다 try-catch 강제 + 기본 커밋이라 롤백을 놓치기 쉽다.
// - ErrorResponse 는 (status, code, message, timestamp) 4필드 + errors(검증 실패 시만). 스펙(TRD 3.9) 고정.
// - code 에 enum.name() 을 그대로 노출하면 프론트가 안정적으로 분기할 수 있다(메시지는 사람용, code 는 기계용).
