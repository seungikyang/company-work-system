// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 승인/반려를 거의 백지에서. TRD 3.8.4, 3.10.3.
// 막히면 starter/14-approval-decision, answers.md 14장 참고.

@____
public ApprovalResponse approve(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 01: 도메인 메서드에서 결재자/상태 검증 + 승인일시 세팅까지.
    doc.____(currentEmployeeId);

    return ApprovalResponse.from(doc);
}

@____
public ApprovalResponse reject(Long currentEmployeeId, Long approvalId, ApprovalRejectRequest request) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 02: 반려 사유 비면 차단.
    if (request.getRejectReason() == null || request.getRejectReason().____()) {
        throw new BusinessException(ErrorCode.____, "반려 사유는 필수입니다.");
    }

    doc.reject(currentEmployeeId, request.getRejectReason());
    return ApprovalResponse.from(doc);
}

// 학습 질문 (직접 답):
// Q1. 도메인 메서드 안에서 검증을 다 하면 Service 가 가벼워지는데, 단점은?
//     A:
// Q2. 결재자가 아닌 사람이 승인하려고 하면 어디서 어떻게 막히나?
//     A:
// Q3. approvedAt 을 LocalDateTime.now() 대신 Clock 주입하면 이점은?
//     A:

// 자가 채점:
// □ 두 메서드 @Transactional  □ APPROVAL_NOT_FOUND  □ doc.approve(current)
// □ rejectReason isBlank → INVALID_INPUT  □ 권한(APPROVER/ADMIN)은 Service, 본인 여부는 Entity
