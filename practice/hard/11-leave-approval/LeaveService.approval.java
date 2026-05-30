// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 승인/반려를 거의 백지에서. TRD 3.8.2, 3.10.2, 3.16.1.
// 막히면 starter/11-leave-approval, answers.md 11장 참고.

@____
public LeaveResponse approve(Long approverUserId, Long leaveId) {

    // TODO 01: 휴가 조회. 없으면?
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 상태/승인자 처리를 도메인 메서드에 캡슐화(Service 가 직접 setStatus 하지 않는다).
    leave.____(approverUserId);

    // saveAndFlush 불필요 — dirty checking 으로 반영.
    return LeaveResponse.from(leave);
}

@____
public LeaveResponse reject(Long approverUserId, Long leaveId, LeaveRejectRequest request) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 03: 반려 사유 비면 차단(Service 2차 방어).
    if (request.getRejectReason() == null || request.getRejectReason().____()) {
        throw new BusinessException(ErrorCode.____, "반려 사유는 필수입니다.");
    }

    // TODO 04: 반려 처리(도메인 메서드).
    leave.____(approverUserId, request.getRejectReason());

    return LeaveResponse.from(leave);
}

// 학습 질문 (직접 답):
// Q1. PENDING 검증을 Entity.approve 가 아닌 Service 에서만 했다면 어떤 버그가 가능?
//     A:
// Q2. 두 관리자가 동시에 같은 휴가 승인을 시도하면?
//     A:
// Q3. 승인 후 알림은 어디서 트리거하는 게 안전?
//     A:

// 자가 채점:
// □ 두 메서드 @Transactional  □ LEAVE_NOT_FOUND  □ leave.approve(approverId)
// □ rejectReason isBlank → INVALID_INPUT  □ leave.reject(approverId, reason)  □ 낙관/비관 락 인지
