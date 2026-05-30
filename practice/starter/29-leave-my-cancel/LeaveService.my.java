// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 — 내 목록 / 상세 / 취소 + 관리자 목록 흐름을 채우세요. PRD 2.5.4 (FR-LEAVE-002~004, FR-LEAVE-008), TRD 3.7.4 참고.

@Transactional(readOnly = true)
public Page<LeaveResponse> findMyLeaves(Long currentEmployeeId, Pageable pageable) {

    // TODO 01: 내 휴가만 보여주려면 어떤 Repository 메서드를 호출해야 하나요?
    Page<LeaveRequest> page = leaveRepository.____(currentEmployeeId, pageable);

    return page.map(LeaveResponse::from);
}

@Transactional(readOnly = true)
public LeaveResponse detail(Long currentEmployeeId, UserRole currentRole, Long leaveId) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 02: 본인 또는 ADMIN 만 상세 조회 가능.
    boolean isOwner = leave.getEmployee().getId().equals(currentEmployeeId);
    boolean isAdmin = currentRole == UserRole.____;
    if (!isOwner && !isAdmin) {
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
//
// 심화 노트 (면접 답변 포인트):
// - IDOR 이중 방어: ① Service 에서 소유자/ADMIN 검증 ② Repository 쿼리에 employeeId 조건(findByEmployee_Id).
//   URL 의 leaveId 만 믿고 findById 후 반환하면 남의 휴가가 새어나간다.
// - findForAdmin 의 if 분기는 조건 조합(status/employeeId)을 다룬다. 기간(from/to)까지 늘면 조합 폭발 →
//   Querydsl/Specification 으로 동적 쿼리 1개로 합친다.
// - cancelByOwner() 는 본인+PENDING 만 허용. 승인된 휴가를 취소 가능하게 두면 이미 반영된 일정/통계가 깨진다.
// - CANCELED 별도 상태 = "본인 취소" 와 "관리자 반려" 를 통계/이력에서 구분.
