// 중학생 눈높이: 오류 안내
// 문제가 생겼을 때 다른 코드와 화면이 알아볼 수 있는 오류 안내를 만듭니다.
// 예외는 정상 작업을 계속할 수 없다는 신호입니다. 오류 코드는 어떤 문제인지 구별하는 이름표입니다.
// 문제 종류 고르기 → 예외로 알리기 → 응답에 담을 항목 보기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/exception/ErrorCode.java
//   - src/main/java/com/example/companywork/exception/BusinessException.java
//   - src/main/java/com/example/companywork/exception/ErrorResponse.java
// 목표: 공통 에러 모델을 채우세요. TRD 3.9 참고.

// 초보자 안내 (15장): 예외와 오류 응답 모델
// 개념: 예외는 정상 처리를 계속할 수 없다는 신호이고 ErrorCode는 실패 이유의 공통 이름입니다.
// 사용 이유: 각 기능이 서로 다른 형식의 오류를 보내면 화면과 테스트에서 처리하기 어렵습니다.
// 동작 흐름: 업무 코드가 BusinessException에 오류 코드를 담아 던집니다. ErrorResponse는 클라이언트에 보낼 코드·메시지 등을 담으며 HTTP 응답 변환은 다음 장의 처리기가 맡습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
// record는 응답 데이터를 묶는 Java 타입입니다. 각 구성 요소에 이름이 같은 접근자가 생깁니다.
// 예를 들어 status는 status()로 읽으며 HTTP 상태와 상세 업무 오류 코드는 서로 다른 정보를 전합니다.
public record ErrorResponse(
    int status,
    String code,
    String message,
    LocalDateTime timestamp,
    List<FieldErrorItem> errors // Validation 실패 시에만 채워집니다.
) {
    public static ErrorResponse of(ErrorCode code, String message) {
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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
//
// 심화 노트 (면접 답변 포인트):
// - status(HTTP) = 프록시/모니터링/브라우저가 보는 전송계층 신호. code(비즈니스) = 같은 404 안에서도
//   EMPLOYEE_NOT_FOUND vs DEPARTMENT_NOT_FOUND 처럼 원인을 구분하는 신호. 클라이언트는 code 로 분기.
// - BusinessException extends RuntimeException → @Transactional 기본 롤백 대상.
//   Checked 로 만들면 호출부마다 try-catch 강제 + 기본 커밋이라 롤백을 놓치기 쉽다.
// - ErrorResponse 는 (status, code, message, timestamp) 4필드 + errors(검증 실패 시만). 스펙(TRD 3.9) 고정.
// - code 에 enum.name() 을 그대로 노출하면 프론트가 안정적으로 분기할 수 있다(메시지는 사람용, code 는 기계용).
