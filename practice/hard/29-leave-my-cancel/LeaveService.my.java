// 실제 구현 위치 예: service/LeaveService.java
// 목표: 휴가 내 목록/상세/취소 + 관리자 목록을 거의 백지에서. PRD 2.5.4 (FR-LEAVE-002~004, 008).
// 막히면 starter/29-leave-my-cancel, answers.md 29장 참고.

// 초보자 안내 (29장): 소유자 검증
// 개념: 소유자 검증은 요청한 자료가 현재 사용자에게 허용된 자료인지 확인하는 일입니다.
// 사용 이유: 휴가 ID만 바꿔 다른 사람의 신청을 보거나 취소하는 일을 막습니다.
// 동작 흐름: 내 목록 쿼리에는 현재 직원 ID를 넣고 상세·취소에서도 소유자 또는 허용 역할을 확인합니다. 취소는 소유권 검사 후 도메인 메서드로 상태 규칙을 적용합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional(readOnly = true)
public Page<LeaveResponse> findMyLeaves(Long currentEmployeeId, Pageable pageable) {
    // TODO 01: 내 휴가만(IDOR — 쿼리에 employeeId 조건).
    Page<LeaveRequest> page = leaveRepository.____(currentEmployeeId, pageable);
    return page.map(LeaveResponse::from);
}

@Transactional(readOnly = true)
public LeaveResponse detail(Long currentEmployeeId, UserRole currentRole, Long leaveId) {
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));
    // TODO 02: 본인 또는 ADMIN 만.
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
    // TODO 03: 본인 신청만.
    if (!leave.getEmployee().getId().____(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }
    // TODO 04: 상태 검증 + 전이는 도메인 메서드.
    leave.____();
    return LeaveResponse.from(leave);
}

@Transactional(readOnly = true)
public Page<LeaveResponse> findForAdmin(ApprovalStatus status, Long employeeId, Pageable pageable) {
    // TODO 05: 조건 조합별 분기.
    if (status != null && employeeId != null) {
        return leaveRepository.findByStatusAndEmployee_Id(status, employeeId, pageable).map(LeaveResponse::from);
    } else if (status != null) {
        return leaveRepository.findByStatus(status, pageable).map(LeaveResponse::from);
    } else if (employeeId != null) {
        return leaveRepository.findByEmployee_Id(employeeId, pageable).map(LeaveResponse::from);
    } else {
        return leaveRepository.____(pageable).map(LeaveResponse::from);
    }
}

// 학습 질문 (직접 답):
// Q1. 내 휴가만 보여주는 안전망을 Service 에만 두면 충분한가? Repository 차원 보호는?
//     A:
// Q2. 관리자 목록에 기간(from/to) 조건도 자주 필요하다. 어떻게 확장?
//     A:
// Q3. CANCELED 별도 상태가 REJECTED 합치기보다 좋은 점은?
//     A:

// 자가 채점:
// □ findByEmployee_Id  □ UserRole.ADMIN  □ ACCESS_DENIED  □ leave.cancelByOwner()  □ findAll(pageable)
