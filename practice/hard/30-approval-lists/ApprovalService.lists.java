// 실제 구현 위치 예: service/ApprovalService.java
// 목표: 결재 내 목록/대기 목록/상세 권한을 거의 백지에서. PRD 2.5.6 (FR-APPROVAL-003/004).
// 막히면 starter/30-approval-lists, answers.md 30장 참고.

@Transactional(readOnly = true)
public Page<ApprovalResponse> findMyDocuments(Long currentEmployeeId, ApprovalStatus status, Pageable pageable) {
    Page<ApprovalDocument> page;
    if (status == null) {
        // TODO 01: 내가 작성한 전체.
        page = approvalRepository.____(currentEmployeeId, pageable);
    } else {
        page = approvalRepository.findByWriterIdAndStatus(currentEmployeeId, status, pageable);
    }
    return page.map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public Page<ApprovalResponse> findPendingForApprover(Long currentEmployeeId, Pageable pageable) {
    // TODO 02: "내가 결재할" + "PENDING".
    return approvalRepository.findByApproverIdAndStatus(currentEmployeeId, ApprovalStatus.____, pageable)
            .map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public ApprovalResponse detail(Long currentEmployeeId, UserRole currentRole, Long approvalId) {
    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));
    // TODO 03: 작성자/결재자/ADMIN 아니면 차단.
    boolean isWriter   = doc.getWriterId().equals(currentEmployeeId);
    boolean isApprover = doc.getApproverId().equals(currentEmployeeId);
    boolean isAdmin    = currentRole == UserRole.____;
    if (!isWriter && !isApprover && !isAdmin) {
        throw new BusinessException(ErrorCode.____);
    }
    return ApprovalResponse.from(doc);
}

// 학습 질문 (직접 답):
// Q1. /my 와 /pending 을 한 메서드에서 분기하지 않고 분리한 이유는?
//     A:
// Q2. 결재자 변경(권한 위임)이 가능해지면 pending 쿼리는 어떻게 바뀌나?
//     A:
// Q3. 관리자에게 모든 문서를 보여줄 때 Service 시그니처를 어떻게?
//     A:

// 자가 채점:
// □ findByWriterId  □ ApprovalStatus.PENDING  □ UserRole.ADMIN  □ ACCESS_DENIED
// □ 역할(@PreAuthorize) 통과해도 소유/담당은 Service 가 검증
