// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 신청 흐름을 거의 백지에서. PRD 2.4.2, 2.5.4, TRD 3.8.2.
// 막히면 starter/10-leave-request, answers.md 10장 참고.

// 초보자 안내 (10장): 업무 규칙 검증
// 개념: 업무 규칙은 재직자만 신청 가능 같은 서비스 고유의 조건입니다.
// 사용 이유: 필수값 검사만으로는 잘못된 날짜 순서나 퇴사자의 신청을 걸러낼 수 없습니다.
// 동작 흐름: 직원 조회 → 재직 여부 확인 → 시작일·종료일 검사 → 휴가 생성·저장 → DTO 반환 순서입니다. 실패 조건에서는 예외를 던져 이후 저장으로 진행하지 않습니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
