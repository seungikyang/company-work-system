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

// 학습 질문 (직접 답):
// Q1. Entity 직접 응답의 보안/직렬화 문제 3가지.
//     A1:  A2:  A3:
// Q2. record vs class DTO 차이 한 줄.
//     A:
// Q3. Entity 의 모든 필드를 1:1 로 옮기지 않는 이유.
//     A:

// 자가 채점:
// □ from 팩토리  □ getUser().getName()  □ getDepartment().getName()  □ getStatus()  □ page.map(mapper)
