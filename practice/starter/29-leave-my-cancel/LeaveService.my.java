// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 — 내 목록 / 상세 / 취소 흐름을 채우세요. PRD/TRD 2.5.4 (FR-LEAVE-002~004), 3.7.4 참고.

@Transactional(readOnly = true)
public Page<LeaveResponse> findMyLeaves(Long currentEmployeeId, Pageable pageable) {

    // TODO 01: 내 휴가만 보여주려면 어떤 Repository 메서드를 호출해야 하나요?
    Page<LeaveRequest> page = leaveRepository.____(currentEmployeeId, pageable);

    return page.map(LeaveResponse::from);
}

@Transactional(readOnly = true)
public LeaveResponse detail(Long currentEmployeeId, Long leaveId) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 02: 본인 휴가만 상세 조회 가능. 관리자는 별도 API 로 분리하거나 role 인자를 함께 받아 분기.
    if (!leave.getEmployee().getId().equals(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.____);
    }

    return LeaveResponse.from(leave);
}

@Transactional
public LeaveResponse cancel(Long currentEmployeeId, Long leaveId) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 03: 본인 신청만 취소 가능.
    if (!leave.getEmployee().getId().equals(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // TODO 04: 도메인 메서드에서 상태 검증 + 전이를 캡슐화.
    //          이미 승인된 휴가를 본인이 취소할 수 있게 두면 어떤 문제가 생기나?
    leave.____();

    return LeaveResponse.from(leave);
}

// ===== 관리자용 목록 =====

@Transactional(readOnly = true)
public Page<LeaveResponse> findForAdmin(ApprovalStatus status, Long employeeId, Pageable pageable) {
    // TODO 05: 조건이 모두 null 이면 전체, 일부만 있으면 동적으로 조건 적용.
    //          학습 단계에서는 if 분기 또는 @Query 두 개로 시작합니다.

    if (status != null && employeeId != null) {
        return leaveRepository.findByStatusAndEmployee_Id(status, employeeId, pageable)
                .map(LeaveResponse::from);
    } else if (status != null) {
        return leaveRepository.findByStatus(status, pageable).map(LeaveResponse::from);
    } else if (employeeId != null) {
        return leaveRepository.findByEmployee_Id(employeeId, pageable).map(LeaveResponse::from);
    } else {
        return leaveRepository.findAll(pageable).map(LeaveResponse::from);
    }
}

// 학습 질문:
// Q1. 내 휴가만 보여주는 안전망을 Service 에서만 두면 충분한가? Repository 쿼리 차원의 보호는 필요한가?
//     A:
// Q2. 관리자 목록에서 status + employeeId 외에 기간(from/to) 조건도 자주 요구된다. 어떻게 확장할까?
//     A:
// Q3. 'CANCELED' 라는 별도 상태를 두는 편이 'REJECTED' 로 합치는 것보다 좋은 점은?
//     A:
