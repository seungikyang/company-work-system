// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 작성(DRAFT) → 요청(PENDING)을 거의 백지에서. PRD 2.4.3, 2.5.6, TRD 3.8.4.
// 막히면 starter/13-approval-document, answers.md 13장 참고.

// 초보자 안내 (13장): 작성과 제출 분리
// 개념: 임시 저장과 제출은 다른 업무 동작이며 서로 다른 상태를 만듭니다.
// 사용 이유: 작성 중인 문서가 결재자의 처리 목록에 바로 섞이지 않게 합니다.
// 동작 흐름: createDraft는 작성자·결재자 조건을 확인하고 DRAFT로 저장합니다. submit은 문서를 조회하고 작성자를 확인한 뒤 도메인 메서드로 제출 상태로 바꿉니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
