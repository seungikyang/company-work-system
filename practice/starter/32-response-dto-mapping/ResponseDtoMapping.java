// 중학생 눈높이: 화면에 보낼 정보
// DB의 자료에서 화면에 필요한 값만 골라 응답용 DTO에 옮깁니다.
// Entity는 저장용 카드, 응답 DTO는 공개할 정보만 담은 메모라고 생각하면 됩니다.
// 원본 자료 읽기 → 보낼 항목 고르기 → 응답 메모 만들기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/dto/*Response.java
// 목표: Entity ↔ Response DTO 매핑 패턴을 채우세요. TRD 3.16.3 (Entity 직접 반환 문제) 참고.

// 초보자 안내 (32장): 응답 DTO 매핑
// 개념: 매핑은 Entity에서 외부에 공개할 필드만 골라 응답 객체로 옮기는 작업입니다.
// 사용 이유: 비밀번호와 내부 관계를 숨기고 DB 구조 변경이 API에 바로 퍼지는 것을 줄입니다.
// 동작 흐름: from 메서드가 Entity 값을 record의 필드로 옮기고 Page.map이 목록 항목마다 이를 적용합니다. 지연 로딩 관계를 읽는 시점과 쿼리 수도 함께 확인합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// 핵심 원칙:
// - Entity 는 영속성/도메인 책임.
// - Response DTO 는 API 응답 책임. (필드 선택, 직렬화, 버전 관리)
// - "응답 DTO 가 Entity 를 import 해도 되지만, 그 반대는 절대 금지" 라는 단방향 의존을 지킨다.

// =====================================================================
// 1. EmployeeResponse
// =====================================================================
public record EmployeeResponse(
        Long employeeId,
        String name,
        String email,
        String employeeNumber,
        String position,
        String phone,
        LocalDate hireDate,
        String departmentName,
        EmployeeStatus status
) {
    // TODO 01: Entity → DTO 변환 정적 팩토리.
    // from은 직접 만든 변환 메서드 이름입니다. 필요한 필드만 복사하므로 비밀번호 같은 내부 값의 노출을 줄입니다.
    // 관련 User·Department를 읽는 시점에 추가 조회가 필요한지도 Service의 조회 범위와 함께 확인하세요.
    public static EmployeeResponse from(Employee e) {
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return new EmployeeResponse(
                e.getId(),
                e.getUser().____(),       // 이름
                e.getUser().getEmail(),
                e.getEmployeeNumber(),
                e.getPosition(),
                e.getPhone(),
                e.getHireDate(),
                e.getDepartment().getName(),
                e.____()                  // 상태
        );
    }
}

// =====================================================================
// 2. LeaveResponse
// =====================================================================
public record LeaveResponse(
        Long leaveId,
        Long employeeId,
        String employeeName,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        ApprovalStatus status,
        Long approverId,
        String rejectReason,
        LocalDateTime createdAt
) {
    public static LeaveResponse from(LeaveRequest l) {
        return new LeaveResponse(
                l.getId(),
                l.getEmployee().getId(),
                // TODO 02: 직원 이름은 employee → user → name 경로로 접근.
                l.getEmployee().getUser().____(),
                l.getLeaveType(),
                l.getStartDate(),
                l.getEndDate(),
                l.getReason(),
                l.getStatus(),
                l.getApproverId(),
                l.getRejectReason(),
                l.getCreatedAt()
        );
    }
}

// =====================================================================
// 3. 공통: Page 매핑 헬퍼
// =====================================================================
public final class PageMapper {

    private PageMapper() {}

    public static <E, R> Page<R> map(Page<E> page, Function<E, R> mapper) {
        // TODO 03: Spring Data 가 Page<E> 를 Page<R> 로 변환하는 API 는?
        return page.____(mapper);
    }
}

// =====================================================================
// 4. 안티 패턴 (하지 말 것)
// =====================================================================
// (A) Controller 에서 Entity 를 그대로 반환
//     → 비밀번호, 내부 필드, LAZY 프록시 직렬화 실패, JSON 무한 루프, API 가 깨지기 쉬움.
//
// (B) Entity 안에 @JsonIgnore 를 덕지덕지 붙여서 응답에 맞춰가기
//     → 도메인 책임이 흐려진다. 응답 모양이 바뀔 때마다 Entity 가 흔들린다.
//
// (C) 같은 DTO 를 요청 / 응답 양쪽에 사용
//     → 요청은 password 가 필요하지만 응답에는 password 가 절대 들어가면 안 된다.
//     → 입력 검증 어노테이션이 응답에도 묻어들어가는 어색함.

// =====================================================================
// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-32
//
// Q1. Entity 직접 응답의 보안·직렬화 위험을 올바르게 묶은 것은? [chapter-v1-32-06]
// ① Entity 응답은 어떤 필드도 JSON에 포함하지 않는다.
// ② Entity이면 비밀번호와 역할이 자동 제거된다.
// ③ 모든 LAZY 관계는 응답 직렬화에서 SQL 없이 로딩된다.
// ④ 민감 필드 노출·양방향 순환 참조·지연 로딩 또는 N+1 문제를 유발할 수 있다.
// 정답: ④
// 해설: 공개 필드를 가진 DTO와 명시적 조회·매핑 경계로 위험을 줄인다. 실제 응답과 쿼리도 검증한다.
//
// Q2. record와 일반 class DTO의 차이로 알맞은 것은? [chapter-v1-32-07]
// ① record는 구성 요소의 final 필드·접근자·동등성 등을 제공하지만 참조한 객체의 깊은 불변성까지 보장하지 않는다.
// ② record의 모든 참조 객체와 컬렉션은 자동으로 깊은 불변이 된다.
// ③ record는 Entity처럼 무조건 변경 가능한 setter를 만든다.
// ④ class DTO에는 생성자나 검증을 작성할 수 없다.
// 정답: ①
// 해설: DTO의 변경 요구와 데이터 구조에 맞게 선택한다. record의 불변 참조와 내부 객체의 가변성은 구분한다.
//
// Q3. 응답 DTO에 Entity 모든 필드를 1:1로 옮기지 않는 이유는? [chapter-v1-32-08]
// ① 필드를 빼면 DB 원본 값도 자동 삭제되기 때문이다.
// ② API 소비자에게 필요한 공개 필드와 계약만 제공해 민감 정보와 내부 구조 결합을 줄이기 위해서다.
// ③ DTO는 언제나 모든 관계를 포함해야 하기 때문이다.
// ④ 필드 수가 적으면 인증 검사를 하지 않아도 되기 때문이다.
// 정답: ②
// 해설: DTO는 저장 구조의 복사본보다 목적별 공개 계약이다. 입력·출력·조회 용도에 맞게 설계한다.
//
// Q4. MapStruct·ModelMapper 같은 매핑 도구의 도입을 검토할 시점은? [chapter-v1-32-09]
// ① DTO가 한 개라도 있으면 수동 매핑은 불가능하다.
// ② 도구가 모든 민감 필드 공개 정책을 자동 판단한다.
// ③ 반복 매핑량과 변경 비용이 커졌을 때 공개 필드·검증 규칙을 유지하며 생성·실행 특성을 평가한다.
// ④ 매핑 도구를 넣으면 조회와 N+1 검토는 필요 없다.
// 정답: ③
// 해설: 자동화해도 공개 범위와 누락·변환 규칙을 검증한다. 단순한 규모에서는 명시적 수동 매핑도 적절하다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 단방향 의존(DTO → Entity 변환은 OK, Entity → DTO import 는 금지): Entity 가 DTO 를 알면 도메인이 표현계층에 묶여
//   응답 모양이 바뀔 때마다 Entity 가 흔들린다.
// - Entity 직접 반환 3대 사고: ① password 등 내부 필드 노출 ② LAZY 프록시 직렬화 실패/N+1 ③ 양방향 연관 JSON 무한 루프.
// - 필드 1:1 금지 이유: 응답엔 필요한 것만(보안·페이로드). departmentName 처럼 연관을 평탄화해 클라이언트가 쓰기 쉽게.
// - record 는 불변 + equals/hashCode/toString 자동 → 응답 DTO 에 적합. MapStruct 는 DTO 수가 많고 변환이 단순 반복일 때.
