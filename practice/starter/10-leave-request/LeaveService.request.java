// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 신청 흐름을 채우세요. PRD 2.4.2, 2.5.4, TRD 3.8.2 참고.

@Transactional
public LeaveResponse request(Long currentEmployeeId, LeaveCreateRequest request) {

    // TODO 01: 신청 직원이 실제로 존재하는지 확인.
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
