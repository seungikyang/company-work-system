// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 신청 흐름을 채우세요. PRD 2.4.2, 2.5.4, TRD 3.8.2 참고.

// 초보자 안내 (10장): 업무 규칙 검증
// 개념: 업무 규칙은 재직자만 신청 가능 같은 서비스 고유의 조건입니다.
// 사용 이유: 필수값 검사만으로는 잘못된 날짜 순서나 퇴사자의 신청을 걸러낼 수 없습니다.
// 동작 흐름: 직원 조회 → 재직 여부 확인 → 시작일·종료일 검사 → 휴가 생성·저장 → DTO 반환 순서입니다. 실패 조건에서는 예외를 던져 이후 저장으로 진행하지 않습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional
public LeaveResponse request(Long currentEmployeeId, LeaveCreateRequest request) {

    // TODO 01: 신청 직원이 실제로 존재하는지 확인.
    // currentEmployeeId는 인증한 계정에서 확인한 직원 ID입니다. 요청자가 body에 적은 ID를 그대로 믿으면
    // 다른 사람 이름으로 신청할 수 있으므로 19·34장의 사용자 주입과 연결하세요.
    Employee employee = employeeRepository.findById(currentEmployeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 퇴사한 직원은 휴가 신청 불가. 어떤 ErrorCode 가 자연스러울까요?
    if (!employee.____()) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 03: 시작일/종료일 검증.
    //          Entity 의 create 안에서도 검증하지만, Service 에서 미리 막아 일관된 에러로 반환합니다.
    if (request.getStartDate() == null || request.getEndDate() == null
            || request.getStartDate().isAfter(request.getEndDate())) {
        throw new BusinessException(ErrorCode.____);
    }

    LeaveRequest entity = LeaveRequest.create(
        employee,
        request.getLeaveType(),
        request.getStartDate(),
        request.getEndDate(),
        request.getReason()
    );

    LeaveRequest saved = leaveRepository.save(entity);

    // TODO 04: 응답은 DTO 로 변환.
    return LeaveResponse.____(saved);
}

// 학습 질문:
// Q1. 시작일 > 종료일 검증을 DTO 의 @AssertTrue 로 옮기면 어떤 장점이 있을까요?
//     A:
// Q2. 같은 기간의 휴가가 이미 PENDING/APPROVED 상태로 있다면 새 신청을 막아야 할까요?
//     A:
// Q3. 신청 직후 알림을 보내야 한다면, 트랜잭션 안에서 보낼지 밖에서 보낼지?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - 날짜 검증 다층: DTO @AssertTrue(즉시 400) → Entity create(불변식) → Service(INVALID_DATE_RANGE 일관 변환).
// - 기간 겹침(확장): 같은 직원의 PENDING/APPROVED 기간과 겹치면 차단 — startDate <= reqEnd AND endDate >= reqStart.
// - 알림은 커밋 후(@TransactionalEventListener AFTER_COMMIT). 트랜잭션 안에서 보내면 롤백돼도 알림이 이미 나가는 모순.
// - 퇴사 직원 차단은 employee.isActive() 로 도메인에 캡슐화 → false 면 ACCESS_DENIED.
