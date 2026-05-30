// 실제 구현 위치 예: dto/*Response.java
// 목표: Entity ↔ Response DTO 매핑을 거의 백지에서. TRD 3.16.3.
// 막히면 starter/32-response-dto-mapping, answers.md 32장 참고.
// 원칙: 응답 DTO 는 Entity 를 import 해도 되지만, Entity 가 DTO 를 import 하면 안 된다(단방향).

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
