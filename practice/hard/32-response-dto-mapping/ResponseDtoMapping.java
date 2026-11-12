// 중학생 눈높이: 화면에 보낼 정보
// DB의 자료에서 화면에 필요한 값만 골라 응답용 DTO에 옮깁니다.
// Entity는 저장용 카드, 응답 DTO는 공개할 정보만 담은 메모라고 생각하면 됩니다.
// 원본 자료 읽기 → 보낼 항목 고르기 → 응답 메모 만들기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: dto/*Response.java
// 목표: Entity ↔ Response DTO 매핑을 거의 백지에서. TRD 3.16.3.
// 막히면 starter/32-response-dto-mapping, answers.md 32장 참고.
// 원칙: 응답 DTO 는 Entity 를 import 해도 되지만, Entity 가 DTO 를 import 하면 안 된다(단방향).

// 초보자 안내 (32장): 응답 DTO 매핑
// 개념: 매핑은 Entity에서 외부에 공개할 필드만 골라 응답 객체로 옮기는 작업입니다.
// 사용 이유: 비밀번호와 내부 관계를 숨기고 DB 구조 변경이 API에 바로 퍼지는 것을 줄입니다.
// 동작 흐름: from 메서드가 Entity 값을 record의 필드로 옮기고 Page.map이 목록 항목마다 이를 적용합니다. 지연 로딩 관계를 읽는 시점과 쿼리 수도 함께 확인합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

public record EmployeeResponse(
        Long employeeId, String name, String email, String employeeNumber,
        String position, String phone, LocalDate hireDate, String departmentName, EmployeeStatus status) {
    // TODO 01: Entity → DTO 정적 팩토리.
    public static EmployeeResponse ____(Employee e) {
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return new EmployeeResponse(
                e.getId(),
                e.getUser().____(),          // 이름
                e.getUser().getEmail(),
                e.getEmployeeNumber(),
                e.getPosition(),
                e.getPhone(),
                e.getHireDate(),
                e.getDepartment().____(),     // 부서명
                e.____()                      // 상태
        );
    }
}

public record LeaveResponse(
        Long leaveId, Long employeeId, String employeeName, LeaveType leaveType,
        LocalDate startDate, LocalDate endDate, String reason, ApprovalStatus status,
        Long approverId, String rejectReason, LocalDateTime createdAt) {
    public static LeaveResponse from(LeaveRequest l) {
        return new LeaveResponse(
                l.getId(), l.getEmployee().getId(),
                // TODO 02: employee → user → name.
                l.getEmployee().getUser().____(),
                l.getLeaveType(), l.getStartDate(), l.getEndDate(), l.getReason(),
                l.getStatus(), l.getApproverId(), l.getRejectReason(), l.getCreatedAt());
    }
}

public final class PageMapper {
    private PageMapper() {}
    public static <E, R> Page<R> map(Page<E> page, Function<E, R> mapper) {
        // TODO 03: Page<E> → Page<R> API.
        return page.____(mapper);
    }
}

// 안티 패턴(직접 한 줄씩 적기):
// (A) Controller 에서 Entity 직접 반환 → ____
// (B) Entity 에 @JsonIgnore 덕지덕지 → ____
// (C) 같은 DTO 를 요청/응답 공용 → ____

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
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ from 팩토리  □ getUser().getName()  □ getDepartment().getName()  □ getStatus()  □ page.map(mapper)
