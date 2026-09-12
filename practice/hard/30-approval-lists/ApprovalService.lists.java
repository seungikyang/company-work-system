// 실제 구현 위치 예: service/ApprovalService.java
// 목표: 결재 내 목록/대기 목록/상세 권한을 거의 백지에서. PRD 2.5.6 (FR-APPROVAL-003/004).
// 막히면 starter/30-approval-lists, answers.md 30장 참고.

// 초보자 안내 (30장): 조건별 결재 조회
// 개념: 같은 결재 테이블도 작성자·결재자·상태에 따라 다른 목록이 됩니다.
// 사용 이유: 내가 쓴 문서와 내가 처리할 문서를 구분하고 타인의 문서 노출을 막습니다.
// 동작 흐름: my는 작성자 조건, pending은 결재자와 대기 상태 조건으로 조회합니다. 상세에서도 작성자·결재자·관리자 여부를 검사한 뒤 DTO로 변환합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
