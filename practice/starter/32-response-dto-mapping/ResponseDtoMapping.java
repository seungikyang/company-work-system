// 실제 구현 위치 예: src/main/java/com/example/companywork/dto/*Response.java
// 목표: Entity ↔ Response DTO 매핑 패턴을 채우세요. TRD 3.16.3 (Entity 직접 반환 문제) 참고.

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
    public static EmployeeResponse from(Employee e) {
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
// 5. 학습 질문
// =====================================================================
// Q1. Entity 를 그대로 응답하면 발생할 수 있는 보안 / 직렬화 문제 3 가지를 적어 보세요.
//     A1:
//     A2:
//     A3:
//
// Q2. record 와 일반 class DTO 의 차이를 한 줄로 설명해 보세요.
//     A:
//
// Q3. 응답 DTO 를 만들 때 Entity 의 모든 필드를 1:1 로 옮기지 않는 이유는?
//     A:
//
// Q4. MapStruct / ModelMapper 같은 자동 매핑 도구를 도입할 만한 시점은?
//     A:
