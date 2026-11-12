// 중학생 눈높이: 오류 안내
// 문제가 생겼을 때 다른 코드와 화면이 알아볼 수 있는 오류 안내를 만듭니다.
// 예외는 정상 작업을 계속할 수 없다는 신호입니다. 오류 코드는 어떤 문제인지 구별하는 이름표입니다.
// 문제 종류 고르기 → 예외로 알리기 → 응답에 담을 항목 보기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: exception/ErrorCode.java, BusinessException.java, ErrorResponse.java
// 목표: 공통 에러 모델을 거의 백지에서. HttpStatus 매핑은 루트 PRD/TRD 3.9와 일치시킵니다.
// 막히면 starter/15-error-model, answers.md 15장 참고.

// 초보자 안내 (15장): 예외와 오류 응답 모델
// 개념: 예외는 정상 처리를 계속할 수 없다는 신호이고 ErrorCode는 실패 이유의 공통 이름입니다.
// 사용 이유: 각 기능이 서로 다른 형식의 오류를 보내면 화면과 테스트에서 처리하기 어렵습니다.
// 동작 흐름: 업무 코드가 BusinessException에 오류 코드를 담아 던집니다. ErrorResponse는 클라이언트에 보낼 코드·메시지 등을 담으며 HTTP 응답 변환은 다음 장의 처리기가 맡습니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-15
//
// Q1. ErrorResponse에서 HTTP status와 업무 code를 분리하는 이유는? [chapter-v1-15-04]
// ① 모든 업무 오류를 HTTP 200으로 숨기기 위해서다.
// ② 전송 수준 결과와 구체적인 업무 실패 원인을 각각 안정적으로 표현하기 위해서다.
// ③ 업무 code가 있으면 HTTP 상태는 전혀 필요 없기 때문이다.
// ④ HTTP status에 사용자의 비밀번호를 넣기 위해서다.
// 정답: ②
// 해설: 같은 HTTP 상태 안에 여러 업무 원인이 있을 수 있다. 상태·코드·메시지의 계약을 분리한다.
//
// Q2. 클라이언트 ErrorResponse에 stack trace를 넣지 않는 이유는? [chapter-v1-15-05]
// ① stack trace를 숨기면 서버 로그도 반드시 삭제해야 하기 때문이다.
// ② stack trace가 없으면 모든 오류가 자동 복구되기 때문이다.
// ③ 내부 구조·경로 등 정보 노출을 줄이고 상세 원인은 서버 로그와 추적 ID로 관리하기 위해서다.
// ④ 클라이언트가 DB 비밀번호를 알아야 복구할 수 있기 때문이다.
// 정답: ③
// 해설: 공개 오류 계약과 내부 진단 정보를 분리한다. 로그에도 민감 정보를 과도하게 기록하지 않는다.
//
// Q3. BusinessException을 Checked Exception으로 바꿀 때 고려할 점은? [chapter-v1-15-06]
// ① Checked Exception도 모든 기본 설정에서 반드시 자동 롤백된다.
// ② Checked Exception은 HTTP 응답에서 사용할 수 없다.
// ③ Checked Exception은 컴파일러가 언제나 무시한다.
// ④ 호출자의 throws·catch 부담과 기본 트랜잭션 롤백 규칙에 맞춘 rollbackFor 설정 등을 검토한다.
// 정답: ④
// 해설: 체크 예외는 처리·선언 요구가 있다. 기본 롤백과 명시적 롤백 설정을 구분하여 실패 저장 테스트를 수행한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점 (HttpStatus): NOT_FOUND ×6 / BAD_REQUEST: INVALID_INPUT·DUPLICATE_*·DEPARTMENT_HAS_EMPLOYEES·INVALID_DATE_RANGE·INVALID_STATUS
// □ ACCESS_DENIED=FORBIDDEN  □ AUTHENTICATION_REQUIRED=UNAUTHORIZED  □ INTERNAL_ERROR=INTERNAL_SERVER_ERROR
// □ extends RuntimeException  □ timestamp=LocalDateTime  □ code.name()
