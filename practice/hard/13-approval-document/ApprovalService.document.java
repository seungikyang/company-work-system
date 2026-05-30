// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 작성(DRAFT) → 요청(PENDING)을 거의 백지에서. PRD 2.4.3, 2.5.6, TRD 3.8.4.
// 막히면 starter/13-approval-document, answers.md 13장 참고.

@____
public ApprovalResponse createDraft(Long currentEmployeeId, ApprovalCreateRequest request) {

    // TODO 01: 작성자=결재자 금지.
    if (currentEmployeeId.____(request.getApproverId())) {
        throw new BusinessException(ErrorCode.____, "작성자와 결재자는 같을 수 없습니다.");
    }

    // TODO 02: 결재자 존재 + ACTIVE 확인.
    ____ approver = employeeRepository.findById(request.getApproverId())
        .orElseThrow(() -> new BusinessException(ErrorCode.____));
    if (!approver.____()) {
        throw new BusinessException(ErrorCode.INVALID_STATUS, "결재자는 재직 중이어야 합니다.");
    }

    ApprovalDocument doc = ApprovalDocument.createDraft(
        currentEmployeeId, request.getApproverId(),
        request.getTitle().trim(), request.getContent()
    );
    return ApprovalResponse.from(approvalRepository.save(doc));
}

@____
public ApprovalResponse submit(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 03: 본인 작성 문서만 요청.
    if (!doc.getWriterId().____(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 04: 상태 전이는 도메인 메서드.
    doc.____();

    return ApprovalResponse.from(doc);
}

// 학습 질문 (직접 답):
// Q1. 작성과 결재 요청을 분리하는 이유(임시저장)는?
//     A:
// Q2. 결재자가 존재하지 않는 ID 라면 어떤 에러?
//     A:
// Q3. doc.submit() 의 상태 검증을 Service if 로 바꾸면 단점은?
//     A:

// 자가 채점:
// □ 두 메서드 @Transactional  □ equals/INVALID_INPUT  □ APPROVAL_NOT_FOUND  □ EMPLOYEE_NOT_FOUND
// □ approver.isActive()  □ writerId.equals(current)/ACCESS_DENIED  □ doc.submit()
