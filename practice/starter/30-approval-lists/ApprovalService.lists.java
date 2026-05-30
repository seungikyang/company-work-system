// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 — 내 목록 / 승인 대기 목록 / 상세. PRD 2.5.6 (FR-APPROVAL-003/004), TRD 3.7.6 참고.

@Transactional(readOnly = true)
public Page<ApprovalResponse> findMyDocuments(Long currentEmployeeId, ApprovalStatus status, Pageable pageable) {

    Page<ApprovalDocument> page;
    if (status == null) {
        // TODO 01: 내가 작성한 결재 문서 전체.
        page = approvalRepository.____(currentEmployeeId, pageable);
    } else {
        page = approvalRepository.findByWriterIdAndStatus(currentEmployeeId, status, pageable);
    }
    return page.map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public Page<ApprovalResponse> findPendingForApprover(Long currentEmployeeId, Pageable pageable) {

    // TODO 02: "내가 결재해야 할" + "PENDING" 두 조건을 모두 만족해야 합니다.
    return approvalRepository.findByApproverIdAndStatus(currentEmployeeId, ApprovalStatus.____, pageable)
            .map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public ApprovalResponse detail(Long currentEmployeeId, UserRole currentRole, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 03: 작성자도 결재자도 아니고 ADMIN 도 아닌 사람이 접근하면 차단.
    boolean isWriter   = doc.getWriterId().equals(currentEmployeeId);
    boolean isApprover = doc.getApproverId().equals(currentEmployeeId);
    boolean isAdmin    = currentRole == UserRole.____;
    if (!isWriter && !isApprover && !isAdmin) {
        throw new BusinessException(ErrorCode.____);
    }

    return ApprovalResponse.from(doc);
}

// 학습 질문:
// Q1. /my 와 /pending 을 같은 메서드에서 분기하지 않고 분리한 이유는?
//     A:
// Q2. 결재자 변경(권한 위임)이 가능해진다면 ‘승인 대기 목록’ 조회 쿼리는 어떻게 바뀌어야 하나?
//     A:
// Q3. 관리자에게 모든 문서를 보여줄 때, Service 시그니처를 어떻게 두는 게 깔끔할까?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - /my(writerId 조건) vs /pending(approverId + status=PENDING) — 쿼리 조건이 달라 한 메서드에서 분기하지 않고 분리.
// - detail 권한: writer/approver/ADMIN 셋 다 아니면 ACCESS_DENIED(403). @PreAuthorize 의 hasAnyRole 을 통과한 APPROVER 라도
//   "남의 결재 문서" 면 차단된다 → "역할" 과 "소유/담당" 은 다른 검사.
// - 권한 위임(approverId 변경)이 생기면 pending 은 현재 approverId 기준이라 위임 즉시 새 결재자에게 노출된다.
// - 관리자 전체 조회는 시그니처에 UserRole 을 받아 ADMIN 분기를 명시하는 편이 읽기 쉽다.
