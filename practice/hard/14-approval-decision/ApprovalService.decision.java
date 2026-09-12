// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 승인/반려를 거의 백지에서. TRD 3.8.4, 3.10.3.
// 막히면 starter/14-approval-decision, answers.md 14장 참고.

// 초보자 안내 (14장): 행위자 검증과 상태 변경
// 개념: 행위자는 지금 승인·반려를 요청한 사람입니다.
// 사용 이유: 로그인만 한 사람이 다른 사람에게 지정된 결재를 처리하지 못하게 합니다.
// 동작 흐름: 문서를 조회하고 도메인 메서드에서 지정 결재자와 현재 상태를 검사합니다. 반려는 사유도 확인하며, 성공하면 상태와 처리 정보를 함께 변경합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
