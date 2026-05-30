// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 신청 흐름을 거의 백지에서. PRD 2.4.2, 2.5.4, TRD 3.8.2.
// 막히면 starter/10-leave-request, answers.md 10장 참고.

@____
public LeaveResponse request(Long currentEmployeeId, LeaveCreateRequest request) {

    // TODO 01: 신청 직원 존재 확인.
    ____ employee = employeeRepository.findById(currentEmployeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 퇴사 직원 차단.
    if (!employee.____()) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 03: 날짜 검증(Service 1차).
    if (request.getStartDate() == null || request.getEndDate() == null
            || request.getStartDate().____(request.getEndDate())) {
        throw new BusinessException(ErrorCode.____);
    }

    LeaveRequest entity = LeaveRequest.create(
        employee, request.getLeaveType(),
        request.getStartDate(), request.getEndDate(), request.getReason()
    );
    LeaveRequest saved = leaveRepository.save(entity);

    // TODO 04: DTO 변환.
    return LeaveResponse.____(saved);
}

// 학습 질문 (직접 답):
// Q1. 시작일>종료일 검증을 DTO @AssertTrue 로 옮기면 장점은?
//     A:
// Q2. 같은 기간 PENDING/APPROVED 휴가가 이미 있으면 막아야 하나? 조건식은?
//     A:
// Q3. 신청 알림은 트랜잭션 안/밖 중 어디서?
//     A:

// 자가 채점:
// □ @Transactional  □ EMPLOYEE_NOT_FOUND  □ isActive()/ACCESS_DENIED  □ isAfter()/INVALID_DATE_RANGE
// □ LeaveResponse.from  □ 알림은 AFTER_COMMIT
